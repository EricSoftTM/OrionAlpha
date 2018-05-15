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
import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import common.item.ItemType;
import game.field.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import util.Logger;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 * @author Eric
 * @author Arnah
 */
public class ItemInfo {

    private static WzPackage characterDir;
    private static WzPackage itemDir;
    private static WzPackage fieldDir;
    protected static final Map<Integer, BundleItem> bundleItem;
    protected static final Map<Integer, EquipItem> equipItem;
    protected static final Map<Integer, StateChangeItem> statChangeItem;
    protected static final Map<Integer, PortalScrollItem> portalScrollItem;
    protected static final Map<Integer, UpgradeItem> upgradeItem;
    //
    protected static final Map<Integer, String> mapString;
    protected static final Map<Integer, String> itemString;
    
    static {
        // Initialize Packages
        characterDir = new WzFileSystem().init("Character").getPackage();
        itemDir = new WzFileSystem().init("Item").getPackage();
        fieldDir = new WzFileSystem().init("Map/Map").getPackage();
        
        // Initialize Item Containers
        bundleItem = new HashMap<>();
        equipItem = new HashMap<>();
        statChangeItem = new HashMap<>();
        portalScrollItem = new HashMap<>();
        upgradeItem = new HashMap<>();
        
        // Initialize Strings
        mapString = new HashMap<>();
        itemString = new HashMap<>();
    }

    public static BundleItem getBundleItem(int itemID) {
        return bundleItem.get(itemID);
    }

    public static EquipItem getEquipItem(int itemID) {
        return equipItem.get(itemID);
    }

    public static StateChangeItem getStateChangeItem(int itemID) {
        return statChangeItem.get(itemID);
    }

    public static PortalScrollItem getPortalScrollItem(int itemID) {
        return portalScrollItem.get(itemID);
    }

    public static UpgradeItem getUpgradeItem(int itemID) {
        return upgradeItem.get(itemID);
    }

    public static String getItemName(int itemID) {
        return itemString.get(itemID);
    }

    public static String getMapName(int mapID) {
        return mapString.get(mapID);
    }

    public static int getBulletPAD(int itemID) {
        BundleItem item = getBundleItem(itemID);
        if (item == null) {
            return 0;
        } else {
            return item.getIncPAD();
        }
    }
    
    public static ItemSlotBase getItemSlot(int itemID, int option) {
        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
        if (ti == ItemType.Equip) {
            EquipItem info = getEquipItem(itemID);
            if (info == null) {
                Logger.logError("Inexistant item [%d]", itemID);
                return null;
            }
            ItemSlotEquip item = new ItemSlotEquip(itemID);
            item.ruc = ItemVariationOption.getVariation(info.getTUC(), option).byteValue();//Total Upgrade Count
            item.iSTR = ItemVariationOption.getVariation(info.getIncSTR(), option).shortValue();
            item.iDEX = ItemVariationOption.getVariation(info.getIncDEX(), option).shortValue();
            item.iINT = ItemVariationOption.getVariation(info.getIncINT(), option).shortValue();
            item.iLUK = ItemVariationOption.getVariation(info.getIncLUK(), option).shortValue();
            item.iMaxHP = ItemVariationOption.getVariation(info.getIncMaxHP(), option).shortValue();
            item.iMaxMP = ItemVariationOption.getVariation(info.getIncMaxMP(), option).shortValue();
            item.iPAD = ItemVariationOption.getVariation(info.getIncPAD(), option).shortValue();
            item.iMAD = ItemVariationOption.getVariation(info.getIncMAD(), option).shortValue();
            item.iPDD = ItemVariationOption.getVariation(info.getIncPDD(), option).shortValue();
            item.iMDD = ItemVariationOption.getVariation(info.getIncMDD(), option).shortValue();
            item.iACC = ItemVariationOption.getVariation(info.getIncACC(), option).shortValue();
            item.iEVA = ItemVariationOption.getVariation(info.getIncEVA(), option).shortValue();
            item.iCraft = ItemVariationOption.getVariation(info.getIncCraft(), option).shortValue();
            item.iSpeed = ItemVariationOption.getVariation(info.getIncSpeed(), option).shortValue();
            item.iJump = ItemVariationOption.getVariation(info.getIncJump(), option).shortValue();
            
            return item.makeClone();
        } else {
            if (ti <= ItemType.Equip) {
                Logger.logError("Inexistant item [%d]", itemID);
                return null;
            }
            if (ti <= ItemType.Etc) {
                BundleItem info = getBundleItem(itemID);
                if (info != null) {
                    ItemSlotBundle item = new ItemSlotBundle(itemID);
                    return item;
                }
            }
        }
        // Cash Items don't exist yet..
        return null;
    }

