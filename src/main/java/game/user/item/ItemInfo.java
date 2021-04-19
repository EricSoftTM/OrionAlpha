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

import common.JobAccessor;
import common.JobCategory;
import common.item.BodyPart;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import common.item.ItemSlotPet;
import common.item.ItemType;
import common.user.CharacterStat.CharacterStatType;
import game.field.Field;
import game.user.stat.CharacterTemporaryStat;
import java.util.HashMap;
import java.util.List;
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

    protected static final Map<Integer, BundleItem> bundleItem;
    protected static final Map<Integer, EquipItem> equipItem;
    protected static final Map<Integer, StateChangeItem> statChangeItem;
    protected static final Map<Integer, PortalScrollItem> portalScrollItem;
    protected static final Map<Integer, UpgradeItem> upgradeItem;
    protected static final Map<Integer, PetFoodItem> petFoodItem;
    protected static final Map<Integer, MobSummonItem> mobSummonItem;
    //
    protected static final Map<Integer, String> mapString;
    protected static final Map<Integer, String> itemString;
    
    static {
        // Initialize Item Containers
        bundleItem = new HashMap<>();
        equipItem = new HashMap<>();
        statChangeItem = new HashMap<>();
        portalScrollItem = new HashMap<>();
        upgradeItem = new HashMap<>();
        petFoodItem = new HashMap<>();
        mobSummonItem = new HashMap<>();
        
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
    
    public static MobSummonItem getMobSummonItem(int itemID) {
        return mobSummonItem.get(itemID);
    }
    
    public static PetFoodItem getPetFoodItem(int itemID) {
        return petFoodItem.get(itemID);
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
    
    public static double getUnitSellPrice(int itemID) {
        BundleItem item;
        if ((item = getBundleItem(itemID)) != null) {
            return item.getUnitPrice();
        }
        return 1.0;
    }
    
    public static int getExclusiveClothesBodyPart(List<ItemSlotBase> a, int itemID) {
        ItemSlotBase excl1 = a.get(BodyPart.Clothes);
        ItemSlotBase excl2 = a.get(BodyPart.Pants);
        if (itemID != 0) {
            if (ItemAccessor.isLongCoat(itemID) && excl2 != null)
                return BodyPart.Pants;
            if (itemID / 10000 == 106 && excl1 != null && ItemAccessor.isLongCoat(excl1.getItemID()))
                return BodyPart.Clothes;
        } else {
            if (excl1 != null && excl2 != null)
                return ItemAccessor.isLongCoat(excl1.getItemID()) ? BodyPart.Clothes : 0;
        }
        return 0;
    }
    
    public static int getExclusiveEquipItemBodyPart(List<ItemSlotBase> a, int itemID) {
        int exclBodyPart = getExclusiveWeaponShieldBodyPart(a, itemID);
        if (exclBodyPart == 0)
            exclBodyPart = getExclusiveClothesBodyPart(a, itemID);
        return exclBodyPart;
    }
    
    public static int getExclusiveWeaponShieldBodyPart(List<ItemSlotBase> a, int itemID) {
        ItemSlotBase excl1 = a.get(BodyPart.Weapon);
        ItemSlotBase excl2 = a.get(BodyPart.Shield);
        if (itemID != 0) {
            if (itemID / 100000 == 14 && excl2 != null)
                return BodyPart.Shield;
            if (itemID / 10000 == 109 && excl1 != null && excl1.getItemID() / 100000 == 14)
                return BodyPart.Weapon;
        } else {
            if (excl2 != null && excl1 != null)
                return excl1.getItemID() / 100000 == 14 ? BodyPart.Weapon : 0;
        }
        return 0;
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
            item.ruc = (byte) ItemVariationOption.getVariation(info.getTUC(), option);//Total Upgrade Count
            item.iSTR = (short) ItemVariationOption.getVariation(info.getIncSTR(), option);
            item.iDEX = (short) ItemVariationOption.getVariation(info.getIncDEX(), option);
            item.iINT = (short) ItemVariationOption.getVariation(info.getIncINT(), option);
            item.iLUK = (short) ItemVariationOption.getVariation(info.getIncLUK(), option);
            item.iMaxHP = (short) ItemVariationOption.getVariation(info.getIncMaxHP(), option);
            item.iMaxMP = (short) ItemVariationOption.getVariation(info.getIncMaxMP(), option);
            item.iPAD = (short) ItemVariationOption.getVariation(info.getIncPAD(), option);
            item.iMAD = (short) ItemVariationOption.getVariation(info.getIncMAD(), option);
            item.iPDD = (short) ItemVariationOption.getVariation(info.getIncPDD(), option);
            item.iMDD = (short) ItemVariationOption.getVariation(info.getIncMDD(), option);
            item.iACC = (short) ItemVariationOption.getVariation(info.getIncACC(), option);
            item.iEVA = (short) ItemVariationOption.getVariation(info.getIncEVA(), option);
            item.iCraft = (short) ItemVariationOption.getVariation(info.getIncCraft(), option);
            item.iSpeed = (short) ItemVariationOption.getVariation(info.getIncSpeed(), option);
            item.iJump = (short) ItemVariationOption.getVariation(info.getIncJump(), option);
            
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
            if (ti == ItemType.Cash) {
                BundleItem info = getBundleItem(itemID);
                if (info != null) {
                    ItemSlotPet item = new ItemSlotPet(itemID);
                    item.setPetName(getItemName(itemID));
                    return item;
                }
            }
        }
        return null;
    }
    
    public static boolean isAbleToEquip(int gender, int level, int job, int STR, int DEX, int INT, int LUK, int pop, ItemSlotPet petItem, int itemID) {
        EquipItem info = getEquipItem(itemID);
        if (info != null) {
            int jobCategory = JobAccessor.getJobCategory(job);
            if (ItemAccessor.isCorrectBodyPart(itemID, BodyPart.PetWear, gender)) {
                if (petItem == null || !info.isItemSuitedForPet(petItem.getItemID())) {
                    return false;
                }
                level = petItem.getLevel();
            }
            int jobBit = 0;
            if (jobCategory == JobCategory.Wizard) {
                // In the actual KMS Alpha client, Magicians are not allowed
                // to equip two-handed weapons.
                int nWT = ItemAccessor.getWeaponType(itemID);
                if (nWT != 0 && nWT != 30 && nWT != 33 && nWT != 31 && nWT != 32 && nWT != 38 && nWT != 37) {
                    return false;
                }
                jobBit = 2;
            } else if (jobCategory != 0) {
                jobBit = 1 << (jobCategory - 1);
            }
            return ItemAccessor.isMatchedItemIDGender(itemID, gender)
                    && level >= info.getReqLevel()
                    && STR >= info.getReqSTR()
                    && DEX >= info.getReqDEX()
                    && INT >= info.getReqINT()
                    && LUK >= info.getReqLUK()
                    && (info.getReqPOP() == 0 || pop >= info.getReqPOP())
                    && (info.getReqJob() == 0 || info.getReqJob() == -1 && jobBit == 0 || info.getReqJob() > 0 && (info.getReqJob() & jobBit) != 0);
        }
        return false;
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
    
    public static boolean isReqUpgradeItem(int uItemID, int eItemID) {
        boolean canUpgrade;
        if (uItemID / 10000 != 204 || ItemAccessor.getItemTypeIndexFromID(eItemID) != ItemType.Equip)
            canUpgrade = false;
        else
            canUpgrade = uItemID % 10000 / 100 == eItemID / 10000 % 100;
        
        UpgradeItem ui = getUpgradeItem(uItemID);
        if (canUpgrade && ui != null) {
            //if (ui.lnReqItemID.size() > 0) {
            //    return ui.lnReqItemID.contains(eItemID);
            //}
            return true;
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
        WzPackage characterDir = new WzFileSystem().init("Character").getPackage();
        if (characterDir != null) {
            for (Entry<String, WzPackage> category : characterDir.getChildren().entrySet()) {
                if (!category.getKey().equals("Afterimage")) {
                    for (WzProperty itemData : category.getValue().getEntries().values()) {
                        registerEquipItemInfo(itemData);
                    }
                }
                category.getValue().release();
            }
            characterDir.release();
        }
        characterDir = null;

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
            item.setReqPOP(WzUtil.getInt32(info.getNode("reqPOP"), 0));
            item.setReqJob(WzUtil.getInt32(info.getNode("reqJob"), 0));
            item.setReqLevel(WzUtil.getInt32(info.getNode("reqLevel"), 0));

            item.setSellPrice(WzUtil.getInt32(info.getNode("price"), 0));
            item.setCash(WzUtil.getBoolean(info.getNode("cash"), false));

            item.setIncSTR(WzUtil.getShort(info.getNode("incSTR"), 0));
            item.setIncDEX(WzUtil.getShort(info.getNode("incDEX"), 0));
            item.setIncINT(WzUtil.getShort(info.getNode("incINT"), 0));
            item.setIncLUK(WzUtil.getShort(info.getNode("incLUK"), 0));
            item.setIncMaxHP(WzUtil.getShort(info.getNode("incMHP"), 0));
            item.setIncMaxMP(WzUtil.getShort(info.getNode("incMMP"), WzUtil.getShort(info.getNode("incMMD"), 0)));

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
            item.setRecovery(WzUtil.getFloat(info.getNode("recovery"), 1.0f));
            // vslot, iconRaw, tuc, sfx, incMDD, icon, reqLUK, reqLevel, knockback, reqDEX, incJump, price, attack, incINT, islot, incSTR, incPDD, stand, cash, incMHP, reqPOP, afterImage, incACC, incLUK, nameTag, incMMD, incDEX, reqJob, chatBalloon, incSpeed, attackSpeed, name, incEVA, incMMP, incMAD, incPAD, reqINT, walk, reqSTR, desc
    
            if (item.getItemID() / 10000 == 180 && item.getItemID() % 1000 < 100) {
                for (WzProperty prop : itemData.getChildNodes()) {
                    if (prop.getNodeName().equals("info")) {
                        continue;
                    }
                    int templateID = Integer.parseInt(prop.getNodeName());
                    int flag = 1 << (templateID % 100);
                    if (templateID / 10000 != 500 || item.isItemSuitedForPet(templateID)) {
                        continue;
                    }
                    item.addPetTemplateFlag(flag);
                }
            }
        }
        //
        equipItem.put(item.getItemID(), item);
    }

    private static void iterateBundleItem() {
        WzPackage itemDir = new WzFileSystem().init("Item").getPackage();
        if (itemDir != null) {
            String[] category = {"Consume", "Etc", "Pet"};
            for (String cat : category) {
                WzPackage pack = itemDir.getChildren().get(cat);
                for (WzProperty itemSection : pack.getEntries().values()) {
                    if (cat.equals("Pet")) {
                        loadBundleItem(itemSection);
                    } else {
                        for (WzProperty itemData : itemSection.getChildNodes()) {
                            loadBundleItem(itemData);
                        }
                    }
                }
                pack.release();
            }
            itemDir.release();
        }
        itemDir = null;
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
            item.setSlotMax(WzUtil.getInt32(info.getNode("slotMax"), 200));

            // unitPrice, iconRaw, incMDD, icon, incACC, slotMax, incLUK, incDEX, incJump, price, success, incSpeed, name, incINT, incSTR, incPDD, incMAD, incEVA, incPAD, cash, incMHP, desc
        }

        if (ItemAccessor.isStateChangeItem(item.getItemID())) {
            registerStateChangeItem(item.getItemID(), itemData);
        } else if (ItemAccessor.isUpgradeItem(item.getItemID())) {
            registerUpgradeItem(item.getItemID(), itemData);
        } else if (ItemAccessor.isPortalScrollItem(item.getItemID())) {
            registerPortalScrollItem(item.getItemID(), itemData);
        } else if (ItemAccessor.isPetFoodItem(item.getItemID())) {
            registerPetFoodItem(item.getItemID(), itemData);
        } else if (ItemAccessor.isMobSummonItem(item.getItemID())) {
            registerMobSummonItem(item.getItemID(), itemData);
        } else if (ItemAccessor.isWeatherItem(item.getItemID())) {
            // wonder if 'CashItem' should add this and megaphone (208)..
        }
        
        bundleItem.put(item.getItemID(), item);
    }

    private static void registerStateChangeItem(int itemID, WzProperty itemData) {
        StateChangeItem item = new StateChangeItem();
        item.setItemID(itemID);
        WzProperty specEx = itemData.getNode("specEx");
        if (specEx == null) {
            loadStateChangeInfo(item.getInfo(), itemData);
        } else {
            // Nice joke
        }
        statChangeItem.put(item.getItemID(), item);
    }

    private static void loadStateChangeInfo(StateChangeInfo sci, WzProperty itemData) {
        WzProperty spec = itemData.getNode("spec");
        if (spec != null) {
            sci.setFlagRate(0);
            sci.setFlag(0);
            
            //acc, eva, mad, pdd, pad, mp, hp, hpR, time, pda, mpR, speed
            int hpR = WzUtil.getInt32(spec.getNode("hpR"), 0);
            int hp = WzUtil.getInt32(spec.getNode("hp"), 0);
            if (hpR != 0) {
                hp = hpR;
                sci.addFlagRate(CharacterStatType.HP);
            }
            sci.setHP(hp);
            if (sci.getHP() != 0) {
                sci.addFlag(CharacterStatType.HP);
            }
            
            int mpR = WzUtil.getInt32(spec.getNode("mpR"), 0);
            int mp = WzUtil.getInt32(spec.getNode("mp"), 0);
            if (mpR != 0) {
                mp = mpR;
                sci.addFlagRate(CharacterStatType.MP);
            }
            sci.setMP(mp);
            if (sci.getMP() != 0) {
                sci.addFlag(CharacterStatType.MP);
            }
            
            sci.setACC(WzUtil.getInt32(spec.getNode("acc"), 0));
            if (sci.getACC() != 0) {
                sci.addFlagTemp(CharacterTemporaryStat.ACC);
            }
            sci.setEVA(WzUtil.getInt32(spec.getNode("eva"), 0));
            if (sci.getEVA() != 0) {
                sci.addFlagTemp(CharacterTemporaryStat.EVA);
            }
            sci.setMAD(WzUtil.getInt32(spec.getNode("mad"), 0));
            if (sci.getMAD() != 0) {
                sci.addFlagTemp(CharacterTemporaryStat.MAD);
            }
            sci.setPDD(WzUtil.getInt32(spec.getNode("pdd"), 0));
            if (sci.getPDD() != 0) {
                sci.addFlagTemp(CharacterTemporaryStat.PDD);
            }
            sci.setPAD(WzUtil.getInt32(spec.getNode("pad"), 0));
            if (sci.getPAD() != 0) {
                sci.addFlagTemp(CharacterTemporaryStat.PAD);
            }
            sci.setSpeed(WzUtil.getInt32(spec.getNode("speed"), 0));
            if (sci.getSpeed() != 0) {
                sci.addFlagTemp(CharacterTemporaryStat.Speed);
            }

            sci.setTime(WzUtil.getInt32(spec.getNode("time"), 0));
        }
    }
    
    private static void registerMobSummonItem(int itemID, WzProperty itemData) {
        MobSummonItem item = new MobSummonItem();
        item.setItemID(itemID);
        WzProperty info = itemData.getNode("info");
        if (info != null) {
            item.setType(WzUtil.getInt32(info.getNode("type"), 1));
            WzProperty mobs = itemData.getNode("mob");
            if (mobs != null) {
                for (WzProperty mob : mobs.getChildNodes()) {
                    int mobID = WzUtil.getInt32(mob.getNode("id"), 0);
                    int prob = WzUtil.getInt32(mob.getNode("prob"), 0);
                    item.getMobs().add(new MobEntry(mobID, prob));
                }
            }
        }
        mobSummonItem.put(item.getItemID(), item);
    }
    
    private static void registerPetFoodItem(int itemID, WzProperty itemData) {
        PetFoodItem item = new PetFoodItem();
        item.setItemID(itemID);
        WzProperty spec = itemData.getNode("spec");
        if (spec != null) {
            item.setIncRepleteness(WzUtil.getInt32(spec.getNode("inc"), 0));
            for (int i = 0; ; i++) {
                WzProperty pet = spec.getNode(String.valueOf(i));
                if (pet == null) {
                    break;
                }
                item.getPets().add(WzUtil.getInt32(pet, 0));
            }
        }
        petFoodItem.put(item.getItemID(), item);
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

    // Disabled since this is just eating up memory and isn't even used yet.
    private static void iterateMapString() {
        /*
        WzPackage fieldDir = new WzFileSystem().init("Map/Map").getPackage();
        if (fieldDir != null) {
            for (WzProperty map : fieldDir.getEntries().values()) {
                WzProperty info = map.getNode("info");
                if (info != null) {
                    int mapID = Integer.parseInt(map.getNodeName().replace(".img", ""));
                    mapString.put(mapID, WzUtil.getString(info.getNode("mapName"), "NULL"));
                }
                map.release();
            }
            fieldDir.release();
        }
        fieldDir = null;
        */
    }
}
