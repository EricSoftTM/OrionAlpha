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
package util.wz;

import java.awt.Point;

/**
 *
 * @author Eric
 */
public class WzUtil {
    
    public static boolean getBoolean(WzProperty p, boolean def) {
        return getInt32(p, def ? 1 : 0) != 0;
    }
    
    public static byte getByte(WzProperty p, int def) {
        return (byte) getInt32(p, (byte) def);
    }
    
    public static double getDouble(WzProperty p, double def) {
        if (p != null && p.getNodeType() == WzNodeType.Double) {
            return (Double) p.getNodeValue();
        }
        return def;
    }
    
    public static float getFloat(WzProperty p, float def) {
        if (p != null && p.getNodeType() == WzNodeType.Float) {
            return (Float) p.getNodeValue();
        }
        return def;
    }
    
    public static int getInt32(WzProperty p, int def) {
        if (p != null) {
            switch (p.getNodeType()) {
                case Short:
                    return (Short) p.getNodeValue();
                case Int:
                    return (Integer) p.getNodeValue();
            }
        }
        return def;
    }
    
    public static Point getPoint(WzProperty p, Point def) {
        if (p != null && p.getNodeType() == WzNodeType.WzVector2D) {
            return (Point) p.getNodeValue();
        }
        return def;
    }
    
    public static short getShort(WzProperty p, int def) {
        return (short) getInt32(p, (short) def);
    }
    
    public static String getString(WzProperty p, String def) {
        if (p != null && (p.getNodeType() == WzNodeType.String || p.getNodeType() == WzNodeType.WzUOL)) {
            return (String) p.getNodeValue();
        }
        return def;
    }
}
