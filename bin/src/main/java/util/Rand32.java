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
 * MapleStory RNG
 * 
 * @author Eric
 */
public class Rand32 {
    private static Rand32 g_rand;
    
    private int s1;
    private int s2;
    private int s3;
    
    public Rand32(int seed) {
        int rand = crtRand(seed);
        
        this.s1 = seed | 0x100000;
        this.s2 = rand | 0x1000;
        this.s3 = crtRand(rand) | 0x10;
    }
    
    public static Rand32 getInstance() {
        if (g_rand == null) {
            g_rand = new Rand32((int) (System.currentTimeMillis() / 1000));
        }
        return g_rand;
    }
    
    public static int crtRand(int rand) {
        return 214013 * rand + 2531011;
    }
    
    public Long random() {
        int v3;
        int v4;
        int v5;

        v3 = ((((s1 >> 6) & 0x3FFFFFF) ^ (s1 << 12)) & 0x1FFF) ^ ((s1 >> 19) & 0x1FFF) ^ (s1 << 12);
        v4 = ((((s2 >> 23) & 0x1FF) ^ (s2 << 4)) & 0x7F) ^ ((s2 >> 25) & 0x7F) ^ (s2 << 4);
        v5 = ((((s3 << 17) ^ ((s3 >> 8) & 0xFFFFFF)) & 0x1FFFFF) ^ (s3 << 17)) ^ ((s3 >> 11) & 0x1FFFFF);

        s3 = v5;
        s1 = v3;
        s2 = v4;
            
        return (s1 ^ s2 ^ s3) & 0xFFFFFFFFL;
    }
    
    public float randomFloat() {
        int uBits = (int) ((random() & 0x007FFFFF) | 0x3F800000);
        
        return Float.intBitsToFloat(uBits) - 1.0f;
    }
}