    public static boolean isCashItem(int itemID) {
        if (ItemAccessor.getItemTypeIndexFromID(itemID) == ItemType.Equip) {
            EquipItem equip = getEquipItem(itemID);
            if (equip != null) {
                return equip.isCash();
            }
        } else {
            BundleItem item = getBundleItem(itemID);
            if (item != null) {
                return item.isCash();
            }
        }
        return false;
    }
    
    public static boolean isTwoHanded(int itemID) {
        int weaponType = itemID / 10000 % 100;
        /*
            TowHand_Sword(40),
            TowHand_Axe(41),
            TowHand_Mace(42),
            Spear(43),
            PoleArm(44),
            Bow(45),
            CrossBow(46),
            ThrowingGloves(47),
        */
        return weaponType >= 40 && weaponType <= 47;
    }

    public static void load() {
        Logger.logReport("Loading Equip Info");
        for (Entry<String, WzPackage> category : characterDir.getChildren().entrySet()) {
            if (!category.getKey().equals("Afterimage")) {
                for (WzProperty itemData : category.getValue().getEntries().values()) {
                    registerEquipItemInfo(itemData);
                }
            }
            category.getValue().release();
        }
        characterDir.release();

        Logger.logReport("Loading Bundle Info");
        iterateBundleItem();
        iterateMapString();
        
        characterDir = null;
        itemDir = null;
        fieldDir = null;
    }

    private static void registerEquipItemInfo(WzProperty itemData) {
        EquipItem item = new EquipItem();
        item.setItemID(Integer.parseInt(itemData.getNodeName().replaceAll(".img", "")));
        //
        WzProperty info = itemData.getNode("info");
        if (info != null) {
            item.setItemName(WzUtil.getString(info.getNode("name"), "NULL"));
            itemString.put(item.getItemID(), item.getItemName());
            item.setReqSTR(WzUtil.getInt32(info.getNode("reqSTR"), 0));
            item.setReqDEX(WzUtil.getInt32(info.getNode("reqDEX"), 0));
            item.setReqINT(WzUtil.getInt32(info.getNode("reqINT"), 0));
            item.setReqLUK(WzUtil.getInt32(info.getNode("reqLUK"), 0));

            item.setSellPrice(WzUtil.getInt32(info.getNode("price"), 0));
            item.setCash(WzUtil.getBoolean(info.getNode("cash"), false));

            item.setIncSTR(WzUtil.getShort(info.getNode("incSTR"), 0));
            item.setIncDEX(WzUtil.getShort(info.getNode("incDEX"), 0));
            item.setIncINT(WzUtil.getShort(info.getNode("incINT"), 0));
            item.setIncLUK(WzUtil.getShort(info.getNode("incLUK"), 0));
            item.setIncMaxHP(WzUtil.getShort(info.getNode("incMHP"), 0));
            item.setIncMaxMP(WzUtil.getShort(info.getNode("incMMP"), 0));

            item.setIncPAD(WzUtil.getShort(info.getNode("incPAD"), 0));
            item.setIncMAD(WzUtil.getShort(info.getNode("incMAD"), 0));
            item.setIncPDD(WzUtil.getShort(info.getNode("incPDD"), 0));
            item.setIncMDD(WzUtil.getShort(info.getNode("incMDD"), 0));

            item.setIncACC(WzUtil.getShort(info.getNode("incACC"), 0));
            item.setIncEVA(WzUtil.getShort(info.getNode("incEVA"), 0));
            item.setIncCraft(WzUtil.getShort(info.getNode("incCraft"), 0));
            item.setIncSpeed(WzUtil.getShort(info.getNode("incSpeed"), 0));
            item.setIncJump(WzUtil.getShort(info.getNode("incJump"), 0));
            item.setIncSwim(WzUtil.getShort(info.getNode("incSwim"), 0));

            item.setKnockback(WzUtil.getInt32(info.getNode("knockback"), 0));
            item.setAttackSpeed(WzUtil.getInt32(info.getNode("attackSpeed"), 0));
            item.setTUC(WzUtil.getInt32(info.getNode("tuc"), 0));
            // vslot, iconRaw, tuc, sfx, incMDD, icon, reqLUK, reqLevel, knockback, reqDEX, incJump, price, attack, incINT, islot, incSTR, incPDD, stand, cash, incMHP, reqPOP, afterImage, incACC, incLUK, nameTag, incMMD, incDEX, reqJob, chatBalloon, incSpeed, attackSpeed, name, incEVA, incMMP, incMAD, incPAD, reqINT, walk, reqSTR, desc

        }
        //
        equipItem.put(item.getItemID(), item);
    }

