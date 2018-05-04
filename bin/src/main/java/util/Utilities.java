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
package util;

/**
 *
 * @author Eric
 */
public class Utilities {
    
    /**
     * Returns the specified 32-bit signed integer value as an array of bytes.
     * 
     * @param val The value to convert
     * @return An array of bytes with length of 4
     */
    public static byte[] getBytes(int val) {
        byte[] arr = new byte[Integer.BYTES];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (byte) ((val >> (8 * i)) & 0xFF);
        }
        return arr;
    }
    
    public static short toInt16(byte[] buf, int idx) {
        return (short) (((buf[idx + 1] & 0xFF) << 8) | (buf[idx] & 0xFF));
    }
    
     public static int toInt32(byte[] buf, int idx) {
        return (((buf[3] & 0xFF) << 24) | ((buf[2] & 0xFF) << 16) | ((buf[1] & 0xFF) << 8) | (buf[0] & 0xFF));
    }
     
     /**
     * Converts a standard byte-array into a readable hex string AoB.
     * 
     * @param buf The buffer containing an array of bytes
     * @return A readable hex string 
     */
    public static String toHexString(byte[] buf) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            int b = buf[i] & 0xFF;
            if (b < 0x10)
                str.append('0');
            str.append(Integer.toHexString(b).toUpperCase());
            str.append(' ');
        }
        str.deleteCharAt(str.length() - 1);
        return str.toString();
    }
    
    public static final Long inet_aton(String inp) {
        Long dwIP = 0L;
        String[] aIP = inp.split("\\.");
        
        for (int i = 0; i < 4; i++) {
            dwIP += Long.parseLong(aIP[i]) << (i * 8);
        }
        return dwIP;
    }
    
    public static final String inet_ntoa(long netaddr) {
        return (netaddr & 0xFF) + "." + ((netaddr >> 8) & 0xFF) + "." + ((netaddr >> 16) & 0xFF) + "." + ((netaddr >> 24) & 0xFF);
    }
}
