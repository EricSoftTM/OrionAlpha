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
    
    /**
     * Converts an IP from a string to an integer.
     * e.g "127.0.0.1" -> 16777343
     * 
     * @param addr The string representation of the IP
     * 
     * @return The value of the IP as an integer
     */
    public static final int netIPToInt32(String addr) {
        if (addr.length() < "0.0.0.0".length()) {
            return 0;
        }
        Long netaddr = 0L;
        String[] ip = addr.split("\\.");
        
        for (int i = 0; i < 4; i++) {
            netaddr += Long.parseLong(ip[i]) << (i << 3);
        }
        return netaddr.intValue();
    }
    
    /**
     * Converts an IP from an integer representation into a string.
     * e.g 16777343 -> "127.0.0.1"
     * 
     * @param netaddr The integer representation of the IP
     * 
     * @return The string representation of the IP
     */
    public static final String netIPToString(long netaddr) {
        return String.format("%d.%d.%d.%d", (netaddr & 0xFF), ((netaddr >> 8) & 0xFF), ((netaddr >> 16) & 0xFF), ((netaddr >> 24) & 0xFF));
    }
}
