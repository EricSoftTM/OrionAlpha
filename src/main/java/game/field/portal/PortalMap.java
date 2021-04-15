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
package game.field.portal;

import game.field.Field;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import util.Logger;
import util.Rand32;
import util.Rect;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class PortalMap {
    private final List<Portal> portal;
    private final List<Byte> startPoint;
    
    public PortalMap() {
        this.portal = new ArrayList<>();
        this.startPoint = new ArrayList<>();
    }
    
    public List<Portal> getPortal() {
        return portal;
    }
    
    public List<Byte> getStartPoint() {
        return startPoint;
    }
    
    public boolean enablePortal(String name, boolean enable) {
        for (Portal p : portal) {
            if ((p.getPortalType() == PortalType.Changable || p.getPortalType() == PortalType.Changable_Invisible) && p.getPortalName().equalsIgnoreCase(name)) {
                p.setEnable(enable);
                return true;
            }
        }
        return false;
    }
    
    public Portal findCloseStartPoint(int x, int y) {
        int count = 0;
        if (!startPoint.isEmpty())
            count = startPoint.size() - 1;
        int idx = -1;
        double dis = 0;
        if (count >= 0) {
            for (int i = count; i >= 0; i--) {
                Portal pt = portal.get(startPoint.get(i));
                double range = (double)((x - pt.getPortalPos().x) * (x - pt.getPortalPos().x) + (y - pt.getPortalPos().y) * (y - pt.getPortalPos().y));
                if (idx < 0 || range < dis) {
                    dis = range;
                    idx = i;
                }
            }
        }
        return portal.get(startPoint.get(idx));
    }
    
    public Portal findPortal(String name) {
        for (Portal p : portal) {
            if (p.getPortalName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
    
    public Portal getPortal(int idx) {
        return portal.get(idx);
    }
    
    public Portal getRandStartPoint() { // GetRandStartPoint2 and GetRandStartPoint3 are the same and just LogReport..
        if (!startPoint.isEmpty()) {
            int idx = (int) (Rand32.getInstance().random() % startPoint.size());
            if (idx < 0) {//should never happen..
                idx = 0;
            }
            return portal.get(startPoint.get(idx));
        } else {
            Logger.logError("GetRandStartPoint() Failed. The count of m_aStartPoint is zero.");
            if (!portal.isEmpty()) {
                return portal.get(0);
            } else {
                Logger.logReport("GetRandStartPoint() Failed. There are no portals in the map.");
                return null;
            }
        }
    }
    
    public boolean isPortalNear(List<Point> routes, int xrange) {
        for (Portal pt : portal) {
            if (pt.getPortalType() > 0) {
                if (pt.getPortalType() != PortalType.TownPortal_Point) {
                    Rect rc = new Rect(pt.getPortalPos().x - xrange, pt.getPortalPos().y - 70, pt.getPortalPos().x + xrange, pt.getPortalPos().y + 70);
                    for (Point route : routes) {
                        if (rc.ptInRect(route)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void resetPortal() {
        for (Portal p : portal) {
            p.setEnable(!(p.getPortalType() == PortalType.Changable || p.getPortalType() == PortalType.Changable_Invisible));
        }
    }
    
    public void restorePortal(WzProperty propPortal, Field field) {
        if (propPortal != null && field != null) {
            for (WzProperty prop : propPortal.getChildNodes()) {
                Portal pt = new Portal();
                pt.setFieldID(field.getFieldID());
                pt.setPortalIdx(portal.size());
                pt.setPortalName(WzUtil.getString(prop.getNode("pn"), ""));
                pt.setPortalType(WzUtil.getInt32(prop.getNode("pt"), 0));
                pt.setEnable(pt.getPortalType() != PortalType.Changable && pt.getPortalType() != PortalType.Changable_Invisible);
                pt.getPortalPos().setLocation(WzUtil.getInt32(prop.getNode("x"), 0), WzUtil.getInt32(prop.getNode("y"), 0));
                pt.setTargetName(WzUtil.getString(prop.getNode("tn"), ""));
                pt.setTargetMap(WzUtil.getInt32(prop.getNode("tm"), 0));
                pt.setScript(null);
                if (pt.getPortalType() == PortalType.Script || pt.getPortalType() == PortalType.Script_Invisible || pt.getPortalType() == PortalType.Collision_Script) {
                    pt.setScript(WzUtil.getString(prop.getNode("script"), null));
                }
                if (pt.getPortalType() != PortalType.StartPoint) {
                    pt.getPortalPos().y -= 40;
                } else {
                    startPoint.add(pt.getPortalIdx());
                }
                portal.add(pt);
            }
            if (startPoint.isEmpty()) {
                Logger.logError("No start-point in map [%d]", field.getFieldID());
            }
        }
    }
}
