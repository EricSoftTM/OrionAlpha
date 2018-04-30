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
public class ItemSlotEquip extends ItemSlotBase {
    private long sn;
    
    // TODO: Getter/Setters
    public byte ruc;//Remaining Upgrade Count
    public byte cuc;//Current Upgrade Count
    public short iSTR;
    public short iDEX;
    public short iINT;
    public short iLUK;
    public short iMaxHP;
    public short iMaxMP;
    public short iPAD;//Physical Attack Damage
    public short iMAD;//Magic Attack Damage
    public short iPDD;//Physical Defense
    public short iMDD;//Magic Defense
    public short iACC;//Accuracy Rate
    public short iEVA;//Evasion
    public short iCraft;//Hands
    public short iSpeed;
    public short iJump;
    
    public ItemSlotEquip(int itemID) {
        super(itemID);
    }
    
    @Override
    public short getItemNumber() {
        return 1;
    }
    
    @Override
    public long getSN() {
        return sn;
    }
    
    @Override
    public int getType() {
        return ItemSlotType.Equip;
    }
    
    public boolean isSameEquipItem(ItemSlotEquip src) {
        return this.ruc == src.ruc && this.cuc == src.cuc && this.iSTR == src.iSTR && this.iDEX == src.iDEX && this.iINT == src.iINT && this.iLUK == src.iLUK
                && this.iMaxHP == src.iMaxHP && this.iMaxMP == src.iMaxMP && this.iPAD == src.iPAD && this.iMAD == src.iMAD && this.iPDD == src.iPDD 
                && this.iMDD == src.iMDD && this.iACC == src.iACC && this.iEVA == src.iEVA && this.iCraft == src.iCraft && this.iSpeed == src.iSpeed 
                && this.iJump == src.iJump;
    }
    
    @Override
    public ItemSlotBase makeClone() {
        ItemSlotEquip item = (ItemSlotEquip) CreateItem(ItemSlotType.Equip);
        item.setItemID(this.getItemID());
        item.setCashItemSN(this.getCashItemSN());
        item.setDateExpire(this.getDateExpire());
        
        // TODO: Re-assign cloned stats
        return item;
    }
    
    @Override
    public void rawEncode(OutPacket packet) {
        packet.encodeByte(ruc);
        packet.encodeByte(cuc);
        packet.encodeShort(iSTR);
        packet.encodeShort(iDEX);
        packet.encodeShort(iINT);
        packet.encodeShort(iLUK);
        packet.encodeShort(iMaxHP);
        packet.encodeShort(iMaxMP);
        packet.encodeShort(iPAD);
        packet.encodeShort(iMAD);
        packet.encodeShort(iPDD);
        packet.encodeShort(iMDD);
        packet.encodeShort(iACC);
        packet.encodeShort(iEVA);
        packet.encodeShort(iCraft);
        packet.encodeShort(iSpeed);
        packet.encodeShort(iJump);
    }
    
    @Override
    public void setItemNumber(short number) {
        
    }
}
