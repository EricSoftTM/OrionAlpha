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
import common.user.CharacterData;
import game.user.skill.SkillInfo;
import java.util.Collections;
import java.util.List;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class InventoryManipulator {
    
    public static void insertChangeLog(List<ChangeLog> a, byte change, byte ti, short pos, ItemSlotBase pi, short pos2, short number) {
        if (pi != null)
            pi = pi.makeClone();
        ChangeLog cl = new ChangeLog();
        cl.setChange(change);
        cl.setTI(ti);
        cl.setPOS(pos);
        cl.setItem(pi);
        cl.setPOS2(pos2);
        cl.setNumber(number);
        if (cl.getItem() != null && number > 0 && (ti == ItemType.Consume || ti == ItemType.Install || ti == ItemType.Etc))
            cl.getItem().setItemNumber(number);
        a.add(cl);
    }
    
    public static boolean isItemExist(CharacterData cd, byte ti, int itemID) {
        int slotCount = cd.getItemSlotCount(ti);
        if (slotCount < 1) {
            return false;
        }
        for (int i = 1; i <= slotCount; i++) {
            ItemSlotBase item = cd.getItem(ti, i);
            if (item != null) {
                if (item.getItemID() == itemID) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static OutPacket makeInventoryOperation(byte onExclResult, List<ChangeLog> changeLog) {
        if (changeLog == null) {
            changeLog = Collections.EMPTY_LIST;
        }
        OutPacket packet = new OutPacket(LoopbackPacket.InventoryOperation);
        packet.encodeByte(onExclResult);
        packet.encodeByte(changeLog.size());
        for (ChangeLog change : changeLog) {
            packet.encodeByte(change.getChange());
            packet.encodeByte(change.getTI());
            packet.encodeShort(change.getPOS());
            switch (change.getChange()) {
                case ChangeLog.NewItem:
                    change.getItem().encode(packet);
                    break;
                case ChangeLog.ItemNumber:
                    packet.encodeShort(change.getNumber());
                    break;
                case ChangeLog.Position:
                    packet.encodeShort(change.getPOS2());
                    break;
                case ChangeLog.DelItem:
                    break;
            }
        }
        return packet;
    }
    
    public static boolean rawAddItem(CharacterData cd, byte ti, ItemSlotBase item, List<ChangeLog> changeLog, Pointer<Integer> incRet) {
        if (item == null || cd.getCharacterStat().getHP() == 0) {
            return false;
        }
        if (ItemAccessor.isTreatSingly(item)) {
            short emptyPOS = (short) cd.findEmptySlotPosition(ti);
            if (emptyPOS > 0) {
                if (ItemInfo.isCashItem(item.getItemID())) {
                    if (!item.isCashItem())
                        Inventory.getNextSN(item, true);
                } else {
                    if (item.getSN() == 0)
                        Inventory.getNextSN(item, false);
                }
                cd.getItemSlot().get(ti).set(emptyPOS, item);
                insertChangeLog(changeLog, ChangeLog.NewItem, ti, emptyPOS, item, (short) 0, (short) 0);
                if (incRet != null) {
                    incRet.set(1);
                }
                return true;
            }
            return false;
        }
        if (ItemInfo.getBundleItem(item.getItemID()) == null) {
            return false;
        }
        int maxPerSlot = SkillInfo.getInstance().getBundleItemMaxPerSlot(item.getItemID(), cd);
        int fullCount = Math.max(item.getItemNumber(), 0);
        if (fullCount <= 0) {
            return false;
        }
        int remain = fullCount;
        int slotCount = cd.getItemSlotCount(ti);
        if (fullCount > 0) {
            ItemSlotBase itemSlot;
            for (int i = 1; i <= slotCount; i++) {
                itemSlot = cd.getItemSlot().get(ti).get(i);
                if (itemSlot != null && itemSlot.getItemID() == item.getItemID()) {
                    int number = itemSlot.getItemNumber();
                    int inc = Math.min(Math.max(maxPerSlot - number, 0), remain);
                    if (inc > 0) {
                        int nTradingCount = cd.getItemTrading().get(ti).get(i);
                        if (itemSlot.getItemNumber() - nTradingCount > 0) {
                            insertChangeLog(changeLog, ChangeLog.ItemNumber, ti, (short) i, itemSlot, (short) 0, (short) (inc + itemSlot.getItemNumber() - nTradingCount));
                        } else {
                            insertChangeLog(changeLog, ChangeLog.NewItem, ti, (short) i, itemSlot, (short) 0, (short) inc);
                        }
                        itemSlot.setItemNumber((short) (itemSlot.getItemNumber() + inc));
                        remain -= inc;
                        if (remain <= 0) {
                            if (incRet != null) {
                                incRet.set(fullCount);
                            }
                            return true;
                        }
                    }
                }
            }
            short pos = (short) cd.findEmptySlotPosition(ti);
            if (pos > 0) {
                if (item.getSN() == 0)
                    Inventory.getNextSN(item, false);
                item.setItemNumber((short) remain);
                cd.getItemSlot().get(ti).set(pos, item);
                insertChangeLog(changeLog, ChangeLog.NewItem, ti, pos, item, (short) 0, (short) 0);
                if (incRet != null) {
                    incRet.set(fullCount);
                }
            } else {
                if (incRet != null) {
                    incRet.set(fullCount - remain);
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    // rawExchange
    
    public static boolean rawIncMoney(CharacterData cd, int inc, boolean onlyFull) {
        int money = inc + cd.getCharacterStat().getMoney();
        if (inc <= 0) {
            if (money < Math.max(cd.getMoneyTrading(), 0)) {
                if (onlyFull)
                    return false;
                money = Math.max(cd.getMoneyTrading(), 0);
            }
        } else {
            if (money < 0) {
                if (onlyFull)
                    return false;
                money = ((cd.getCharacterStat().getMoney() < 0 ? 1 : 0) - 1) & 0x7FFFFFFF;
            }
            if (money < cd.getMoneyTrading())
                money = cd.getMoneyTrading();
        }
        cd.getCharacterStat().setMoney(money);
        return true;
    }
    
    public static boolean rawRechargeItem(CharacterData cd, short pos, List<ChangeLog> changeLog) {
        if (cd.getCharacterStat().getHP() == 0)
            return false;
        ItemSlotBase item = cd.getItem(ItemType.Consume, pos);
        short maxPerSlot;
        if (item == null || ItemInfo.isCashItem(item.getItemID()) || !ItemAccessor.isRechargeableItem(item.getItemID()) 
                || item.getItemNumber() > (maxPerSlot = (short) SkillInfo.getInstance().getBundleItemMaxPerSlot(item.getItemID(), cd))) {
            return false;
        } else {
            item.setItemNumber(maxPerSlot);
            insertChangeLog(changeLog, ChangeLog.ItemNumber, ItemType.Consume, pos, item, (short) 0, (short) maxPerSlot);
            return true;
        }
    }
    
    public static boolean rawRemoveItem(CharacterData cd, byte ti, short pos, int count, List<ChangeLog> changeLog, Pointer<Integer> decRet, Pointer<ItemSlotBase> itemRemoved) {
        if (cd.getCharacterStat().getHP() == 0)
            return false;
        ItemSlotBase item = cd.getItem(ti, pos);
        if (item == null) {
            return false;
        }
        if (ItemAccessor.isTreatSingly(item)) {
            if (count == 1) {
                if (pos > 0 && cd.getItemTrading().get(ti).get(pos) != 0) {
                    return false;
                }
                cd.setItem(ti, pos, null);
                if (itemRemoved != null)
                    itemRemoved.set(item.makeClone());
                insertChangeLog(changeLog, ChangeLog.DelItem, ti, pos, item, (short) 0, (short) 0);
                if (decRet != null)
                    decRet.set(1);
                return true;
            } else {
                return false;
            }
        }
        if (count <= 0)
            return false;
        int hold = item.getItemNumber();
        int trading = cd.getItemTrading().get(ti).get(pos);
        int dec = Math.min(Math.max(hold - trading, 0), count);
        if (dec > 0) {
            item.setItemNumber((short) (item.getItemNumber() - dec));
            int remain = item.getItemNumber() - trading;
            if (item.getItemNumber() == trading) {
                insertChangeLog(changeLog, ChangeLog.DelItem, ti, pos, null, (short) 0, (short) 0);
            } else {
                insertChangeLog(changeLog, ChangeLog.ItemNumber, ti, pos, null, (short) 0, (short) remain);
            }
            if (itemRemoved != null) {
                itemRemoved.set(item.makeClone());
                itemRemoved.get().setItemNumber((short) dec);
            }
            if (item.getItemNumber() <= 0) {
                cd.setItem(ti, pos, null);
            }
            if (decRet != null)
                decRet.set(dec);
            return true;
        }
        return false;
    }
    
    public static boolean rawWasteItem(CharacterData cd, short pos, short count, List<ChangeLog> aChangeLog) {
        if (count <= 0)
            return false;
        ItemSlotBase item = cd.getItem(ItemType.Consume, pos);
        if (item == null || ItemInfo.isCashItem(item.getItemID()) || item.isCashItem() || !ItemAccessor.isRechargeableItem(item.getItemID()) || cd.getItemTrading().get(ItemType.Consume).get(pos) > 0)
            return false;
        short remain = (short) (item.getItemNumber() - count);
        if (remain < 0) {
            return false;
        }
        item.setItemNumber(remain);
        insertChangeLog(aChangeLog, ChangeLog.ItemNumber, ItemType.Consume, pos, null, (short) 0, remain);
        return true;
    }
}
