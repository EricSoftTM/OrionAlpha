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
package network.security;

import util.Rand32;
import util.Utilities;

/**
 * Provides a class for encrypting KMS packets with XOR Encryption.
 * 
 * @author Eric
 */
public class XORCipher {
    private int seqSnd;
    private int seqRcv;
    
    public XORCipher(int seqSnd, int seqRcv) {
        this.seqSnd = seqSnd;
        this.seqRcv = seqRcv;
    }
    
    public int getSeqSnd() {
        return seqSnd;
    }
    
    public int getSeqRcv() {
        return seqRcv;
    }
    
    public byte[] decrypt(byte[] buffer) {
        byte iv = (byte) (Utilities.getBytes(seqRcv)[0] & 0xFF);
        
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) ((byte) (16 * (iv ^ (buffer[i] & 0xFF))) | (byte) (((iv ^ (buffer[i] & 0xFF)) >> 4) & 0xFF0F0F0F));
        }
        
        return buffer;
    }
    
    public byte[] encrypt(byte[] buffer) {
        byte iv = (byte) (Utilities.getBytes(seqSnd)[0] & 0xFF);
        
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (iv ^ (byte) (16 * (buffer[i] & 0xFF) | (byte) ((buffer[i] & 0xFF) >> 4)));
        }
        
        return buffer;
    }
    
    public void updateSeqSnd() {
        this.seqSnd = Rand32.crtRand(this.seqSnd);
    }
    
    public void updateSeqRcv() {
        this.seqRcv = Rand32.crtRand(this.seqRcv);
    }
    
    /**
     * Returns the IV of this instance as a string.
     * 
     * @return the send/recv initialization vectors
     */
    @Override
    public String toString() {
        return "Send IV: " + this.seqSnd + " | RecvIV: " + this.seqRcv;
    }
}
