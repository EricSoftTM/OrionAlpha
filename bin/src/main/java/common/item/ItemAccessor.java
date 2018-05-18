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

import common.user.DBChar;
import util.FileTime;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class ItemAccessor {
    
    public static int getWeaponType(int itemID) {
        if (getItemTypeIndexFromID(itemID) == ItemType.Equip) {
            return itemID / 10000 % 100;
        }
        return 0;
    }
    
    public static boolean isTreatSingly(ItemSlotBase p) {
        byte ti = getItemTypeIndexFromID(p.getItemID());
        return !isBundleTypeIndex(ti) || isRechargeableItem(p.getItemID()) || FileTime.compareFileTime(p.getDateExpire(), FileTime.END) < 0;
    }
    
    public static boolean isTreatSingly(int itemID) {
        byte ti = getItemTypeIndexFromID(itemID);
        return !isBundleTypeIndex(ti) || isRechargeableItem(itemID);
    }
    
    public static boolean isBundleTypeIndex(byte ti) {
        return ti == ItemType.Consume || ti == ItemType.Install || ti == ItemType.Etc;
    }
    
    public static boolean isJavelinItem(int itemID) {
        return itemID / 10000 == 207; // lol throwing stars xd
    }
    
    public static boolean isLongCoat(int itemID) {
        return itemID / 10000 == 105;
    }
    
    public static boolean isRechargeableItem(int itemID) {
        return isJavelinItem(itemID);
    }
    
    public static byte getItemTypeIndexFromID(int itemID) {
        return (byte) (itemID / 1000000);
    }
    
    public static int getItemTypeFromTypeIndex(int ti) {
        switch (ti) {
            case ItemType.Equip:
                return DBChar.ItemSlotEquip;
            case ItemType.Consume:
                return DBChar.ItemSlotConsume;
            case ItemType.Install:
                return DBChar.ItemSlotInstall;
            case ItemType.Etc:
                return DBChar.ItemSlotEtc;
            default: {
                return 0;
            }
        }
    }

    public static void getBodyPartFromItem(int itemID, byte gender, Pointer<Integer> bodyPart, boolean b) {
        // TODO
    }
}
