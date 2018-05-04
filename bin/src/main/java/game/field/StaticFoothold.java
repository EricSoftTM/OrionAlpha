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
package game.field;

import java.awt.Point;

/**
 *
 * @author Eric
 */
public class StaticFoothold implements Comparable<StaticFoothold> {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int page;
    private int ZMass;
    private AttrFoothold attrFoothold;
    private int sn;
    private int snNext;
    private int snPrev;
    private double len;
    private double uvx;
    private double uvy;
    
    public StaticFoothold() {
        this.attrFoothold = new AttrFoothold();
        this.sn = 0;
    }
    
    public StaticFoothold(Point p1, Point p2, int sn) {
        this.x1 = p1.x;
        this.y1 = p1.y;
        this.x2 = p2.x;
        this.y2 = p2.y;
        this.sn = sn;
    }
    
    public AttrFoothold getAttrFoothold() {
        return attrFoothold;
    }
    
    public int getX1() {
        return x1;
    }
    
    public int getX2() {
        return x2;
    }
    
    public int getY1() {
        return y1;
    }
    
    public int getY2() {
        return y2;
    }
    
    public int getSN() {
        return sn;
    }
    
    public int getNextSN() {
        return snNext;
    }
    
    public int getPrevSN() {
        return snPrev;
    }
    
    public int getPage() {
        return page;
    }
    
    public int getZMass() {
        return ZMass;
    }
    
    public double getLen() {
        return len;
    }
    
    public double getUvx() {
        return uvx;
    }
    
    public double getUvy() {
        return uvy;
    }
    
    public boolean isWall() {
        return x1 == x2;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public void setSnNext(int snNext) {
        this.snNext = snNext;
    }

    public void setSnPrev(int snPrev) {
        this.snPrev = snPrev;
    }
    
    public void setSN(int sn) {
        this.sn = sn;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public void setZMass(int mass) {
        this.ZMass = mass;
    }
    
    public void validateVectorInfo() {
        double lr = (double) (x2 - x1);
        double tb = (double) (y2 - y1);
        double v = Math.sqrt(tb * tb + lr * lr);
        len = v;
        uvx = lr / v;
        uvy = tb / v;
    }
    
    @Override
    public int compareTo(StaticFoothold o) {
        if (y2 < o.y1) {
            return -1;
        } else if (y1 > o.y2) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public class AttrFoothold {
        private double walk;
        private double drag;
        private double force;
        
        public AttrFoothold() {
            this.walk = 0.0d;
            this.drag = 0.0d;
            this.force = 0.0d;
        }
        
        public double getWalk() {
            return walk;
        }
        
        public double getDrag() {
            return drag;
        }
        
        public double getForce() {
            return force;
        }
        
        public void setWalk(double walk) {
            this.walk = walk;
        }
        
        public void setDrag(double drag) {
            this.drag = drag;
        }
        
        public void setForce(double force) {
            this.force = force;
        }
    }
}
