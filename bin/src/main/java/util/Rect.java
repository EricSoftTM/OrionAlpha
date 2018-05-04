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

import java.awt.Point;

/**
 * tagRECT
 * 
 * @author Eric
 */
public class Rect {
    public int left;   // 00000000 left            dd ?
    public int top;    // 00000004 top             dd ?
    public int right;  // 00000008 right           dd ?
    public int bottom; // 0000000C bottom          dd ?
    
    public Rect() {
        this.left = 0;
        this.top = 0;
        this.right = 0;
        this.bottom = 0;
    }
    
    public Rect(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
    
    /**
     * Checks whether or not this <code>Rectangle</code> contains the
     * specified <code>Point</code>.
     * @param p the <code>Point</code> to test
     * @return    <code>true</code> if the specified <code>Point</code>
     *            is inside this <code>Rectangle</code>;
     *            <code>false</code> otherwise.
     * @since     1.1
     */
    public boolean ptInRect(Point p) {
        return ptInRect(p.x, p.y);
    }
    
    /**
     * Checks whether or not this <code>Rectangle</code> contains the
     * point at the specified location {@code (x,y)}.
     *
     * @param  x the specified X coordinate
     * @param  y the specified Y coordinate
     * @return    <code>true</code> if the point
     *            {@code (x,y)} is inside this
     *            <code>Rectangle</code>;
     *            <code>false</code> otherwise.
     * @since     1.1
     */
    public boolean ptInRect(int x, int y) {
        return left <= x && x <= right && top <= y && y <= bottom;
        //return contains(x, y);
    }
    
    /**
     * public inflate(dx: Number, dy: Number) : Void
     * x -= dx;
     * width += 2 * dx;
     * y -= dy;
     * height += 2 * dy;
     * 
     * @param dx
     * @param dy
     */
    public void inflateRect(int dx, int dy) {
        this.left -= dx;
        this.right += /*2 */ dx;
        this.top -= dy;
        this.bottom += /*2 */ dy;
    }
    
    public Rect unionRect(Rect rc) {
        long tx2 = this.right;
        long ty2 = this.bottom;
        if ((tx2 | ty2) < 0) {
            return rc;
        }
        long rx2 = rc.right;
        long ry2 = rc.bottom;
        if ((rx2 | ry2) < 0) {
            return this;
        }
        int tx1 = this.left;
        int ty1 = this.top;
        tx2 += tx1;
        ty2 += ty1;
        int rx1 = rc.left;
        int ry1 = rc.top;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1) tx1 = rx1;
        if (ty1 > ry1) ty1 = ry1;
        if (tx2 < rx2) tx2 = rx2;
        if (ty2 < ry2) ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        if (tx2 > Integer.MAX_VALUE) tx2 = Integer.MAX_VALUE;
        if (ty2 > Integer.MAX_VALUE) ty2 = Integer.MAX_VALUE;
        return new Rect(tx1, ty1, (int) tx2, (int) ty2);
    }
    
    /**
     * Offset the rectangle by adding {@param dx} to its left and right coordinates, and adding {@param dy} to its top and bottom coordinates.
     * 
     * @param dx
     * @param dy 
     */
    public void offsetRect(double dx, double dy) {
        this.left += dx;
        this.right += dx;
        this.top += dy;
        this.bottom += dy;
    }
    
    public boolean isRectEmpty() {
        return left == 0 && right == 0 && top == 0 && bottom == 0;
    }
    
    public boolean intersectRect(Rect rc) {
        if (rc.left < this.left + this.right && this.left < rc.left + rc.right && rc.top < this.top + this.bottom)
            return this.top < rc.top + rc.bottom;
        else
            return false;
    }
    
    public static Rect intersectRect(Rect src1, Rect src2) {
        int x1 = Math.max(src1.left, src2.left);
        int x2 = Math.min(src1.left + src1.right, src2.left + src2.right);
        int y1 = Math.max(src1.top, src2.top);
        int y2 = Math.min(src1.top + src1.bottom, src2.top + src2.bottom);
        if (x2 >= x1 && y2 >= y1) {
            return new Rect(x1, y1, x2 - x1, y2 - y1);
        }
        return src1;
    }
}
