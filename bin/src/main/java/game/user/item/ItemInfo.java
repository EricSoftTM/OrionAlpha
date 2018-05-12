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

    private static final WzPackage characterDir = new WzFileSystem().init("Character").getPackage();
    private static final WzPackage itemDir = new WzFileSystem().init("Item").getPackage();
    private static final WzPackage fieldDir = new WzFileSystem().init("Map/Map").getPackage();
    protected static final Map<Integer, BundleItem> bundleItem = new HashMap<>();
    protected static final Map<Integer, EquipItem> equipItem = new HashMap<>();
    protected static final Map<Integer, StateChangeItem> statChangeItem = new HashMap<>();
    protected static final Map<Integer, PortalScrollItem> portalScrollItem = new HashMap<>();
    protected static final Map<Integer, UpgradeItem> upgradeItem = new HashMap<>();
    //
    protected static final Map<Integer, String> mapString = new HashMap<>();
    protected static final Map<Integer, String> itemString = new HashMap<>();

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

    public static boolean isCashItem(int itemID) {
        if (itemID / 1000000 == 5) {
            return true;
        }
        BundleItem item = getBundleItem(itemID);
        if (item != null) {
            return item.isCash();
        }
        return false;
    }

    public static void load() {
        Logger.logReport("Loading Equip Info");
        for (Entry<String, WzPackage> category : characterDir.getChildren().entrySet()) {
            if (category.getKey().equals("Afterimage")) {
                continue;
            }
            // int size = equipItem.size();
            for (WzProperty itemData : category.getValue().getEntries().values()) {
                registerEquipItemInfo(itemData);
            }
            // Logger.logReport("%d Item(s) loaded successfully from [%S]", equipItem.size() - size, category.getKey());
        }

        Logger.logReport("Loading Bundle Info");
        iterateBundleItem();
        iterateMapString();
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
            item.setTuc(WzUtil.getInt32(info.getNode("tuc"), 0));
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
        }
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
    }
}
