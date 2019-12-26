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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Pointer;
import util.Range;
import util.Rect;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class WvsPhysicalSpace2D {
    private final Rect mbr;
    private final List<Range> massRange;
    private final List<Integer> indexZMass;
    private int baseZMass;
    //private final List<StaticFoothold> foothold;
    private final Map<Integer, StaticFoothold> footholds;
    private final List<LadderOrRope> ladderOrRope;
    private AttrField attrField;
    
    public WvsPhysicalSpace2D() {
        this.massRange = new ArrayList<>();
        this.indexZMass = new ArrayList<>();
        this.footholds = new HashMap<>();
        this.ladderOrRope = new ArrayList<>();
        this.mbr = new Rect(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
    
    public LadderOrRope findLadderOrRope(int x, int y, int dx, int dy) {
        for (LadderOrRope p : ladderOrRope) {
            if (p.x >= x - dx && p.x <= x + dx && p.y1 - dy <= y && dy + p.y2 >= y)
                return p;
        }
        return null;
    }
    
    public StaticFoothold getFoothold(int sn) {
        return footholds.get(sn);
    }
    
    public StaticFoothold getFootholdClosest(Field field, int x, int y, Pointer<Integer> pcx, Pointer<Integer> pcy, int hitx) {
        StaticFoothold ret = null;
        
        int minimum = Integer.MAX_VALUE;
        for (StaticFoothold pfh : footholds.values()) {
            int x1 = pfh.getX1();
            int x2 = pfh.getX2();
            if (x1 < x2) {
                if (hitx > x) {
                    if (x1 < x)
                        continue;
                }
                if (hitx <= x || x2 <= x) {
                    if (pfh.getY1() >= y - 100) {
                        if (pfh.getY2() >= y - 100) {
                            int x0;
                            int y0;
                            if (hitx <= x) {
                                if (hitx >= x) {
                                    x0 = (x1 + x2) / 2 - x;
                                } else {
                                    x0 = x2 - x;
                                }
                            } else {
                                x0 = x1 - x;
                            }
                            if (hitx <= x) {
                                if (hitx >= x)
                                    y0 = (pfh.getY2() + pfh.getY1()) / 2;
                                else
                                    y0 = pfh.getY2();
                            } else {
                                y0 = pfh.getY1();
                            }
                            int dist = x0 * x0 + (y0 - y) * (y0 - y);
                            if (dist < minimum) {
                                if (x > x1 && (x >= x2 || x - (x1 + x2) / 2 >= 0))
                                    x1 = x2;
                                x1 = Math.min(Math.max(x1, mbr.left + 10), mbr.right - 10);
                                int y1 = pfh.getY1() + (((pfh.getY2() - pfh.getY1()) * (x1 - pfh.getX1())) / (x2 - pfh.getX1()));
                                if (field.splitFromPoint(x1, y1) != null) {
                                    pcx.set(x1);
                                    pcy.set(y1);
                                    minimum = dist;
                                    ret = pfh;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (ret == null) {
            ret = getFootholdUnderneath(x, y - 100, pcy);
            x = Math.min(Math.max(x, mbr.left + 10), mbr.right - 10);
            pcx.set(x);
            if (ret == null) {
                if (hitx != Integer.MAX_VALUE)
                    ret = getFootholdClosest(field, x, y, pcx, pcy, Integer.MAX_VALUE);
            }
        }
        return ret;
    }
    
    /*public void getFootholdRandom(int count, Rect range, List<Point> apt) {
        Rect dst = Rect.IntersectRect(range, mbr);
        List<Integer> shuffle = new ArrayList<>();
        int x0 = dst.left;
        int x1 = (dst.right - dst.left + 1) / (2 * count);
        getRandomUniqueArray(shuffle, 0, 2 * count, 2 * count);
        for (int i = 0; i < 2 * count; i++) {
            List<Integer> position = new ArrayList<>();
            int y1;
            if (x1 == 0) {
                y1 = 0;
            } else {
                y1 = Rand32.getRand(x1, 0).intValue();
            }
            int x2 = x0 + y1 + x1 * shuffle.get(i);
            getFootholdRange(x2, dst.top, dst.bottom, position);
            if (!position.isEmpty()) {
                int y2 = position.get(Rand32.getRand(position.size(), 0).intValue());
                apt.add(new Point(x2, y2));
                if (apt.size() == count) {
                    position.clear();
                    break;
                }
            }
            position.clear();
        }
        shuffle.clear();
    }*/
    
    public void getFootholdRange(int x, int y1, int y2, List<Integer> position) {
        for (StaticFoothold pfh : footholds.values()) {
            int x2 = pfh.getX2();
            int x1 = pfh.getX1();
            if (x1 < x2) {
                if (x1 <= x) {
                    if (x2 >= x) {
                        int y = pfh.getY1() + (x - x1) * (pfh.getY2() - pfh.getY1()) / (x2 - x1);
                        if (y >= y1) {
                            if (y2 >= y) {
                                position.add(y);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public StaticFoothold getFootholdUnderneath(int x, int y, Pointer<Integer> pcy) {
        int y1 = Integer.MAX_VALUE;
        StaticFoothold fh = null;
        for (StaticFoothold pfh : footholds.values()) {
            int x2 = pfh.getX2();
            int x1 = pfh.getX1();
            if (x1 < x2) {
                if (x1 <= x) {
                    if (x2 >= x) {
                        int y2 = pfh.getY1() + (x - x1) * (pfh.getY2() - pfh.getY1()) / (x2 - x1);
                        if (y2 >= y) {
                            if (y2 < y1) {
                                y1 = pfh.getY1() + (x - x1) * (pfh.getY2() - pfh.getY1()) / (x2 - x1);
                                if (pcy != null) {
                                    pcy.set(y2);
                                }
                                fh = pfh;
                            }
                        }
                    }
                }
            }
        }
        return fh;
    }
    
    public StaticFoothold getFootholdUnderneath(int x, int y) {
        return getFootholdUnderneath(x, y, null);
    }
    
    public LadderOrRope getLadderOrRope(int x1, int y1, int x2, int y2) {
        int xMin = Math.min(x1, x2) - 10;
        int xMax = Math.max(x1, x2) + 10;
        int yMin = Math.min(y1, y2);
        int yMax = Math.max(y1, y2);
        for (LadderOrRope p : ladderOrRope) {
            if (xMin <= p.x && p.x <= xMax && yMin <= p.y2 && p.y1 <= yMax) {
                return p;
            }
        }
        return null;
    }
    
    public LadderOrRope getLadderOrRopeBySN(int sn) {
        for (LadderOrRope p : ladderOrRope) {
            if (p.sn == sn) {
                return p;
            }
        }
        return null;
    }
    
    public Rect getMBR() {
        return mbr;
    }
    
    public boolean isPointInMBR(int x, int y, boolean asClient) {
        Rect rc;
        if (asClient) {
            rc = new Rect(mbr.left + 9, mbr.top + 9, mbr.right - 9, mbr.bottom - 9);
        } else {
            rc = new Rect();
            rc.left = mbr.left;
            rc.top = mbr.top;
            rc.right = mbr.right;
            rc.bottom = mbr.bottom;
        }
        return rc.ptInRect(x, y);
    }
    
    public void load(Field field, WzProperty propFoothold, WzProperty ladderRope, WzProperty info) {
        if (propFoothold != null) {
            for (WzProperty page : propFoothold.getChildNodes()) {
                for (WzProperty massFoothold : page.getChildNodes()) {
                    for (WzProperty foothold : massFoothold.getChildNodes()) {
                        int x0 = WzUtil.getInt32(foothold.getNode("x1"), 0);
                        int y0 = WzUtil.getInt32(foothold.getNode("y1"), 0);
                        int x1 = WzUtil.getInt32(foothold.getNode("x2"), 0);
                        int y1 = WzUtil.getInt32(foothold.getNode("y2"), 0);
                        int drag = WzUtil.getInt32(foothold.getNode("drag"), 0);
                        int force = WzUtil.getInt32(foothold.getNode("force"), 0);
                        int snPrev = WzUtil.getInt32(foothold.getNode("prev"), 0);
                        int snNext = WzUtil.getInt32(foothold.getNode("next"), 0);
                        
                        StaticFoothold pfh = new StaticFoothold();
                        pfh.setSN(Integer.valueOf(foothold.getNodeName()));
                        pfh.setX1(x0);
                        pfh.setY1(y0);
                        pfh.setX2(x1);
                        pfh.setY2(y1);
                        pfh.setPage(Integer.valueOf(page.getNodeName()));
                        pfh.setZMass(Integer.valueOf(massFoothold.getNodeName()));
                        pfh.setSnPrev(snPrev);
                        pfh.setSnNext(snNext);
                        if (drag > 0)
                            pfh.getAttrFoothold().setDrag((double) drag * 0.01d);
                        if (force > 0)
                            pfh.getAttrFoothold().setForce((double) force * 0.01d);
                        pfh.validateVectorInfo();
                        footholds.put(pfh.getSN(), pfh);
                        while (massRange.size() <= pfh.getZMass()) {
                            massRange.add(new Range(Integer.MAX_VALUE, Integer.MIN_VALUE));
                        }
                        
                        int xMin = Math.min(x0, x1);
                        int xMax = Math.max(x0, x1);
                        int yMin = Math.min(y0, y1);
                        int yMax = Math.max(y0, y1);
                        if (mbr.left > xMin + 30)
                            mbr.left = xMin + 30;
                        if (mbr.right < xMax - 30)
                            mbr.right = xMax - 30;
                        if (mbr.top > yMin - 300)
                            mbr.top = yMin - 300;
                        if (mbr.bottom < yMax + 10)
                            mbr.bottom = yMax + 10;
                    }
                }
            }
        }
        if (info != null) {
            boolean VRLimit = WzUtil.getBoolean(info.getNode("VRLimit"), false);
            if (VRLimit) {
                int VRLeft = WzUtil.getInt32(info.getNode("VRLeft"), 0);
                int VRRight = WzUtil.getInt32(info.getNode("VRRight"), 0);
                int VRTop = WzUtil.getInt32(info.getNode("VRTop"), 0);
                int VRBottom = WzUtil.getInt32(info.getNode("VRBottom"), 0);
                
                if (VRLeft > 0 && mbr.left < VRLeft + 20)
                    mbr.left = VRLeft + 20;
                if (VRRight > 0 && mbr.right > VRRight)
                    mbr.right = VRRight;
                if (VRTop > 0 && mbr.top < VRTop + 65)
                    mbr.top = VRTop + 65;
                if (VRBottom > 0 && mbr.bottom > VRBottom)
                    mbr.bottom = VRBottom;
            }
        }
        mbr.inflateRect(10, 10);
        baseZMass = 0;
        int i = 0;
        for (Range r : massRange) {
            if (r.high >= r.low) {
                baseZMass = i;
                break;
            }
            i++;
        }
        if (ladderRope != null) {
            for (WzProperty data : ladderRope.getChildNodes()) {
                LadderOrRope lr = new LadderOrRope();
                lr.sn = Integer.valueOf(data.getNodeName());
                lr.ladder = WzUtil.getBoolean(data.getNode("l"), false);
                lr.upperFoothold = WzUtil.getBoolean(data.getNode("uf"), false);
                lr.x = WzUtil.getInt32(data.getNode("x"), 0);
                lr.y1 = WzUtil.getInt32(data.getNode("y1"), 0);
                lr.y2 = WzUtil.getInt32(data.getNode("y2"), 0);
                lr.page = WzUtil.getInt32(data.getNode("page"), 0);
                ladderOrRope.add(lr);
            }
        }
    }
    
    public void setFieldAttr(double fs, boolean swim) {
        AttrField attr = new AttrField();
        this.attrField = attr;
        this.attrField.setDrag(fs);
        if (swim) {
            this.attrField.setFly(-1.0d);
        }
    }
    
    public class LadderOrRope {
        public int sn;
        public boolean ladder;
        public boolean upperFoothold;
        public int x;
        public int y1, y2;
        public int page;
    }
}
