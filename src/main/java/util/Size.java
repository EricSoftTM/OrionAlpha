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
 * tagSIZE
 * 
 * @author Eric
 */
public class Size {
    public int cx;
    public int cy;
    
    public Size(int cx, int cy) {
        this.cx = cx;
        this.cy = cy;
    }
    
    public Size(Size size) {
        this.cx = size.cx;
        this.cy = size.cy;
    }
    
    public int getCx() {
        return cx;
    }
    
    public int getCy() {
        return cy;
    }
    
    public void setCx(int cx) {
        this.cx = cx;
    }
    
    public void setCy(int cy) {
        this.cy = cy;
    }
}
