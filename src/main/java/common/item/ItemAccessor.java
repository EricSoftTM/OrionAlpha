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

import common.user.CharacterData;
import common.user.DBChar;
import game.user.item.ItemInfo;
import java.util.List;
import util.FileTime;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class ItemAccessor {

    public static boolean isWeapon(int itemID) {
        int type = itemID / 10000;
        
        return (type >= 130 && type <= 133) || type == 137 || type == 138 || (type >= 140 && type <= 147) || type == 160;
    }
    
    public static int getGenderFromID(int itemID) {
        return getItemTypeIndexFromID(itemID) == ItemType.Equip ? itemID / 1000 % 10 : 2;
    }

    public static int getWeaponType(int itemID) {
        int type = itemID / 100000;
        if (getItemTypeIndexFromID(itemID) == ItemType.Equip && (type == 13 || type == 14)) {
            return itemID / 10000 % 100;
        }
        return 0;
    }
    
    public static boolean isStateChangeItem(int itemID) {
        int type = itemID / 10000;
        
        return type == 200 || type == 201 || type == 202 || type == 205;
    }
    
    public static boolean isUpgradeItem(int itemID) {
        return itemID / 10000 == 204;
    }
    
    public static boolean isPortalScrollItem(int itemID) {
        return itemID / 10000 == 203;
    }
    
    public static boolean isWeatherItem(int itemID) {
        return itemID / 10000 == 209;
    }

    public static boolean isTreatSingly(ItemSlotBase p) {
        if (ItemInfo.isCashItem(p.getItemID())) {
            return true;
        }
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
    
    public static boolean isCorrectBulletItem(int weaponItemID, int itemID) {
        int wt = getWeaponType(weaponItemID);
        switch (wt) {
            case 45: // Bow
                return itemID / 1000 == 2060;
            case 46: // Crossbow
                return itemID / 1000 == 2061;
            case 47:
                return isJavelinItem(itemID);
            default: {
                return false;
            }
        }
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
    
    public static byte getItemSlotTypeFromID(int itemID) {
        switch (getItemTypeIndexFromID(itemID)) {
            case ItemType.Equip:
                return ItemSlotType.Equip;
            case ItemType.Consume:
            case ItemType.Install:
            case ItemType.Etc:
                return ItemSlotType.Bundle;
            case ItemType.Cash:
                return ItemSlotType.Pet;
            default: {
                return 0;
            }
        }
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
            case ItemType.Cash:
                return DBChar.ItemSlotCash;
            default: {
                return 0;
            }
        }
    }
    
    public static void getRealEquip(CharacterData c, List<ItemSlotBase> realEquip, int excl1, int excl2) {
        byte gender = c.getCharacterStat().getGender();
        byte level = c.getCharacterStat().getLevel();
        short job = c.getCharacterStat().getJob();
        short STR = c.getCharacterStat().getSTR();
        short DEX = c.getCharacterStat().getDEX();
        short INT = c.getCharacterStat().getINT();
        short LUK = c.getCharacterStat().getLUK();
        short pop = c.getCharacterStat().getPOP();
        short incSTR = 0, incDEX = 0, incINT = 0, incLUK = 0;
        
        ItemSlotEquip equip;
        for (int pos = -1; pos >= -BodyPart.BP_Count; pos--) {
            //equip = (ItemSlotEquip) realEquip.get(-pos);
            if (pos != excl1 && pos != excl2) {
                equip = (ItemSlotEquip) c.getItem(ItemType.Equip, pos);
                if (equip != null) {
                    incSTR += equip.iSTR;
                    incDEX += equip.iDEX;
                    incINT += equip.iINT;
                    incLUK += equip.iLUK;
                }
                realEquip.set(-pos, equip);
            } else {
                realEquip.set(-pos, null);
            }
        }
        
        int pos = 0;
        while (pos <= BodyPart.BP_Count) {
            for (pos = 1; pos <= BodyPart.BP_Count; pos++) {
                equip = (ItemSlotEquip) realEquip.get(pos);
                if (equip != null) {
                    int totSTR = incSTR + STR - equip.iSTR;
                    int totDEX = incDEX + DEX - equip.iDEX;
                    int totINT = incINT + INT - equip.iINT;
                    int totLUK = incLUK + LUK - equip.iLUK;
                    if (!ItemInfo.isAbleToEquip(gender, level, job, totSTR, totDEX, totINT, totLUK, pop, equip.getItemID())) {
                        incSTR -= equip.iSTR;
                        incDEX -= equip.iDEX;
                        incINT -= equip.iINT;
                        incLUK -= equip.iLUK;
                        realEquip.set(pos, null);
                        break;
                    }
                }
            }
        }
    }

    public static int getBodyPartFromItem(int itemID, int gender, Pointer<Integer> bodyPart, boolean all) {
        int itemGender = getGenderFromID(itemID);
        if ((gender != 2 && itemGender != 2 && gender != itemGender) || bodyPart == null) {
            return 0;
        }
        int type = itemID / 10000;
        switch (type) {
            case 100:
                bodyPart.set(BodyPart.Cap);
                break;
            case 101:
                bodyPart.set(BodyPart.FaceAcc);
                break;
            case 102:
                bodyPart.set(BodyPart.EyeAcc);
                break;
            case 103:
                bodyPart.set(BodyPart.EarAcc);
                break;
            case 104:
            case 105:
                bodyPart.set(BodyPart.Clothes);
                break;
            case 106:
            case 107:
            case 108:
                bodyPart.set(type % 10);
                break;
            case 109:
                bodyPart.set(BodyPart.Shield);
                break;
            case 110:
                bodyPart.set(BodyPart.Cape);
                break;
            case 111:
                bodyPart.set(BodyPart.Ring1);
                if (!all) {
                    return 1;
                }
                bodyPart.set(BodyPart.Ring2);
                return 2;
            case 180:
                bodyPart.set(BodyPart.PetWear);
                break;
            default: {
                type /= 10;
                if (type == 13 || type == 14 || type == 16) {
                    bodyPart.set(BodyPart.Weapon);
                }
            }
        }
        return isCorrectBodyPart(itemID, bodyPart.get(), gender) ? 1 : 0;
    }
    
    public static boolean isCorrectBodyPart(int itemID, int bodyPart, int gender) {
        int itemGender = getGenderFromID(itemID);
        if (gender != 2 && itemGender != 2 && gender != itemGender) {
            return false;
        }
        int type = itemID / 10000;
        switch (type) {
            case 100:
                return bodyPart == BodyPart.Cap;
            case 101:
                return bodyPart == BodyPart.FaceAcc;
            case 102:
                return bodyPart == BodyPart.EyeAcc;
            case 103:
                return bodyPart == BodyPart.EarAcc;
            case 104:
            case 105:
                return bodyPart == BodyPart.Clothes;
            case 106:
            case 107:
            case 108:
                return bodyPart == (type % 10);
            case 109:
                return bodyPart == BodyPart.Shield;
            case 110:
                return bodyPart == BodyPart.Cape;
            case 111:
                return bodyPart == BodyPart.Ring1 || bodyPart == BodyPart.Ring2;
            case 180:
                return bodyPart == BodyPart.PetWear;
            default: {
                type /= 10;
                if (type == 13 || type == 14 || type == 16) {
                    return bodyPart == BodyPart.Weapon;
                }
            }
        }
        return false;
    }
    
    public static boolean isMatchedItemIDGender(int itemID, int gender) {
        int itemGender = getGenderFromID(itemID);
        return gender == 2 || itemGender == 2 || itemGender == gender;
    }
}
