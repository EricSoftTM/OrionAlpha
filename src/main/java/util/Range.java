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
 * tagRANGE
 * 
 * @author Eric
 */
public class Range {
    public int low;
    public int high;
    
    public Range() {
        this.low = 0;
        this.high = 0;
    }
    
    public Range(int low, int high) {
        this.low = low;
        this.high = high;
    }
    
    public int getLow() {
        return low;
    }
    
    public int getHigh() {
        return high;
    }
    
    public void setLow(int low) {
        this.low = low;
    }
    
    public void setHigh(int high) {
        this.high = high;
    }
}
