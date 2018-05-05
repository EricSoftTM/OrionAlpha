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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import util.Size;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class FieldMan {
    private static final WzPackage fieldDir = new WzFileSystem().init("Map/Map").getPackage();
    private static final FieldMan instance = new FieldMan();
    
    private final Map<Integer, Field> fields;
    private final Lock lock;
    
    public FieldMan() {
        this.fields = new HashMap<>();
        this.lock = new ReentrantLock();
        
        
    }
    
    public static FieldMan getInstance() {
        return instance;
    }
    
    public Field getField(int fieldID, boolean forceLoad) {
        if (fieldID == Field.Invalid) {
            return null;
        } else {
            Field field = fields.get(fieldID);
            if (field == null || forceLoad) {
                lock.lock();
                try {
                    if ((field != null && forceLoad) || field == null) {
                        field = registerField(fieldID, fieldDir.getItem(String.format("%09d.img", fieldID)));
                        fields.put(fieldID, field);
                    }
                } finally {
                    lock.unlock();
                }
            }
            return field;
        }
    }
    
    private Field registerField(int fieldID, WzProperty mapData) {
        final Field field = new Field(fieldID);
        
        WzProperty info = mapData.getNode("info");
        field.setForcedReturn(WzUtil.getInt32(info.getNode("returnMap"), Field.Invalid));
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
        
        restoreFoothold(field, mapData.getNode("foothold"), mapData.getNode("ladderRope"), info);
        field.getSpace2D().setFieldAttr(WzUtil.getFloat(info.getNode("fs"), 1.0f), field.isSwim());
        field.makeSplit();
        field.getPortal().restorePortal(mapData.getNode("portal"), field);
        field.getLifePool().init(field, mapData);
        
        return field;
    }
    
    private void restoreFoothold(Field field, WzProperty propFoothold, WzProperty ladderOrRope, WzProperty info) {
        field.getSpace2D().load(field, propFoothold, ladderOrRope, info);
        field.setLeftTop(new Point(field.getSpace2D().getMBR().left, field.getSpace2D().getMBR().top));
        field.setMapSize(new Size(field.getSpace2D().getMBR().right - field.getSpace2D().getMBR().left, field.getSpace2D().getMBR().bottom - field.getSpace2D().getMBR().top));
    }
}
