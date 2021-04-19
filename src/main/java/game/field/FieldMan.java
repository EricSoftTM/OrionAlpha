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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import util.Rect;
import util.Size;
import util.TimerThread;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class FieldMan {
    private static FieldMan[] managers;
	private static ReentrantLock lock;
    
    private final Map<Integer, Field> fields;
    
    public FieldMan() {
        this.fields = new ConcurrentHashMap<>();
        
        TimerThread.Field.Register(() -> {
            FieldMan.this.update(System.currentTimeMillis());
        }, 100, 1000);
    }
    
    public static FieldMan getInstance(int channel) {
        if (channel >= 0 && channel < managers.length) {
            return managers[channel];
        }
        return null;
    }
    
    public static void init(int channels) {
        managers = new FieldMan[channels];
	    lock = new ReentrantLock();
        
        for (int i = 0; i < channels; i++) {
            managers[i] = new FieldMan();
        }
    }
    
    public Field getField(int fieldID, boolean forceLoad) {
        if (fieldID == Field.Invalid) {
            return null;
        } else {
            Field field = fields.get(fieldID);
            if (field == null || forceLoad) {
            	lock.lock();
            	try {
            		WzPackage fieldDir = new WzFileSystem().init("Map/Map").getPackage();
            		if (fieldDir != null) {
			            field = registerField(fieldID, fieldDir.getItem(String.format("%09d.img", fieldID)));
			            if (field != null) {
				            fields.put(fieldID, field);
			            }
			            fieldDir.release();
		            }
		            fieldDir = null;
	            } finally {
            		lock.unlock();
	            }
            }
            return field;
        }
    }
    
    public boolean isBlockedMap(int fieldID) {
        // Probably nothing even worth blocking in this version.
        return false;
    }
    
    private Field registerField(int fieldID, WzProperty mapData) {
        if (mapData != null) {
            final Field field = fieldID == 109020001 ? new OXQuiz(fieldID)
                            : new Field(fieldID);
    
            WzProperty info = mapData.getNode("info");
            if (info != null) {
                field.setFieldReturn(WzUtil.getInt32(info.getNode("returnMap"), Field.Invalid));
                field.setForcedReturn(WzUtil.getInt32(info.getNode("forcedReturn"), Field.Invalid));
                field.setMobRate(WzUtil.getFloat(info.getNode("mobRate"), 1.0f));
                field.setRecoveryRate(WzUtil.getFloat(info.getNode("recovery"), 1.0f));
                field.setStreetName(WzUtil.getString(info.getNode("streetName"), "NULL"));
                field.setMapName(WzUtil.getString(info.getNode("mapName"), "NULL"));
                field.setOption(WzUtil.getInt32(info.getNode("fieldLimit"), 0));
                field.setAutoDecHP(WzUtil.getInt32(info.getNode("decHP"), 0));
                field.setAutoDecMP(WzUtil.getInt32(info.getNode("decMP"), 0));
                field.setClock(WzUtil.getBoolean(info.getNode("clock"), false));
                field.setTown(WzUtil.getBoolean(info.getNode("town"), false));
                field.setSwim(WzUtil.getBoolean(info.getNode("swim"), false));
    
                field.getSpace2D().setFieldAttr(WzUtil.getFloat(info.getNode("fs"), 1.0f), field.isSwim());
            }
    
            restoreFoothold(field, mapData.getNode("foothold"), mapData.getNode("ladderRope"), info);
            restoreArea(field, mapData.getNode("area"));
            field.makeSplit();
            field.getPortal().restorePortal(mapData.getNode("portal"), field);
            field.getLifePool().init(field, mapData);
    
            return field;
        }
        return null;
    }
    
    private void restoreArea(Field field, WzProperty areaData) {
        if (areaData != null) {
            for (WzProperty area : areaData.getChildNodes()) {
                int x1 = WzUtil.getInt32(area.getNode("x1"), 0);
                int y1 = WzUtil.getInt32(area.getNode("y1"), 0);
                int x2 = WzUtil.getInt32(area.getNode("x2"), 0);
                int y2 = WzUtil.getInt32(area.getNode("y2"), 0);
                field.getAreaRect().put(area.getNodeName(), new Rect(x1, y1, x2, y2));
            }
        }
    }
    
    private void restoreFoothold(Field field, WzProperty propFoothold, WzProperty ladderOrRope, WzProperty info) {
        field.getSpace2D().load(field, propFoothold, ladderOrRope, info);
        field.setLeftTop(new Point(field.getSpace2D().getMBR().left, field.getSpace2D().getMBR().top));
        field.setMapSize(new Size(field.getSpace2D().getMBR().right - field.getSpace2D().getMBR().left, field.getSpace2D().getMBR().bottom - field.getSpace2D().getMBR().top));
    }
    
    private void update(long time) {
        updateField(time);
    }
    
    private void updateField(long time) {
        for (Field field : fields.values()) {
            field.update(time);
        }
    }

    public boolean isConnected(int from, int to) {
        from /= 100000;
        to /= 100000;
        if (from == to) {
            return true;
        }
        // Not really sure how else to check this..
        return true;
    }
}
