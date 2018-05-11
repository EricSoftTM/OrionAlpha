/*
 * This file is part of OrionAlpha, a MapleStory Emulator Project.
 * Copyright (C) 2018 Eric Smith <notericsoft@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import network.security.XORCrypter;
import util.FileTime;
import util.Pointer;
import util.Utilities;

/**
 * CInPacket
 * 
 * @author Eric
 */
public class InPacket {
    private static final int BLOCK_SIZE = 0x10000;
    private static final Charset IN_ENCODING = Charset.forName("MS949");
    
    private final ByteBuf recvBuff;
    private int state;
    private int length;
    private int rawSeq;
    private int dataLen;
    
    /**
     * Instantiate a new instance of an InPacket to read in data sent from Client->Server.
     * 
     */
    public InPacket() {
        this(BLOCK_SIZE);
    }
    
    /**
     * Instantiate a new instance of an InPacket to read in data sent from Client->Server.
     * 
     * @param size The initial capacity of the packet buffer 
     */
    public InPacket(int size) {
        this.recvBuff = Unpooled.buffer(size);
        this.clear();
    }
    
    /**
     * Initializes this InPacket with the proper sequencing and data length.
     * After the initial state, this continues to append the remaining buffer's
     * raw data into this packet's buffer to be decrypted.
     * 
     * @param buff The raw packet buffer
     * @param lastState The previous state of the packet
     * 
     * @return The new state of the packet
     */
    public int appendBuffer(ByteBuf buff, Pointer<Integer> lastState) {
        final int HEADER = Short.BYTES + Short.BYTES;//uRawSeq + uDataLen
        
        if (lastState != null)
            lastState.set(state);
        int size = buff.readableBytes();
        if (state == 0) {
            int len = Math.min(size, HEADER - length);
            rawAppendBuffer(buff, len);
            if (size >= HEADER) {
                state = 1;
                rawSeq = decodeShort();
                dataLen = decodeShort();
                size -= len;
                if (size == 0)  {
                    return state;
                }
            }
        }
        int append = Math.min(size, dataLen + HEADER - length);
        rawAppendBuffer(buff, append);
        if (length >= dataLen + HEADER) {
            state = 2;
        }
        size -= append;
        return state;
    }
    
    /**
     * Reset the packet's current state and length
     * 
     */
    private void clear() {
        this.state = 0;
        this.length = 0;
    }
    
    /**
     * Copies the remaining data from this buffer and writes it to
     * an OutPacket buffer to be encoded.
     * 
     * @param packet The OutPacket buffer to encode to
     */
    public void copyTo(OutPacket packet) {
        byte[] buff = recvBuff.array();
        byte[] sendBuff = new byte [buff.length];
        int offset = recvBuff.readerIndex();
        int size = buff.length - offset;
        System.arraycopy(buff, offset, sendBuff, 0, size);
        packet.encodeBuffer(sendBuff);
    }
    
    /**
     * Decode a boolean received from the client.
     * 
     * @return The value of the decoded boolean
     */
    public boolean decodeBool() {
        return recvBuff.readBoolean();
    }
    
    /**
     * Decode a single byte received from the client.
     * 
     * @return The value of the decoded byte
     */
    public byte decodeByte() {
        return recvBuff.readByte();
    }
    
    /**
     * Decodes a single byte received from the client.
     * 
     * @param unsigned Whether or not this byte should be treated as unsigned
     * 
     * @return An integer representation of this decoded byte (or unsigned byte)
     */
    public int decodeByte(boolean unsigned) {
        if (!unsigned) {
            return decodeByte();
        }
        return recvBuff.readUnsignedByte();
    }
    
    /**
     * Decodes a 2-byte short received from the client.
     * 
     * @return The value of the decoded short
     */
    public short decodeShort() {
        return recvBuff.readShortLE();
    }
    
    /**
     * Decodes a 2-byte short received from the client.
     * 
     * @param unsigned Whether or not this short should be treated as unsigned
     * 
     * @return An integer representation of this decoded short (or unsigned short)
     */
    public int decodeShort(boolean unsigned) {
        if (!unsigned) {
            return decodeShort();
        }
        return recvBuff.readUnsignedShortLE();
    }
    
    /**
     * Decodes a 4-byte integer received from the client.
     * 
     * @return The value of the decoded integer
     */
    public int decodeInt() {
        return recvBuff.readIntLE();
    }
    
    /**
     * Decodes a 8-byte long received from the client.
     * 
     * @return The value of the decoded long
     */
    public long decodeLong() {
        return recvBuff.readLongLE();
    }
    
    /**
     * Decodes a 4-byte float.
     * This is not typically used in MapleStory.
     * 
     * @return The value of the decoded float
     */
    public float decodeFloat() {
        return recvBuff.readFloatLE();
    }
    
    /**
     * Decodes a 8-byte double.
     * 
     * @return The value of the decoded double
     */
    public double decodeDouble() {
        return recvBuff.readDoubleLE();
    }
    
    /**
     * Decodes a 8-byte FileTime buffer.
     * 
     * @param ft The FileTime object to assign the time to
     * 
     * @return The updated FileTime object with new timestamp
     */
    public FileTime decodeFileTime(FileTime ft) {
        ft.setLowDateTime(decodeInt());
        ft.setHighDateTime(decodeInt());
        return ft;
    }
    
    /**
     * Decodes a set amount of bytes into a byte-array.
     * 
     * @param size The amount of bytes to read in
     * 
     * @return The raw array of bytes that were decoded
     */
    public byte[] decodeBuffer(int size) {
        byte[] buff = new byte[size];
        recvBuff.readBytes(buff);
        return buff;
    }
    
