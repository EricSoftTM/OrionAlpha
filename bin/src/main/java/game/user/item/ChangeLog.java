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
package game.user.item;

import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;

/**
 *
 * @author Eric
 */
public class ChangeLog {
    public static final byte 
            NewItem     = 0,
            ItemNumber  = 1,
            Position    = 2,
            DelItem     = 3
    ;
    private byte change;
    private byte ti;
    private short pos;
    private short pos2;
    private ItemSlotBase pi;
    private short number;
    
    public ChangeLog() {
        
    }
    
    public ChangeLog(byte change, ItemSlotBase item, short pos) {
        this.change = change;
        this.pi = item.makeClone();
        this.ti = ItemAccessor.getItemTypeIndexFromID(item.getItemID());
        this.number = (this.ti == ItemType.Equip ? 1 : item.getItemNumber());
        this.pos = pos;
    }
    
    public byte getChange() {
        return change;
    }
    
    public byte getTI() {
        return ti;
    }
    
    public short getPOS() {
        return pos;
    }
    
    public short getPOS2() {
        return pos2;
    }
    
    public ItemSlotBase getItem() {
        return pi;
    }
    
    public short getNumber() {
        return number;
    }

    public void setChange(byte change) {
        this.change = change;
    }

    public void setTI(byte ti) {
        this.ti = ti;
    }

    public void setPOS(short pos) {
        this.pos = pos;
    }

    public void setPOS2(short pos2) {
        this.pos2 = pos2;
    }

    public void setItem(ItemSlotBase pi) {
        this.pi = pi;
    }

    public void setNumber(short number) {
        this.number = number;
    }
}