    private static void iterateBundleItem() {
        String[] category = {"Consume", "Etc"};
        for (String cat : category) {
            WzPackage pack = itemDir.getChildren().get(cat);
            for (WzProperty itemSection : pack.getEntries().values()) {
                for (WzProperty itemData : itemSection.getChildNodes()) {
                    loadBundleItem(itemData);
                }
            }
            pack.release();
        }
        itemDir.release();
    }

    private static void loadBundleItem(WzProperty itemData) {
        BundleItem item = new BundleItem();
        item.setItemID(Integer.parseInt(itemData.getNodeName().replaceAll(".img", "")));
        WzProperty info = itemData.getNode("info");
        if (info != null) {
            item.setItemName(WzUtil.getString(info.getNode("name"), "NULL"));
            itemString.put(item.getItemID(), item.getItemName());
            item.setIncPAD(WzUtil.getShort(info.getNode("incPAD"), 0));
            item.setSellPrice(WzUtil.getInt32(info.getNode("price"), 0));
            item.setUnitPrice(WzUtil.getDouble(info.getNode("unitPrice"), 0));
            item.setCash(WzUtil.getBoolean(info.getNode("cash"), false));
            item.setSlotMax(WzUtil.getInt32(info.getNode("slotMax"), 0));

            // unitPrice, iconRaw, incMDD, icon, incACC, slotMax, incLUK, incDEX, incJump, price, success, incSpeed, name, incINT, incSTR, incPDD, incMAD, incEVA, incPAD, cash, incMHP, desc
        }
        int type = item.getItemID() / 10000;

        switch (type) {
            case 200:
            case 201:
            case 202:
            case 205:
                registerStateChangeItem(item.getItemID(), itemData);
                break;
            case 203:
                registerPortalScrollItem(item.getItemID(), itemData);
                break;
            case 204:
                registerUpgradeItem(item.getItemID(), itemData);
                break;
            // All this shit is useless.
            case 206:// arrows
            case 207:// throwing star
            case 208:// mega phones
            case 209:// weather effect? idk
                break;
            case 400:
            case 401:
            case 402:
            case 403:
            case 404:
            case 405:
                break;
        }
        bundleItem.put(item.getItemID(), item);
    }

