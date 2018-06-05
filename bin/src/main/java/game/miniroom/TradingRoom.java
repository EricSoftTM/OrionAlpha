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
package game.miniroom;

import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import common.user.CharacterStat.CharacterStatType;
import game.user.User;
import game.user.WvsContext.Request;
import game.user.item.BundleItem;
import game.user.item.ChangeLog;
import game.user.item.EquipItem;
import game.user.item.ExchangeElem;
import game.user.item.Inventory;
import game.user.item.ItemInfo;
import java.util.ArrayList;
import java.util.List;
import network.packet.InPacket;
import util.Logger;

/**
 *
 * @author sunnyboy
 */
public class TradingRoom extends MiniRoomBase {

    public final boolean[] lock;
    public final List<List<Item>> item;

    public TradingRoom(int maxUsers) {
        super(maxUsers);
        this.lock = new boolean[2];
        this.item = new ArrayList<>(2);

        Item itemTrade;
        for (int i = 0; i < 2; i++) {
            this.item.add(new ArrayList<>());
            for (int j = 0; j < 9; j++) {// 8 slots
                itemTrade = new Item();
                itemTrade.setNumber((short)-1);
                itemTrade.setTi((byte)0);
                itemTrade.setPos((short)0);
                item.get(i).add(j, itemTrade);
            }
        }
        lock[1] = false;
        lock[0] = false;
    }

