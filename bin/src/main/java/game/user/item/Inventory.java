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
import game.GameApp;
import game.field.drop.Reward;
import game.field.drop.RewardType;
import game.user.User;
import game.user.skill.SkillInfo;
import java.util.ArrayList;
import java.util.List;
import util.Logger;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class Inventory {
    
    public static boolean changeSlotPosition(User user, byte onExclRequest, byte ti, short pos1, short pos2, short count) {
        if (user.getHP() == 0)
            return false;
        if (ti <= ItemType.NotDefine || ti > ItemType.NO) {
            Logger.logError("Invalid Item Type Index");
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
                                        Reward reward = new Reward(RewardType.Item, itemRemoved.get(), 0, 0);
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
        // TODO: Equipping items.
    }
    
    private static void unequip(User user, short pos1, short pos2, List<ChangeLog> changeLog) {
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
        user.getCharacter().setItem(ItemType.Equip, pos1, source);
        if (target != null) {
            user.getCharacter().setItem(ItemType.Equip, pos1, target);
        }
        InventoryManipulator.insertChangeLog(changeLog, ChangeLog.Position, ItemType.Equip, pos2, source, pos1, (short) 0);
        user.validateStat(false);
    }
    
    public static boolean rawRemoveItem(User user, byte ti, short pos, short count, List<ChangeLog> changeLog, Pointer<Integer> decRet, Pointer<ItemSlotBase> itemRemoved) {
        if (user.getHP() > 0)
            return InventoryManipulator.rawRemoveItem(user.getCharacter(), ti, pos, count, changeLog, decRet, itemRemoved);
        return false;
    }
    
    public static void sendInventoryOperation(User user, byte onExclRequest, List<ChangeLog> changeLog) {
        user.sendPacket(InventoryManipulator.makeInventoryOperation(onExclRequest, changeLog));
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
