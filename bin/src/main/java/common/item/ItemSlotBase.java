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
import util.FileTime;

/**
 *
 * @author Eric
 */
public abstract class ItemSlotBase implements Comparable<ItemSlotBase> {
    private int itemID;
    private long cashItemSN;
    private FileTime dateExpire;
    
    public ItemSlotBase(int itemID) {
        this.itemID = itemID;
        this.cashItemSN = 0;
        this.dateExpire = FileTime.END;
    }
    
    /**
     * Constructs (or, creates) a new Item object.
     * This method is designed to create a "placeholder"
     * item where nothing has been assigned nor being used,
     * as it is only to be used for Cloning an item, or 
     * Decoding a item from a remote server (Center/Login/etc)
     * 
     * @param slotType The ItemSlotType of the item to construct
     * @return A new constructed Item type base
     */
    public static ItemSlotBase CreateItem(int slotType) {
        ItemSlotBase item = null;
        switch (slotType) {
            case ItemSlotType.Equip:
                item = new ItemSlotEquip(0);
                break;
            case ItemSlotType.Bundle:
                item = new ItemSlotBundle(0);
                break;
            case ItemSlotType.Pet:
                break;
        }
        return item;
    }
    
    public String dumpString() {
        return toString();
    }
    
    /**
     * Encodes, or writes, the current item's information
     * into the OutPacket buffer. 
     * 
     * @param packet The OutPacket buffer
     */
    public void encode(OutPacket packet) {
        rawEncode(packet);
    }
    
    public long getCashItemSN() {
        return cashItemSN;
    }
    
    public FileTime getDateExpire() {
        return dateExpire;
    }
    
    public int getItemID() {
        return itemID;
    }
    
    /**
     * Determines if the item has a valid Cash Item Serial Number
     * that is greater than zero, marking this item a Cash Item.
     * 
     * @return If the item is a cash item
     */
    public boolean isCashItem() {
        return cashItemSN > 0;
    }
    
    /**
     * An in-depth comparison of two items. 
     * If the two items are equips, it will
     * continue to compare each individual 
     * equipment stats to determine equality.
     * 
     * @param src The item to compare
     * @return If the two items are the same
     */
    public boolean isSameItem(ItemSlotBase src) {
        if (this.itemID != src.itemID || this.cashItemSN != src.cashItemSN || this.dateExpire != src.dateExpire) {
            return false;
        } else {
            if (src.getType() == ItemSlotType.Equip) {
                return ((ItemSlotEquip) this).isSameEquipItem((ItemSlotEquip) src);
            } else {
                return true;
            }
        }
    }
    
    /**
     * Encodes, or writes, the raw item information,
     * such as the ItemID, CashItemSN, and expiration.
     * 
     * A note about this packet is that each inherited
     * class will override this function to encode
     * all of the additional data that the class has.
     * 
     * @param packet The OutPacket stream
     */
    public void rawEncode(OutPacket packet) {
        packet.encodeInt(itemID);
        if (cashItemSN > 0) {
            packet.encodeBool(true);
            packet.encodeLong(cashItemSN);
            packet.encodeFileTime(dateExpire);
        } else {
            packet.encodeBool(false);
        }
    }
    
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
    
    public void setCashItemSN(long sn) {
        this.cashItemSN = sn;
    }
    
    public void setDateExpire(FileTime ft) {
        this.dateExpire = ft;
    }
    
    @Override
    public int compareTo(ItemSlotBase o) {
        if (!isSameItem(o)) {
            if (this.itemID < o.itemID) {
                return -1;
            } else if (this.itemID > o.itemID) {
                return 1;
            }
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return String.format("ItemID: %d / CashItemSN: %d", this.itemID, this.cashItemSN);
    }
    
    /**
     * The quantity of the item. This variable
     * should only ever be changed for items
     * whose type is of Bundle. All others
     * should remain 1.
     * 
     * @return 
     */
    public abstract short getItemNumber();
    public abstract void setItemNumber(short number);
    
    /**
     * A base will include a generated CashItemSN,
     * but regular SN's are generated/read per class.
     * This will get the item's regular tracking SN.
     * 
     * @return 
     */
    public abstract long getSN();
    
    /**
     * While the Center server determines the base
     * Type Index by the ItemID the base holds,
     * each inherited class will return their
     * proper Item Slot Type value.
     * 
     * @return 
     */
    public abstract int getType();
    
    /**
     * Nexon has the ability to construct a clone
     * by using an inherited GetDataSize block and
     * copying the memory to the new base class.
     * We can't do that so we'll override it per class.
     * 
     * @return 
     */
    public abstract ItemSlotBase makeClone();
}