    /**
     * Using the raw sequencing of this packet and the <code>seqKey</code> given,
     * this method will perform bitwise operation which will result in equaling
     * the sequence base (a.k.a the version) of the game.
     * 
     * This method is designed to validate if the packet is both correct, and that
     * the server's version matches the client's version.
     * 
     * @param seqKey The current sequencing key from the crypter
     * 
     * @return The sequence base (or version) of the game
     */
    public int decodeSeqBase(int seqKey) {
        return ((seqKey >> 16) ^ rawSeq);
    }
    
    /**
     * Decodes a string of characters with a given size.
     * MapleStory Strings are decoded as follows:
     *      [01 00] -> Size of the string
     *      [00] -> The character(s) in <code>IN_ENCODING</code>
     * 
     * @param size The fixed size of the string to read
     * 
     * @return The <code>IN_ENCODING</code> string that was decoded
     */
    public String decodeString(int size) {
        byte[] arr = new byte[size];
        int i = 0;
        while (i < size) {
            arr[i++] = (byte) (decodeByte(true) & 0xFF);
        }
        return new String(arr, IN_ENCODING).replaceAll("\0", "");
    }
    
    /**
     * Decodes a MapleStory string.
     * 
     * @see decodeString(int)
     * 
     * @return The string that was decoded
     */
    public String decodeString() {
        return decodeString(decodeShort());
    }
    
    /**
     * Decode bytes deemed unused or unnecessary, skipping them completely.
     * 
     * @param length The amount of bytes to skip
     */
    public void decodePadding(int length) {
        recvBuff.skipBytes(length);
    }
    
    /**
     * Decrypts the encrypted packet buffer using the client's XORCrypter.
     * 
     * @param cipher The crypter used to decrypt the buffer
     * 
     * @return If the block was both valid and decrypted
     */
    public boolean decryptData(XORCrypter cipher) {
        int remain = dataLen;
        int lenBlock = Math.min(remain, 0x5B0);
        if (remain != 0) {
            // Read bytes from the recv buffer offset +4 into
            // the source buffer to get the encrypted packet.
            byte[] src = new byte[remain];
            recvBuff.readBytes(src);
            
            // We will continue to use the buffer, so we
            // must reset its index before we re-write.
            recvBuff.resetWriterIndex();
            
            int src0 = 0;
            
            src = cipher.decrypt(src);
            src0 += lenBlock;
            
            while (true) {
                remain -= lenBlock;
                if (remain == 0)
                    break;
                lenBlock = Math.min(remain, 0x5B4);
                
                //src = cipher.decrypt(src);
                src0 += lenBlock;
            }
            
            // Re-write back the decrypted bytes back into
            // the receiving buffer to be decoded by OnPacket.
            recvBuff.writeBytes(src);
            // Reset the index back to it's proper position.
            // The remaining data is everything after the 
            // raw decoded packet header and data length.
            recvBuff.resetReaderIndex();
            return true;
        }
        return false;
    }
    
    /**
     * Constructs a readable string displaying the original bytes of the packet.
     * This will print both the overall buffer from start to finish, as well as
     * the remaining bytes of the buffer from the current offset to the end of the
     * buffer.
     * 
     * @return A readable string containing the bytes of this packet 
     */
    public String dumpString() {
        String dump = "";
        byte[] data = new byte[recvBuff.writerIndex()];
        recvBuff.getBytes(0, data);
        if (data.length - recvBuff.readerIndex() > 0) {
            byte[] remain = new byte[data.length - recvBuff.readerIndex()];
            System.arraycopy(data, recvBuff.readerIndex(), remain, 0, data.length - recvBuff.readerIndex());
            dump = Utilities.toHexString(remain);
        }
        return "\r\nAll: " + Utilities.toHexString(data) + "\r\nNow: " + dump;
    }
    
    /**
     * Retrieve the overall data length of this packet.
     * 
     * @return The length of the packet buffer
     */
    public int getDataLen() {
        return dataLen;
    }
    
    /**
     * Retrieve the raw sequence of this packet.
     * 
     * @return The sequence of this packet
     */
    public int getRawSeq() {
        return rawSeq;
    }
    
    /**
     * Retrieve the current index of the overall buffer's reader.
     * 
     * @return The current index of the reader
     */
    public int getOffset() {
        return recvBuff.readerIndex();
    }
    
    /**
     * Appends an entire buffer worth of bytes into this packet's buffer.
     * This will only append the data to the buffer if the new size exceeds
     * the current length of this packet.
     * In addition, this will increment the length of this packet by <code>size</code>.
     * 
     * @param buff A ByteBuf buffer containing the data to be appended
     * @param size The size of the buffer that is being appended
     */
    public void rawAppendBuffer(ByteBuf buff, int size) {
        if (size + length > recvBuff.readableBytes()) {
            recvBuff.writeBytes(buff);
        }
        length += size;
    }
    
    /**
     * Strictly assigns the overall length of this packet's buffer.
     * NOTE: This method should only ever be used in regards to JVM->JVM packets.
     * For instance, we use this for Game->Login and Login->Game networking.
     * 
     * @param len The length of this packet's raw buffer
     */
    public void setDataLen(int len) {
        this.dataLen = len;
    }
    
    /**
     * Dumps the packet data into a readable string.
     * 
     * @see dumpString()
     * @return A readable string displaying the bytes of the packet
     */
    @Override
    public String toString() {
        return dumpString();
    }
}
