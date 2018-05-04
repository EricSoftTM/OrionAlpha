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

import game.user.User;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public abstract class FieldObj {
    private Field field;
    private final List<FieldSplit> split;
    private final List<Point> posSplit;
    private int posFieldObjList;
    
    public FieldObj() {
        this.field = null;
        this.posFieldObjList = 0;
        this.split = new ArrayList<>(9);
        this.posSplit = new ArrayList<>(9);
    }
    
    public FieldObj(Field field) {
        this();
        this.field = field;
    }
    
    public boolean isShowTo(User user) {
        return true;
    }
    
    public List<Point> getPosSplit() {
        return posSplit;
    }
    
    public List<FieldSplit> getSplits() {
        return split;
    }
    
    public FieldSplit getSplit() {
        return split.get(4);
    }
    
    public Field getField() {
        return field;
    }
    
    public void setField(Field field) {
        this.field = field;
    }
    
    public abstract OutPacket makeEnterFieldPacket();
    public abstract OutPacket makeLeaveFieldPacket();
}
