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

import common.OrionConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import java.util.List;
import network.security.XORCipher;
import util.FileTime;
import util.Logger;
import util.Pointer;
import util.Utilities;

/**
 *
 * @author Eric
 */
public class OutPacket {
    private static final int DEFAULT_BUFFER_ALLOC = 0x100;
    private static final Charset OUT_ENCODING = Charset.forName("MS949");
    
    private final ByteBuf sendBuff;
    
    public OutPacket(int type) {
        this(type, DEFAULT_BUFFER_ALLOC);
    }
    
    public OutPacket (int type, int bufferAlloc) {
        this.sendBuff = Unpooled.buffer(bufferAlloc);
        this.init(type);
    }
    
    private void init(int type) {
        if (type != Integer.MAX_VALUE) {
            initByte(type);
        }
    }
    
    private void initByte(int type) {
        if (type != Integer.MAX_VALUE) {
            encodeByte(type);
        }
    }
    
    public int getOffset() {
        return sendBuff.writerIndex();
    }
    
    public int encodeSeqBase(int seqBase, Pointer<Integer> seqKey) {
        if (seqBase != 0) {
            seqBase = (short) ((((0xFFFF - seqBase) >> 8) & 0xFF) | (((0xFFFF - seqBase) << 8) & 0xFF00));
        }
        
        int uSeqKey = 0;
        if (seqKey != null)
            uSeqKey = seqKey.get();
        
        if (uSeqKey != 0) {
            // ((unsigned __int16)(*puSeqKey >> 16) >> 8) ^ uSeqBase;
            uSeqKey = (short) ((((uSeqKey >> 24) & 0xFF) | (((uSeqKey >> 16) << 8) & 0xFF00)) ^ seqBase);
        } else {
            uSeqKey = seqBase;
        }
        return uSeqKey;
    }
    
    public void makeBufferList(List<byte[]> l, int seqBase, XORCipher cipher) {
        // The source buffer to encrypt - do NOT encrypt directly;
        // all output of encrypted buffer should be in pDes, not pSrc.
        byte[] src = toArray();
        
        // The total remaining bytes to be written to the destination
        // buffer. We additionally encode the 4-byte TCP packet header.
        int remain = src.length + 4;
        
        // Calculate the length of the remaining buffer to form the
        // length of the block. 
        int lenBlock = Math.min(remain, 0x5B4);
        
        // Allocate a new destination block of empty bytes, used as the
        // new pDes buffer which will hold the encrypted packet.
        byte[] block = new byte[lenBlock];
        
        // pDes is a pointer to the pBlock buffer, however we use pDes
        // to store the current write offset of the destination buffer,
        // as the reason this is used is to get the current address of
        // the pDes pointer.
        int des = 0;
        // pDesEnd is a pointer to the end address of pDes (aka &pDes[uLenBlock]),
        // but the way we will use it is just to store uLenBlock which means the
        // end of the destination buffer. These two offsets are used to calculate
        // the total length of the packet to be encrypted.
        int desEnd = lenBlock;
        // pSrc0 is the pointer of pSrc, and holds the current address of pSrc.
        // This is used to know the current write offset of pSrc in case the
        // buffer exceeds block length and requires additional looping.
        int src0 = 0;
        
        // Encode the sequence base and write it to the first 2 bytes of the
        // TCP packet header.
        int rawSeq = encodeSeqBase(seqBase, new Pointer<>(cipher.getSeqSnd()));
        byte[] dest = new byte[4];
        //*(unsigned __int16 *)pDes = uRawSeq;
        dest[des++] = (byte) ((rawSeq >>> 8) & 0xFF);
        dest[des++] = (byte) (rawSeq & 0xFF);
        //pDes += 2;

        // Encode the encrypted data length and write it to the last 2 bytes
        // of the TCP packet header.
        int dataLen = (((src.length << 8) & 0xFF00) | (src.length >>> 8));
        //*(unsigned __int16 *)pDes = uDataLen;
        dest[des++] = (byte) ((dataLen >>> 8) & 0xFF);
        dest[des++] = (byte) (dataLen & 0xFF);
        //pDes += 2;
        
        // Encrypt the first block and append to the buffer list.
        System.arraycopy(src, src0, block, des, desEnd - des);
        block = cipher.encrypt(block);
        // Copy the packet header after encrypting block
        System.arraycopy(dest, 0, block, 0, dest.length);
        src0 += desEnd - des;
        l.add(block);
        
        // Encrypt remaining blocks and continue appending to buffer list.
        while (true) {
            remain -= lenBlock;
            if (remain == 0)
                break;
            lenBlock = Math.min(remain, 0x5B4);
            block = new byte[lenBlock];
            des = 0;
            desEnd = lenBlock;
            
            System.arraycopy(src, src0, block, des, desEnd - des);
            block = cipher.encrypt(block);
            
            System.arraycopy(dest, 0, block, 0, dest.length);
            
            src0 += desEnd - des;
            l.add(block);
        }
    }
    
    public void encodeBool(boolean b) {
        sendBuff.writeBoolean(b);
    }
    
    public void encodeByte(byte b) {
        sendBuff.writeByte(b);
    }
    
    public void encodeByte(int b) {
        sendBuff.writeByte(b);
    }
    
    public void encodeShort(short n) {
        sendBuff.writeShortLE(n);
    }
    
    public void encodeShort(int n) {
        sendBuff.writeShortLE(n);
    }
    
    public void encodeInt(int n) {
        sendBuff.writeIntLE(n);
    }
    
    public void encodeLong(long l) {
        sendBuff.writeLongLE(l);
    }
    
    public void encodeFloat(float f) {
        sendBuff.writeFloatLE(f);
    }
    
    public void encodeDouble(double d) {
        sendBuff.writeDoubleLE(d);
    }
    
    public void encodeString(String str) {
        byte[] src = str.getBytes(OUT_ENCODING);
        
        encodeShort(src.length);
        encodeBuffer(src);
    }
    
    public void encodeString(String str, int size) {
        byte[] src = str.getBytes(OUT_ENCODING);
        
        for (int i = 0; i < size; i++) {
            if (i >= src.length) {
                encodeByte('\0');
            } else {
                encodeByte(src[i]);
            }
        }
    }
    
    public void encodeFileTime(FileTime ft) {
        encodeInt(ft.getLowDateTime());
        encodeInt(ft.getHighDateTime());
    }
    
    public void encodeBuffer(byte[] buffer) {
        sendBuff.writeBytes(buffer);
    }
    
    public void encodePacket(InPacket packet) {
        packet.copyTo(this);
    }
    
    public void encodePadding(int count) {
        for (int i = 0; i < count; i++) {
            encodeByte(0);
        }
    }
    
    public byte[] toArray() {
        byte[] src = new byte[sendBuff.writerIndex()];
        sendBuff.readBytes(src);
        sendBuff.resetReaderIndex();
        return src;
    }
    
    public String dumpString() {
        return Utilities.toHexString(sendBuff.array());
    }
    
    @Override
    public String toString() {
        return dumpString();
    }
}
