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
 * The crypter class to encrypt/decrypt KMS Alpha packets.
 * This crypto is different compared to the rest of the MapleStory clients.
 * 
 * @author Eric
 */
public class XORCrypter {
    private int seqSnd;
    private int seqRcv;
    
    public XORCrypter(int seqSnd, int seqRcv) {
        this.seqSnd = seqSnd;
        this.seqRcv = seqRcv;
    }
    
    /**
     * Retrieves the current send sequencing of this crypter.
     * 
     * @return The value of the send sequence
     */
    public int getSeqSnd() {
        return seqSnd;
    }
    
    /**
     * Retrieves the current receive sequencing of this crypter.
     * 
     * @return The value of the receive sequence
     */
    public int getSeqRcv() {
        return seqRcv;
    }
    
    /**
     * Decrypts the raw buffer using this crypter's receive sequencing.
     * 
     * @param buffer The encrypted buffer to decrypt
     * 
     * @return The decrypted buffer
     */
    public byte[] decrypt(byte[] buffer) {
        byte iv = (byte) (Utilities.getBytes(seqRcv)[0] & 0xFF);
        
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) ((byte) (16 * (iv ^ (buffer[i] & 0xFF))) | (byte) (((iv ^ (buffer[i] & 0xFF)) >> 4) & 0xFF0F0F0F));
        }
        
        return buffer;
    }
    
    /**
     * Encrypts the raw buffer using this crypter's send sequencing.
     * 
     * @param buffer The decrypted buffer to encrypt
     * 
     * @return The encrypted buffer
     */
    public byte[] encrypt(byte[] buffer) {
        byte iv = (byte) (Utilities.getBytes(seqSnd)[0] & 0xFF);
        
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (iv ^ (byte) (16 * (buffer[i] & 0xFF) | (byte) ((buffer[i] & 0xFF) >> 4)));
        }
        
        return buffer;
    }
    
    /**
     * Updates this crypter's send sequence to sync with the client.
     * 
     */
    public void updateSeqSnd() {
        this.seqSnd = Rand32.crtRand(this.seqSnd);
    }
    
    /**
     * Updates this crypter's receive sequence to sync with the client.
     * 
     */
    public void updateSeqRcv() {
        this.seqRcv = Rand32.crtRand(this.seqRcv);
    }
    
    /**
     * Returns a readable string of the IV's for this crypter's instance.
     * 
     * @return The send/receive initialization vectors
     */
    @Override
    public String toString() {
        return "Send IV: " + this.seqSnd + " | RecvIV: " + this.seqRcv;
    }
}
