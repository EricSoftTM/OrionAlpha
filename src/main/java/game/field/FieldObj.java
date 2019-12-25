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
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public abstract class FieldObj {
    private Field field;
    private final FieldSplit[] split;
    
    public FieldObj() {
        this(null);
    }
    
    public FieldObj(Field field) {
        this.field = field;
        this.split = new FieldSplit[9];
    }
    
    public boolean isShowTo(User user) {
        return true;
    }
    
    public FieldSplit[] getSplits() {
        return split;
    }
    
    public FieldSplit getSplit() {
        return split[4];
    }
    
    public void setSplit(int index, FieldSplit split) {
        this.split[index] = split;
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