    private static void registerStateChangeItem(int itemID, WzProperty itemData) {
        StateChangeItem item = new StateChangeItem();
        item.setItemID(itemID);
        WzProperty specEx = itemData.getNode("specEx");
        if (specEx == null) {
            loadStateChangeInfo(item, itemData);
        } else {
            // Nice joke
        }
        statChangeItem.put(item.getItemID(), item);
    }

    private static void loadStateChangeInfo(StateChangeItem sci, WzProperty itemData) {
        WzProperty spec = itemData.getNode("spec");
        if (spec != null) {
            //acc, eva, mad, pdd, pad, mp, hp, hpR, time, pda, mpR, speed
            sci.setHp(WzUtil.getInt32(spec.getNode("hp"), 0));
            sci.setMp(WzUtil.getInt32(spec.getNode("mp"), 0));
            sci.setHpR(WzUtil.getInt32(spec.getNode("hpR"), 0));
            sci.setMpR(WzUtil.getInt32(spec.getNode("mpR"), 0));

            sci.setAcc(WzUtil.getInt32(spec.getNode("acc"), 0));
            sci.setEva(WzUtil.getInt32(spec.getNode("eva"), 0));
            sci.setMad(WzUtil.getInt32(spec.getNode("mad"), 0));
            sci.setPdd(WzUtil.getInt32(spec.getNode("pdd"), 0));
            sci.setPad(WzUtil.getInt32(spec.getNode("pad"), 0));

            sci.setSpeed(WzUtil.getInt32(spec.getNode("speed"), 0));

            sci.setTime(WzUtil.getInt32(spec.getNode("time"), 0));
        }
    }

    private static void registerPortalScrollItem(int itemID, WzProperty itemData) {
        PortalScrollItem item = new PortalScrollItem();
        item.setItemID(itemID);
        WzProperty spec = itemData.getNode("spec");
        if (spec != null) {
            item.setMoveTo(WzUtil.getInt32(spec.getNode("moveTo"), Field.Invalid));
        }
        portalScrollItem.put(item.getItemID(), item);
    }

    private static void registerUpgradeItem(int itemID, WzProperty itemData) {
        UpgradeItem item = new UpgradeItem();
        item.setItemID(itemID);
        WzProperty info = itemData.getNode("info");
        if (info != null) {
            item.setIncMaxHP(WzUtil.getShort(info.getNode("incMHP"), 0));

            item.setIncSTR(WzUtil.getShort(info.getNode("incSTR"), 0));
            item.setIncDEX(WzUtil.getShort(info.getNode("incDEX"), 0));
            item.setIncINT(WzUtil.getShort(info.getNode("incINT"), 0));
            item.setIncLUK(WzUtil.getShort(info.getNode("incLUK"), 0));

            item.setIncACC(WzUtil.getShort(info.getNode("incACC"), 0));
            item.setIncEVA(WzUtil.getShort(info.getNode("incEVA"), 0));

            item.setIncSpeed(WzUtil.getShort(info.getNode("incSpeed"), 0));
            item.setIncJump(WzUtil.getShort(info.getNode("incJump"), 0));

            item.setIncPAD(WzUtil.getShort(info.getNode("incPAD"), 0));
            item.setIncPDD(WzUtil.getShort(info.getNode("incPDD"), 0));
            item.setIncMAD(WzUtil.getShort(info.getNode("incMAD"), 0));
            item.setIncMDD(WzUtil.getShort(info.getNode("incMDD"), 0));

            item.setSuccess(WzUtil.getByte(info.getNode("success"), 0));
        }
        upgradeItem.put(item.getItemID(), item);
    }

    private static void iterateMapString() {
        for (WzProperty map : fieldDir.getEntries().values()) {
            int mapid = Integer.parseInt(map.getNodeName().replace(".img", ""));
            WzProperty info = map.getNode("info");
            if (info != null) {
                mapString.put(mapid, WzUtil.getString(info.getNode("mapName"), "NULL"));
            }
        }
        fieldDir.release();
    }
}
