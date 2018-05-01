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
import network.security.XORCipher;
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
    
    public InPacket() {
        this(BLOCK_SIZE);
    }
    
    public InPacket(int size) {
        this.recvBuff = Unpooled.buffer(size);
        this.clear();
    }
    
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
    
    private void clear() {
        this.state = 0;
        this.length = 0;
    }
    
    public void copyTo(OutPacket packet) {
        byte[] buff = recvBuff.array();
        byte[] sendBuff = new byte [buff.length];
        int offset = recvBuff.readerIndex();
        int size = buff.length - offset;
        System.arraycopy(buff, offset, sendBuff, 0, size);
        packet.encodeBuffer(sendBuff);
    }
    
    public boolean decodeBool() {
        return recvBuff.readBoolean();
    }
    
    public byte decodeByte() {
        return recvBuff.readByte();
    }
    
    public int decodeByte(boolean unsigned) {
        if (!unsigned) {
            return decodeByte();
        }
        return recvBuff.readUnsignedByte();
    }
    
    public short decodeShort() {
        return recvBuff.readShortLE();
    }
    
    public int decodeShort(boolean unsigned) {
        if (!unsigned) {
            return decodeShort();
        }
        return recvBuff.readUnsignedShortLE();
    }
    
    public int decodeInt() {
        return recvBuff.readIntLE();
    }
    
    public long decodeLong() {
        return recvBuff.readLongLE();
    }
    
    public float decodeFloat() {
        return recvBuff.readFloatLE();
    }
    
    public double decodeDouble() {
        return recvBuff.readDoubleLE();
    }
    
    public FileTime decodeFileTime(FileTime ft) {
        ft.setLowDateTime(decodeInt());
        ft.setHighDateTime(decodeInt());
        return ft;
    }
    
    public byte[] decodeBuffer(int size) {
        byte[] buff = new byte[size];
        recvBuff.readBytes(buff);
        return buff;
    }
    
    public int decodeSeqBase(int seqKey) {
        return ((seqKey >> 16) ^ rawSeq);
    }
    
    public String decodeString(int size) {
        byte[] arr = new byte[size];
        int i = 0;
        while (i < size) {
            arr[i++] = (byte) (decodeByte(true) & 0xFF);
        }
        return new String(arr, IN_ENCODING).replaceAll("\0", "");
    }
    
    public String decodeString() {
        return decodeString(decodeShort());
    }
    
    public boolean decryptData(XORCipher cipher) {
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
    
    public int getDataLen() {
        return dataLen;
    }
    
    public int getRawSeq() {
        return rawSeq;
    }
    
    public int getOffset() {
        return recvBuff.readerIndex();
    }
    
    public void rawAppendBuffer(ByteBuf buff, int size) {
        if (size + length > recvBuff.readableBytes()) {
            recvBuff.writeBytes(buff);
        }
        length += size;
    }
    
    public void setDataLen(int len) {
        this.dataLen = len;
    }
    
    @Override
    public String toString() {
        return dumpString();
    }
}
