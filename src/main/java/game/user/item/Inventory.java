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

import common.Request;
import common.item.BodyPart;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import common.item.ItemType;
import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import common.user.DBChar;
import game.GameApp;
import game.field.drop.Reward;
import game.field.drop.RewardType;
import game.user.User;
import game.user.WvsContext;
import game.user.skill.SkillInfo;
import game.user.stat.CharacterTemporaryStat;
import java.util.ArrayList;
import java.util.List;
import util.Logger;
import util.Pointer;
import util.Rand32;

/**
 *
 * @author Eric
 */
public class Inventory {
    
    public static boolean changeSlotPosition(User user, byte onExclRequest, byte ti, short pos1, short pos2, short count) {
        if (user.getHP() == 0)
            return false;
        if (ti <= ItemType.NotDefine || ti >= ItemType.NO) {
            Logger.logError("Invalid Item Type Index %d from user %s", ti, user.getCharacterName());
            return false;
        }
        List<ChangeLog> changeLog = new ArrayList<>();
        if (user.lock()) {
            try {
                if (user.getHP() > 0 && pos1 != pos2) {
                    if (pos2 == 0) {
                        if (user.getField() != null) {
                            ItemSlotBase item = user.getCharacter().getItem(ti, pos1);
                            if (item != null) {
                                if (ItemAccessor.isTreatSingly(item)) {
                                    count = 1;
                                }
                                Pointer<ItemSlotBase> itemRemoved = new Pointer<>();
                                Pointer<Integer> decRet = new Pointer<>(0);
                                if (InventoryManipulator.rawRemoveItem(user.getCharacter(), ti, pos1, count, changeLog, decRet, itemRemoved)) {
                                    if (ti == ItemType.Equip && pos1 < 0)
                                        user.validateStat(false);
                                    if (itemRemoved.get() != null && user.getField() != null) {
                                        Reward reward = new Reward(RewardType.ITEM, itemRemoved.get(), 0, 0);
                                        user.getField().getDropPool().create(reward, user.getCharacterID(), 0, user.getCurrentPosition().x, user.getCurrentPosition().y, user.getCurrentPosition().x, 0, 0, user.isGM(), 0);
                                    }
                                }
                            }
                        }
                    }
                    int b = user.getCharacter().getItemSlotCount(ti);
                    if ((pos1 > 0 && pos1 <= b) && (pos2 > 0 && pos2 <= b)) {
                        ItemSlotBase item = user.getCharacter().getItem(ti, pos1);
                        ItemSlotBase item2;
                        if (item != null) {
                            if ((item2 = user.getCharacter().getItem(ti, pos2)) == null || ItemAccessor.isTreatSingly(item) || item.getItemID() != item2.getItemID()) {
                                if (user.getCharacter().getItemTrading().get(ti).get(pos1) <= 0 && user.getCharacter().getItemTrading().get(ti).get(pos2) <= 0) {
                                    user.getCharacter().setItem(ti, pos1, item2);//zswap the two objects
                                    user.getCharacter().setItem(ti, pos2, item);
                                    InventoryManipulator.insertChangeLog(changeLog, ChangeLog.Position, ti, pos1, null, pos2, (short) 0);
                                }
                            } else {
                                item2 = user.getCharacter().getItem(ti, pos2);
                                if (ItemInfo.getBundleItem(item.getItemID()) != null) {
                                    int slotMax = SkillInfo.getInstance().getBundleItemMaxPerSlot(item.getItemID(), user.getCharacter()) - item2.getItemNumber();
                                    if (slotMax <= 0)
                                        slotMax = 0;
                                    int number = item.getItemNumber();
                                    if (slotMax < number)
                                        number = slotMax;
                                    Pointer<Integer> decRet = new Pointer<>(0);
                                    if (Inventory.rawRemoveItem(user, ti, pos1, (short) number, changeLog, decRet, null)) {
                                        count = (short) (item2.getItemNumber() - user.getCharacter().getItemTrading().get(ti).get(pos2));
                                        if (count > 0) {
                                            InventoryManipulator.insertChangeLog(changeLog, ChangeLog.ItemNumber, ti, pos2, null, (short) 0, (short) (count + decRet.get()));
                                        } else {
                                            InventoryManipulator.insertChangeLog(changeLog, ChangeLog.NewItem, ti, pos2, item2, (short) 0, decRet.get().shortValue());
                                        }
                                        item2.setItemNumber((short) (item2.getItemNumber() + decRet.get()));
                                    }
                                }
                            }
                        }
                    } else {
                        if (ti == ItemType.Equip) {
                            if (pos1 > 0 && pos2 < 0) {
                                equip(user, pos1, pos2, changeLog);
                            } else if (pos1 < 0 && pos2 > 0) {
                                unequip(user, pos1, pos2, changeLog);
                            }
                        }
                    }
                    if (!changeLog.isEmpty()) {
                        user.addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                    }
                    sendInventoryOperation(user, onExclRequest, changeLog);
                    return true;
                } else {
                    sendInventoryOperation(user, onExclRequest, changeLog);
                    return false;
                }
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    private static void equip(User user, short pos1, short pos2, List<ChangeLog> changeLog) {
        ItemSlotEquip item = (ItemSlotEquip) user.getCharacter().getItem(ItemType.Equip, pos1);
        if (item == null) {
            return;
        }
        if (pos2 == -BodyPart.Pants) { // unequip the overall
            ItemSlotBase top = user.getCharacter().getItem(ItemType.Equip, BodyPart.Clothes);
            if (top != null && ItemAccessor.isLongCoat(top.getItemID())) {
                if (Inventory.getRoomCountInSlot(user, ItemType.Equip) == 0) {
                    return;
                }
                int pos = user.getCharacter().findEmptySlotPosition(ItemType.Equip);
                unequip(user, -BodyPart.Clothes, pos, changeLog);
            }
        } else if (pos2 == -BodyPart.Clothes) {
            ItemSlotBase bottom = user.getCharacter().getItem(ItemType.Equip, BodyPart.Pants);
            if (bottom != null && ItemAccessor.isLongCoat(item.getItemID())) {
                if (Inventory.getRoomCountInSlot(user, ItemType.Equip) == 0) {
                    return;
                }
                int pos = user.getCharacter().findEmptySlotPosition(ItemType.Equip);
                unequip(user, -BodyPart.Pants, pos, changeLog);
            }
        } else if (pos2 == -BodyPart.Shield) {// check if weapon is two-handed
            ItemSlotBase weapon = user.getCharacter().getItem(ItemType.Equip, BodyPart.Weapon);
            if (weapon != null && ItemInfo.isTwoHanded(weapon.getItemID())) {
                if (Inventory.getRoomCountInSlot(user, ItemType.Equip) == 0) {
                    return;
                }
                int pos = user.getCharacter().findEmptySlotPosition(ItemType.Equip);
                unequip(user, -BodyPart.Weapon, pos, changeLog);
            }
        } else if (pos2 == -BodyPart.Weapon) {
            ItemSlotBase shield = user.getCharacter().getItem(ItemType.Equip, BodyPart.Shield);
            if (shield != null && ItemInfo.isTwoHanded(item.getItemID())) {
                if (Inventory.getRoomCountInSlot(user, ItemType.Equip) == 0) {
                    return;
                }
                int pos = user.getCharacter().findEmptySlotPosition(ItemType.Equip);
                unequip(user, -BodyPart.Shield, pos, changeLog);
            }
        }
        ItemSlotEquip itemSlot = (ItemSlotEquip) user.getCharacter().getItem(ItemType.Equip, pos2);
        user.getCharacter().setItem(ItemType.Equip, pos1, itemSlot);
        user.getCharacter().setItem(ItemType.Equip, pos2, item);
        InventoryManipulator.insertChangeLog(changeLog, ChangeLog.Position, ItemType.Equip, pos2, item, pos1, (short) 0);
        
        if (user.getSecondaryStat().getStatOption(CharacterTemporaryStat.Booster) != 0 && ItemAccessor.isWeapon(item.getItemID())) {
            user.sendTemporaryStatReset(user.getSecondaryStat().resetByCTS(CharacterTemporaryStat.Booster));
        }
        user.validateStat(false);
    }
    
    private static void unequip(User user, int pos1, int pos2, List<ChangeLog> changeLog) {
        ItemSlotEquip source = (ItemSlotEquip) user.getCharacter().getItem(ItemType.Equip, pos1);
        ItemSlotEquip target = (ItemSlotEquip) user.getCharacter().getItem(ItemType.Equip, pos2);
        if (pos2 < 0) {
            Logger.logError("Unequipping to negative slot.");
            return;
        }
        if (source == null) {
            return;
        }
        if (target != null && pos1 <= 0) {
            return;
        }
        user.getCharacter().setItem(ItemType.Equip, pos1, null);
        if (target != null) {
            user.getCharacter().setItem(ItemType.Equip, pos2, null);
        }
        user.getCharacter().setItem(ItemType.Equip, pos2, source);
        if (target != null) {
            user.getCharacter().setItem(ItemType.Equip, pos1, target);
        }
        InventoryManipulator.insertChangeLog(changeLog, ChangeLog.Position, ItemType.Equip, (short) pos2, source, (short) pos1, (short) 0);
        user.validateStat(false);
    }
    
    public static boolean exchange(User user, int money, List<ExchangeElem> exchange, List<ChangeLog> logAdd, List<ChangeLog> logRemove) {
        if (user.lock()) {
            try {
                if (user.getHP() == 0) {
                    return false;
                }
                List<ChangeLog> logDef = new ArrayList<>();
                Pointer<Integer> modFlag = new Pointer<>(0);
                boolean aExchangea = InventoryManipulator.rawExchange(user.getCharacter(), money, exchange, logAdd, logRemove, modFlag, logDef);
                if (aExchangea) {
                    if (money != 0) {
                        user.addCharacterDataMod(DBChar.Character);
                        user.sendCharacterStat(Request.None, CharacterStatType.Money);
                    }
                    //if (!logAdd.isEmpty() && !logRemove.isEmpty())
                    if (logAdd == null && logRemove == null)
                        sendInventoryOperation(user, Request.None, logDef);
                    user.addCharacterDataMod(modFlag.get());
                }
                return aExchangea;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    public static int getItemCount(User user, int itemID) {
        byte ti = ItemAccessor.getItemTypeIndexFromID(itemID);
        if (ti <= ItemType.NotDefine || ti >= ItemType.NO) {
            return 0;
        } else {
            if (user.lock()) {
                try {
                    short count = 0;
                    int nSlotCount = user.getCharacter().getItemSlotCount(ti);
                    for (int i = 1; i <= nSlotCount; i++) {
                        ItemSlotBase item = user.getCharacter().getItemSlot().get(ti).get(i);
                        if (item != null && item.getItemID() == itemID) {
                            if (ti != ItemType.Consume && ti != ItemType.Install && ti != ItemType.Etc) {
                                count += (user.getCharacter().getItemTrading().get(ti).get(i) == 0) ? 1 : 0;
                            } else {
                                count += Math.max(item.getItemNumber() - user.getCharacter().getItemTrading().get(ti).get(i), 0);
                            }
                        }
                    }
                    return count;
                } finally {
                    user.unlock();
                }
            }
            return 0;
        }
    }
    
    public static byte getRoomCountInSlot(User user, int ti) {
        if (user.lock()) {
            try {
                byte count = 0;
                int slotCount = user.getCharacter().getItemSlotCount(ti);
                if (slotCount >= 1) {
                    for (int i = 1; i <= slotCount; i++) {
                        if (user.getCharacter().getItemSlot(ti).get(i) == null)
                            ++count;
                    }
                }
                return count;
            } finally {
                user.unlock();
            }
        }
        return 0;
    }
    
    public static boolean incItemSlotCount(User user, byte ti, int inc) {
        if (ti <= ItemType.NotDefine || ti >= ItemType.NO)
            return false;
        if (user.lock()) {
            try {
                int slotCount = user.getCharacter().getItemSlotCount(ti);
                int slotInc = Math.min(slotCount + inc, 80);
                if (slotCount >= slotInc) {
                    return false;
                }
                for (int i = 0; i < inc; i++) {
                    user.getCharacter().getItemSlot().get(ti).add(null);
                    user.getCharacter().getItemTrading().get(ti).add(0);
                }
                user.addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                user.sendPacket(WvsContext.onInventoryGrow(ti, slotInc));
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    public static ItemSlotBase moveItemToTemp(User user, byte ti, short pos, short number) {
        List<ChangeLog> changeLog = new ArrayList<>();
        Pointer<ItemSlotBase> itemCopyed = new Pointer<>();
        rawMoveItemToTemp(user, ti, pos, number, itemCopyed, changeLog);
        sendInventoryOperation(user, Request.Excl, changeLog);
        changeLog.clear();
        return itemCopyed.get();
    }
    
    public static int moveMoneyToTemp(User user, int amount) {
        if (user.lock()) {
            try {
                int moneyTrading = user.getCharacter().getMoneyTrading();
                if (amount <= 0 || user.getCharacter().getCharacterStat().getMoney() - moneyTrading < amount) {
                    Logger.logError("Invalid amount of money when move temporary space [%s] (%d)", user.getCharacterName(), amount);
                    return 0;
                } else {
                    user.getCharacter().setMoneyTrading(amount + moneyTrading);
                    user.sendCharacterStat(Request.Excl, CharacterStatType.Money);
                    return user.getCharacter().getMoneyTrading();
                }
            } finally {
                user.unlock();
            }
        }
        return 0;
    }
    
    public static boolean rawAddItem(User user, byte ti, ItemSlotBase item, List<ChangeLog> changeLog, Pointer<Integer> incRet) {
        if (user.getHP() == 0) {
            return false;
        }
        return InventoryManipulator.rawAddItem(user.getCharacter(), ti, item, changeLog, incRet);
    }
    
    public static boolean rawMoveItemToTemp(User user, byte ti, short pos, short number, Pointer<ItemSlotBase> itemCopyed, List<ChangeLog> changeLog) {
        if (user.lock()) {
            try {
                CharacterData cd = user.getCharacter();
                ItemSlotBase item = cd.getItem(ti, pos);
                if (item == null || ti != ItemType.Consume && ti != ItemType.Install && ti != ItemType.Etc && pos < 1) {
                    Logger.logError("Invalid Item Position when move temporary space [%s] (%d, %d)", cd.getCharacterStat().getName(), ti, pos);
                    return false;
                }
                if (ItemInfo.isCashItem(item.getItemID()) || item.getCashItemSN() > 0) {
                    Logger.logError("Invalid Item when move temporary space [%s] (%d,%d:%d)", cd.getCharacterStat().getName(), ti, pos, item.getItemID());
                    return false;
                }
                if (ItemAccessor.isTreatSingly(item)) {
                    if (number != 1 || cd.getItemTrading().get(ti).get(pos) > 0) {//only 1 here
                        Logger.logError("Invalid Item Count when move temporary space [%s] (%d,%d) Total:%d, Packet:%d");
                        return false;
                    }
                    cd.getItemTrading().get(ti).set(pos, 1);
                    itemCopyed.set(item.makeClone());
                    InventoryManipulator.insertChangeLog(changeLog, ChangeLog.DelItem, ti, pos, null, (short) 0, (short) 0);
                } else {
                    int trading = cd.getItemTrading().get(ti).get(pos);
                    int remain = item.getItemNumber() - trading;
                    if (remain < number || number < 1) {
                        Logger.logError("Invalid Item Count when move temporary space [%s] (%d,%d) Total:%d, Packet:%d");
                        return false;
                    }
                    cd.getItemTrading().get(ti).set(pos, trading + number);
                    itemCopyed.set(item.makeClone());
                    itemCopyed.get().setItemNumber(number);
                    InventoryManipulator.insertChangeLog(changeLog, (remain - number) != 0 ? ChangeLog.ItemNumber : ChangeLog.DelItem, ti, pos, null, (short) 0, (short) (remain - number));
                }
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    public static boolean rawRechargeItem(User user, short pos, List<ChangeLog> changeLog) {
        if (user.getHP() == 0) {
            return false;
        }
        return InventoryManipulator.rawRechargeItem(user.getCharacter(), pos, changeLog);
    }
    
    public static boolean rawRemoveItem(User user, byte ti, short pos, short count, List<ChangeLog> changeLog, Pointer<Integer> decRet, Pointer<ItemSlotBase> itemRemoved) {
        if (user.getHP() > 0)
            return InventoryManipulator.rawRemoveItem(user.getCharacter(), ti, pos, count, changeLog, decRet, itemRemoved);
        return false;
    }
    
    public static boolean rawWasteItem(User user, short pos, short count, List<ChangeLog> changeLog) {
        if (user.getHP() > 0)
            return InventoryManipulator.rawWasteItem(user.getCharacter(), pos, count, changeLog);
        return false;
    }
    
    public static void restoreFromTemp(User user) {
        if (user.lock()) {
            try {
                if (user.getCharacter().getMoneyTrading() > 0) {
                    user.getCharacter().setMoneyTrading(0);
                    user.sendCharacterStat(Request.None, CharacterStatType.Money);
                }
                List<ChangeLog> changeLog = new ArrayList<>();
                for (int ti = ItemType.Equip; ti <= ItemType.Etc; ti++) {
                    for (int i = 1; i <= user.getCharacter().getItemSlotCount(ti); i++) {
                        int number = user.getCharacter().getItemTrading().get(ti).get(i);
                        if (number > 0) {
                            ItemSlotBase item = user.getCharacter().getItem((byte) ti, i);
                            if (item != null) {
                                if (ItemAccessor.isRechargeableItem(item.getItemID())) {
                                    number = item.getItemNumber();
                                }
                                InventoryManipulator.insertChangeLog(changeLog, number != item.getItemNumber() ? ChangeLog.ItemNumber : ChangeLog.NewItem, (byte) ti, (short) i, item, (short) 0, item.getItemNumber());
                            }
                        }
                    }
                }
                Inventory.sendInventoryOperation(user, Request.None, changeLog);
                changeLog.clear();
            } finally {
                user.unlock();
            }
        }
    }
    
    public static boolean restoreItemFromTemp(User user, byte onExclRequest, byte ti, short pos, short number) {
        if (user.lock()) {
            try {
                List<ChangeLog> changeLog = new ArrayList<>();
                boolean success = false;
                if (ti >= ItemType.Equip && ti <= ItemType.Etc && pos > 0) {
                    if (pos <= user.getCharacter().getItemSlotCount(ti)) {
                        ItemSlotBase item = user.getCharacter().getItem(ti, pos);
                        if (item != null) {
                            short trading = user.getCharacter().getItemTrading().get(ti).get(pos).shortValue();
                            short remain = item.getItemNumber();
                            if (ItemAccessor.isTreatSingly(item)) {
                                if (number == 1) {
                                    user.getCharacter().getItemTrading().get(ti).set(pos, 0);
                                    InventoryManipulator.insertChangeLog(changeLog, ChangeLog.NewItem, ti, pos, item.makeClone(), (short) 0, remain);
                                    success = true;
                                }
                            } else {
                                if (trading >= number) {
                                    user.getCharacter().getItemTrading().get(ti).set(pos, trading - number);
                                    remain -= trading;
                                    InventoryManipulator.insertChangeLog(changeLog, item.getItemNumber() <= trading ? ChangeLog.NewItem : ChangeLog.ItemNumber, ti, pos, item.makeClone(), (short) 0, remain);
                                    success = true;
                                }
                            }
                        }
                    }
                }
                sendInventoryOperation(user, onExclRequest, changeLog);
                changeLog.clear();
                return success;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    public static boolean restoreMoneyFromTemp(User user, byte onExclRequest, int amount) {
        if (user.lock()) {
            try {
                int moneyTrading = user.getCharacter().getMoneyTrading();
                if (moneyTrading <= 0 || amount <= 0 || moneyTrading < amount) {
                    Logger.logError("Cannot restore money from temp (%d/%d)", amount, moneyTrading);
                    user.closeSocket();
                    return false;
                }
                user.getCharacter().setMoneyTrading(moneyTrading - amount);
                user.sendCharacterStat(onExclRequest, CharacterStatType.Money);
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    public static void sendInventoryOperation(User user, byte onExclRequest, List<ChangeLog> changeLog) {
        user.sendPacket(InventoryManipulator.makeInventoryOperation(onExclRequest, changeLog));
    }
    
    public static boolean upgradeEquip(User user, short upos, short epos) {
        CharacterData cd = user.getCharacter();
        List<ChangeLog> changeLog = new ArrayList<>();
        boolean success = false;
        boolean scrolled = false;
        if (cd.getCharacterStat().getHP() > 0) {
            ItemSlotBase useItem = cd.getItem(ItemType.Consume, upos);
            if (useItem == null) {
                return false;
            }
            ItemSlotBase equipItem = cd.getItem(ItemType.Equip, epos);
            if (equipItem != null) {
                if (ItemInfo.isReqUpgradeItem(useItem.getItemID(), equipItem.getItemID())) {
                    if (((ItemSlotEquip) equipItem).ruc > 0) {
                        UpgradeItem info = ItemInfo.getUpgradeItem(useItem.getItemID());
                        if (info != null) {
                            Pointer<Integer> decRet = new Pointer<>(0);
                            if (InventoryManipulator.rawRemoveItem(cd, ItemType.Consume, upos, 1, changeLog, decRet, null) && decRet.get() == 1) {
                                ItemSlotEquip item = (ItemSlotEquip) equipItem;
                                --item.ruc;
                                scrolled = true;
                                if (Rand32.getInstance().random() % 101 <= info.getSuccess()) {
                                    ++item.cuc;
                                    item.iSTR = (short) Math.min(Math.max(item.iSTR + info.getIncSTR(), 0), 9999);
                                    item.iDEX = (short) Math.min(Math.max(item.iDEX + info.getIncDEX(), 0), 9999);
                                    item.iINT = (short) Math.min(Math.max(item.iINT + info.getIncINT(), 0), 9999);
                                    item.iLUK = (short) Math.min(Math.max(item.iLUK + info.getIncLUK(), 0), 9999);
                                    item.iMaxHP = (short) Math.min(Math.max(item.iMaxHP + info.getIncMaxHP(), 0), 9999);
                                    //item.iMaxMP = Math.min(Math.max(item.iMaxMP + info.getIncMaxMP(), 0), 9999);
                                    item.iPAD = (short) Math.min(Math.max(item.iPAD + info.getIncPAD(), 0), 255);
                                    item.iMAD = (short) Math.min(Math.max(item.iMAD + info.getIncMAD(), 0), 255);
                                    item.iPDD = (short) Math.min(Math.max(item.iPDD + info.getIncPDD(), 0), 255);
                                    item.iMDD = (short) Math.min(Math.max(item.iMDD + info.getIncMDD(), 0), 255);
                                    item.iACC = (short) Math.min(Math.max(item.iACC + info.getIncACC(), 0), 255);
                                    item.iEVA = (short) Math.min(Math.max(item.iEVA + info.getIncEVA(), 0), 255);
                                    //item.iCraft = (short) Math.min(Math.max(item.iCraft + info.getIncCraft(), 0), 255);
                                    item.iSpeed = (short) Math.min(Math.max(item.iSpeed + info.getIncSpeed(), 0), 40);
                                    item.iJump = (short) Math.min(Math.max(item.iJump + info.getIncJump(), 0), 23);
                                }
                                InventoryManipulator.insertChangeLog(changeLog, ChangeLog.DelItem, ItemType.Equip, epos, null, (short) 0, (short) 0);
                                InventoryManipulator.insertChangeLog(changeLog, ChangeLog.NewItem, ItemType.Equip, epos, item, (short) 0, (short) 0);
                                user.validateStat(false);
                                success = true;
                            }
                        }
                    }
                }
            }
        }
        user.addCharacterDataMod(DBChar.ItemSlotEquip | DBChar.ItemSlotConsume);
        user.sendPacket(InventoryManipulator.makeInventoryOperation(Request.Excl, changeLog));
        if (scrolled) {
            /*OutPacket oPacket = UserCommon.ShowItemUpgradeEffect(user.dwCharacterID, bUpgrade, bCursed);
            if (bScrolled) {
                user.getField().splitSendPacket(user.getSplit(), oPacket, null);
            } else {
                user.sendPacket(oPacket);
            }*/
        }
        changeLog.clear();
        return success;
    }
    
    public static boolean wasteItem(User user, int itemID, short count) {
        byte ti;
        if (!ItemAccessor.isRechargeableItem(itemID) || (ti = ItemAccessor.getItemTypeIndexFromID(itemID)) <= ItemType.NotDefine || ti >= ItemType.NO)
            return false;
        List<List<ItemSlotBase>> backup = new ArrayList<>();
        List<List<Integer>> backupTrading = new ArrayList<>();
        user.getCharacter().backupItemSlot(backup, backupTrading);
        
        if (user.lock()) {
            try {
                List<ChangeLog> changeLog = new ArrayList<>();
                for (int pos = 1; pos <= user.getCharacter().getItemSlotCount(ti); pos++) {
                    ItemSlotBase pItem = user.getCharacter().getItemSlot().get(ti).get(pos);
                    if (pItem != null && pItem.getItemID() == itemID) {
                        short counta = pItem.getItemNumber();
                        if (counta >= count)
                            counta = count;
                        InventoryManipulator.rawWasteItem(user.getCharacter(), (short) pos, counta, changeLog);
                        count -= counta;
                    }
                }
                if (count > 0) {
                    changeLog.clear();
                    user.getCharacter().restoreItemSlot(backup, backupTrading);
                    for (List<ItemSlotBase> backupItem : backup) {
                        backupItem.clear();
                    }
                    for (List<Integer> backupTrade : backupTrading) {
                        backupTrade.clear();
                    }
                    backup.clear();
                    backupTrading.clear();
                }
                user.addCharacterDataMod(ItemAccessor.getItemTypeFromTypeIndex(ti));
                user.sendPacket(InventoryManipulator.makeInventoryOperation(Request.None, changeLog));
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    /**
     * Global way to update an item's serial number.
     * 
     * @param item The object of the item to update SN
     * @param cash Whether or not to increment CashSN or SN
     * @return The item's new SN assigned
     */
    public static final long getNextSN(ItemSlotBase item, boolean cash) {
        if (cash) {
            item.setCashItemSN(GameApp.getInstance().getNextCashSN());
            return item.getCashItemSN();
        } else {
            if (item instanceof ItemSlotBundle) {
                ((ItemSlotBundle) item).setItemSN(GameApp.getInstance().getNextSN());
            } else if (item instanceof ItemSlotEquip) {
                ((ItemSlotEquip) item).setItemSN(GameApp.getInstance().getNextSN());
            } else if (item instanceof ItemSlotBase) {
                return GameApp.getInstance().getNextSN();
            }
            return item.getSN();
        }
    }
}
