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
package common.item;

import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class ItemSlotBundle extends ItemSlotBase {
    private short number;
    private long sn;
    
    public ItemSlotBundle(int itemID) {
        super(itemID);
        this.number = 1;
        this.sn = 0;
    }
    
    @Override
    public short getItemNumber() {
        return number;
    }
    
    @Override
    public long getSN() {
        return sn;
    }
    
    @Override
    public int getType() {
        return ItemSlotType.Bundle;
    }
    
    @Override
    public ItemSlotBase makeClone() {
        ItemSlotBundle item = (ItemSlotBundle) CreateItem(ItemSlotType.Bundle);
        item.setItemID(this.getItemID());
        item.setItemSN(this.getSN());
        item.setItemNumber(this.getItemNumber());
        item.setDateExpire(this.getDateExpire());
        return item;
    }
    
    @Override
    public void rawEncode(OutPacket packet) {
        super.rawEncode(packet);
        packet.encodeShort(number);
    }
    
    @Override
    public void setItemNumber(short number) {
        this.number = number;
    }
    
    private void setItemSN(long sn) {
        this.sn = sn;
    }
}