    public int doTrade() {
        User pOwner = users.get(0);
        User pUser = users.get(1);
        if (pOwner == null || pUser == null) {
            Logger.logError("Cannot get a user information on trade with %s", pOwner != null ? pOwner.getCharacterName() : pUser != null ? pUser.getCharacterName() : "null");
            return MiniRoomLeave.TradeFail.getType();
        }
        final int[] anMesoTrading = new int[2];
        final List<List<ItemSlotBase>> backup = new ArrayList<>();
        final List<List<ItemSlotBase>> backupItem = new ArrayList<>();
        final List<List<Integer>> backupItemTrading = new ArrayList<>();
        final List<List<Integer>> backupTrading = new ArrayList<>();
       
        anMesoTrading[0] = pOwner.getCharacter().getMoneyTrading();
        pOwner.getCharacter().backupItemSlot(backupItem, backupItemTrading);
        pOwner.getCharacter().clearTradingInfo();
        
        anMesoTrading[1] = pUser.getCharacter().getMoneyTrading();
        pUser.getCharacter().backupItemSlot(backup, backupTrading);
        pUser.getCharacter().clearTradingInfo();
        
        List<ExchangeElem> exchange = new ArrayList<>();
        List<ExchangeElem> exchange2 = new ArrayList<>();
        List<ChangeLog> paLogAdd = new ArrayList<>();
        List<ChangeLog> changeLog = new ArrayList<>();
        MiniRoomLeave result = MiniRoomLeave.TradeDone;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 10; j++) {
                Item tradeItem = item.get(i).get(j);
                if (tradeItem.getTi() == 0) {
                    continue;
                }
                ItemSlotBase itemBase = users.get(i).getCharacter().getItemSlot(tradeItem.getTi()).get(tradeItem.getPos());
                if (itemBase == null) {
                    result = MiniRoomLeave.TradeFail;
                    break;
                }
                byte ti = (byte) (itemBase.getItemID() / 1000000);
                if (!users.get(i).isItemExist(ti, itemBase.getItemID())) {
                    int itemID;
                    short count;
                    ItemSlotBase itemSlot;
                    if (ItemAccessor.isTreatSingly(itemBase)) {
                        itemSlot = itemBase;
                        count = 0;
                        itemID = 0;
                    } else {
                        itemSlot = null;
                        count = (short) tradeItem.getNumber();
                        itemID = itemBase.getItemID();
                    }
                    ExchangeElem exchangeElem = new ExchangeElem();
                    exchangeElem.initAdd(itemID, count, itemSlot);
                    if (i == 0) {
                        exchange2.add(exchangeElem);
                    } else {
                        exchange.add(exchangeElem);
                    }
                    exchangeElem = new ExchangeElem();
                    exchangeElem.setAdd(false);
                    exchangeElem.getR().setItemID(0);
                    exchangeElem.getR().setCount(tradeItem.getNumber());
                    exchangeElem.getR().setTi(tradeItem.getTi());
                    exchangeElem.getR().setPos(tradeItem.getPos());
                    if (i == 0) {
                        exchange.add(exchangeElem);
                    } else {
                        exchange2.add(exchangeElem);
                    }
                } else {
                    result = MiniRoomLeave.TradeFail_OnlyItem;
                    break;
                }
            }
            int idx = (i > 0) ? 0 : 1;
            long meso = users.get(i).getCharacter().getCharacterStat().getMoney() - users.get(i).getCharacter().getMoneyTrading() + (anMesoTrading[idx] - anMesoTrading[i]);
            if ((meso >> 32) != 0 || (int) meso < 0) {
                result = MiniRoomLeave.TradeFail;
            }
            if (result != MiniRoomLeave.TradeDone) {
                break;
            }
        }
        if (result == MiniRoomLeave.TradeDone) {
            if (!Inventory.exchange(pOwner, 0, exchange, paLogAdd, null) || !Inventory.exchange(pUser, 0, exchange2, changeLog, null)) {
                result = MiniRoomLeave.TradeFail;
            }
        }
        if (result != MiniRoomLeave.TradeDone) {
            users.get(0).getCharacter().setMoneyTrading(anMesoTrading[0]);
            users.get(1).getCharacter().setMoneyTrading(anMesoTrading[1]);
            users.get(0).getCharacter().restoreItemSlot(backupItem, backupItemTrading);
            users.get(1).getCharacter().restoreItemSlot(backup, backupTrading);
        } else {
            for (int i = 0; i < 2; i++) {
                int idx = (i == 0) ? 1 : 0;
                int from = getTax(anMesoTrading[idx]);
                int meso = (anMesoTrading[idx] + -anMesoTrading[i] - from);
                users.get(i).incMoney(meso, true, false);
                //addTotalTax
                Inventory.sendInventoryOperation(users.get(i), Request.None, i == 0 ? paLogAdd : changeLog);
                users.get(i).sendCharacterStat(Request.None, CharacterStatType.Money);
                users.get(i).flushCharacterData(0, true);
            }
            if (pOwner.getCharacter().getCharacterStat().getLevel() <= 15) {
                pOwner.setTradeMoneyLimit(pOwner.getTempTradeMoney());
            }
            if (pUser.getCharacter().getCharacterStat().getLevel() <= 15) {
                pUser.setTradeMoneyLimit(pUser.getTempTradeMoney());
            }
        }
        for (List<Integer> backupItemTradingList : backupItemTrading) {
            backupItemTradingList.clear();
        }
        backupItemTrading.clear();
        for (List<Integer> backupTradingList : backupTrading) {
            backupTradingList.clear();
        }
        backupTrading.clear();
        exchange.clear();
        exchange2.clear();
        paLogAdd.clear();
        changeLog.clear();
        return result.getType();
    }

    @Override
    public int getCloseType() {
        return 2;// verify
    }

    public int getItemPrice(int itemID) {
        EquipItem equip;
        BundleItem bundle;
        if ((equip = ItemInfo.getEquipItem(itemID)) != null) {
            return equip.getSellPrice();
        } else if ((bundle = ItemInfo.getBundleItem(itemID)) != null) {
            return bundle.getSellPrice();
        }
        return 0;
    }

    public int getTax(int meso) {
        double tax = 0;
        if (meso >= 100000) {
            tax = (double) meso * 0.008;
        }
        if (meso >= 1000000) {
            tax = (double) meso * 0.018;
        }
        if (meso >= 5000000) {
            tax = (double) meso * 0.03;
        }
        if (meso >= 10000000) {
            tax = (double) meso * 0.04;
        }
        if (meso >= 25000000) {
            tax = (double) meso * 0.05;
        }
        if (meso >= 100000000) {
            tax = (double) meso * 0.06;
        }
        return (int) tax;
    }

    @Override
    public int getTypeNumber() {
        return 1;
    }

    @Override
    public int isAdmitted(User user, boolean onCreate) {
        int admitted = super.isAdmitted(user, onCreate);
        if (admitted == 0) {
            if (!onCreate) {
                if (user.getField() == null || this.users.get(0).getField() == null) {
                    return 8;
                }
                if (user.getField() != this.users.get(0).getField()) {
                    closeRequest(null, 9, 0);
                    processLeaveRequest();
                    return 9;
                }
            }
            if (!user.getCharacter().setTrading(true)) {
                return 7;
            }
        }
        return admitted;
    }

    @Override
    public void onLeave(User user, int leaveType) {
        // restorefromtemp
        user.getCharacter().setTrading(false);
    }

    @Override
    public void onPacket(MiniRoomPacket type, User user, InPacket packet) {
        switch (type) {
            case PutItem_TR:
                System.err.println("Putting item triggered");
                onPutItem(user, packet);
                break;
            case PutMoney:
                onPutMoney(user, packet);
                break;
            case Trade:
                break;
        }
    }

    private void onPutItem(User user, InPacket packet) {
        byte ti = packet.decodeByte();
        short slot = packet.decodeShort();
        short number = 1;
        if (ti != ItemType.Equip) {
            number = packet.decodeShort();
        }
        byte pos = packet.decodeByte();
        int nIdx;
        if (curUsers == 0 || lock[(nIdx = findUserSlot(user))] || users.get(nIdx > 0 ? 0 : 1) == null) {
            user.sendCharacterStat(Request.Excl, 0);
            return;
        }
        ItemSlotBase itemBase = user.getCharacter().getItem(ti, slot);
        if (itemBase == null || (number > itemBase.getItemNumber() || number < 0) || (number == 0 && !ItemAccessor.isRechargeableItem(itemBase.getItemID())) || itemBase.getCashItemSN() > 0) {
            user.sendCharacterStat(Request.Excl, 0);
            return;
        }
        if (pos < 1 || pos > 8 || item.get(nIdx).get(pos).ti > 0) {
            user.sendCharacterStat(Request.Excl, 0);
            return;
        }
        Inventory.moveItemToTemp(user, ti, slot, number);
        itemBase = itemBase.makeClone();
        itemBase.setItemNumber(number);
        Item tradeItem = new Item();
        tradeItem.ti = ti;
        tradeItem.number = number;
        tradeItem.pos = slot;
        item.get(nIdx).set(pos, tradeItem);
        for (int i = 0; i < 2; i++) {
            users.get(i).sendPacket(MiniRoomBaseDlg.onPutItem(i != nIdx ? 1 : 0, pos, itemBase));
            users.get(i).sendCharacterStat(Request.Excl, 0);
        }
    }

    private void onPutMoney(User user, InPacket packet) {
        int idx;
        if (this.curUsers > 0 && (!lock[idx = findUserSlot(user)] && users.get(idx > 0 ? 0 : 1) != null)) {
            int money = packet.decodeInt();
            int amount = Inventory.moveMoneyToTemp(user, money);
            for (int i = 0; i < 2; i++) {
                users.get(i).sendPacket(MiniRoomBaseDlg.onPutMoney(i != idx ? 1 : 0, amount));
                users.get(i).sendCharacterStat(Request.Excl, 0);
            }
        } else {
            user.sendCharacterStat(Request.Excl, 0);
        }
    }

    public class Item {

        private byte ti;
        private short pos;
        private short number;

        public Item() {
            this.ti = 0;
            this.pos = 0;
            this.number = -1;
        }

        public byte getTi() {
            return ti;
        }

        public void setTi(byte ti) {
            this.ti = ti;
        }

        public short getPos() {
            return pos;
        }

        public void setPos(short pos) {
            this.pos = pos;
        }

        public short getNumber() {
            return number;
        }

        public void setNumber(short number) {
            this.number = number;
        }
    }
}
