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

import common.Request;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import common.user.CharacterStat.CharacterStatType;
import game.user.User;
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

    private final boolean[] lock;
    private final List<List<Item>> item;

    public TradingRoom() {
        super(2);
        this.lock = new boolean[2];
        this.item = new ArrayList<>(2);

        Item itemTrade;
        for (int i = 0; i < 2; i++) {
            this.item.add(new ArrayList<>());
            for (int j = 0; j < 9; j++) {// 8 slots
                itemTrade = new Item();
                itemTrade.setNumber((short) -1);
                itemTrade.setTI((byte) 0);
                itemTrade.setPOS((short) 0);
                item.get(i).add(j, itemTrade);
            }
        }
        lock[1] = false;
        lock[0] = false;
    }

    public int doTrade() {
        User owner = getUsers().get(0);
        User user = getUsers().get(1);
        if (owner == null || user == null) {
            Logger.logError("Cannot get a user information on trade with %s", owner != null ? owner.getCharacterName() : user != null ? user.getCharacterName() : "null");
            return MiniRoomLeave.TradeFail.getType();
        }
        final int[] mesoTrading = new int[2];
        final List<List<ItemSlotBase>> backup = new ArrayList<>();
        final List<List<ItemSlotBase>> backupItem = new ArrayList<>();
        final List<List<Integer>> backupItemTrading = new ArrayList<>();
        final List<List<Integer>> backupTrading = new ArrayList<>();
       
        mesoTrading[0] = owner.getCharacter().getMoneyTrading();
        owner.getCharacter().backupItemSlot(backupItem, backupItemTrading);
        owner.getCharacter().clearTradingInfo();
        
        mesoTrading[1] = user.getCharacter().getMoneyTrading();
        user.getCharacter().backupItemSlot(backup, backupTrading);
        user.getCharacter().clearTradingInfo();
        
        List<ExchangeElem> exchange = new ArrayList<>();
        List<ExchangeElem> exchange2 = new ArrayList<>();
        List<ChangeLog> logAdd = new ArrayList<>();
        List<ChangeLog> changeLog = new ArrayList<>();
        MiniRoomLeave result = MiniRoomLeave.TradeDone;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) {
                Item tradeItem = item.get(i).get(j);
                if (tradeItem.getTI() == ItemType.NotDefine) {
                    continue;
                }
                ItemSlotBase itemBase = getUsers().get(i).getCharacter().getItemSlot(tradeItem.getTI()).get(tradeItem.getPOS());
                if (itemBase == null) {
                    result = MiniRoomLeave.TradeFail;
                    break;
                }
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
                exchangeElem.getRemove().setItemID(0);
                exchangeElem.getRemove().setCount(tradeItem.getNumber());
                exchangeElem.getRemove().setTI(tradeItem.getTI());
                exchangeElem.getRemove().setPOS(tradeItem.getPOS());
                if (i == 0) {
                    exchange.add(exchangeElem);
                } else {
                    exchange2.add(exchangeElem);
                }
            }
            int idx = (i > 0) ? 0 : 1;
            long meso = getUsers().get(i).getCharacter().getCharacterStat().getMoney() - getUsers().get(i).getCharacter().getMoneyTrading() + (mesoTrading[idx] - mesoTrading[i]);
            if ((meso >> 32) != 0 || (int) meso < 0) {
                result = MiniRoomLeave.TradeFail;
            }
            if (result != MiniRoomLeave.TradeDone) {
                break;
            }
        }
        if (result == MiniRoomLeave.TradeDone) {
            if (!Inventory.exchange(owner, 0, exchange, logAdd, null) || !Inventory.exchange(user, 0, exchange2, changeLog, null)) {
                result = MiniRoomLeave.TradeFail;
            }
        }
        if (result != MiniRoomLeave.TradeDone) {
            getUsers().get(0).getCharacter().setMoneyTrading(mesoTrading[0]);
            getUsers().get(1).getCharacter().setMoneyTrading(mesoTrading[1]);
            getUsers().get(0).getCharacter().restoreItemSlot(backupItem, backupItemTrading);
            getUsers().get(1).getCharacter().restoreItemSlot(backup, backupTrading);
        } else {
            for (int i = 0; i < 2; i++) {
                int idx = (i == 0) ? 1 : 0;
                int from = getTax(mesoTrading[idx]);
                int meso = (mesoTrading[idx] + -mesoTrading[i] - from);
                getUsers().get(i).incMoney(meso, true, false);
                //addTotalTax
                Inventory.sendInventoryOperation(getUsers().get(i), Request.None, i == 0 ? logAdd : changeLog);
                getUsers().get(i).sendCharacterStat(Request.None, CharacterStatType.Money);
                getUsers().get(i).flushCharacterData(0, true);
            }
            if (owner.getCharacter().getCharacterStat().getLevel() <= 15) {
                owner.setTradeMoneyLimit(owner.getTempTradeMoney());
            }
            if (user.getCharacter().getCharacterStat().getLevel() <= 15) {
                user.setTradeMoneyLimit(user.getTempTradeMoney());
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
        logAdd.clear();
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
        return MiniRoomType.TradingRoom;
    }

    @Override
    public int isAdmitted(User user, boolean onCreate) {
        int admitted = super.isAdmitted(user, onCreate);
        if (admitted == MiniRoomEnter.Success) {
            if (!onCreate) {
                if (user.getField() == null || this.getUsers().get(0).getField() == null) {
                    return 8;//MiniRoomEnter.Etc
                }
                if (user.getField() != this.getUsers().get(0).getField()) {
                    closeRequest(null, 9, 0);//MiniRoomEnter.OnlyInSameField
                    processLeaveRequest();
                    return 9;//MiniRoomEnter.OnlyInSameField
                }
            }
            if (!user.getCharacter().setTrading(true)) {
                return 7;//MiniRoomEnter.NoTrading, but this stuff doesn't exist
            }
        }
        return admitted;
    }

    @Override
    public void onLeave(User user, int leaveType) {
        if (leaveType != MiniRoomLeave.TradeDone.getType()) {
            Inventory.restoreFromTemp(user);
        }
        user.getCharacter().setTrading(false);
    }

    @Override
    public void onPacket(int type, User user, InPacket packet) {
        switch (type) {
            case MiniRoomPacket.PutItem:
                System.err.println("Putting item triggered");
                onPutItem(user, packet);
                break;
            case MiniRoomPacket.PutMoney:
                onPutMoney(user, packet);
                break;
            case MiniRoomPacket.Trade:
                onTrade(user, packet);
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
        int idx;
        if (getCurUsers() == 0 || lock[(idx = findUserSlot(user))] || getUsers().get(idx > 0 ? 0 : 1) == null) {
            user.sendCharacterStat(Request.Excl, 0);
            return;
        }
        ItemSlotBase itemBase = user.getCharacter().getItem(ti, slot);
        if (itemBase == null || (number > itemBase.getItemNumber() || number < 0) || (number == 0 && !ItemAccessor.isRechargeableItem(itemBase.getItemID())) || itemBase.getCashItemSN() > 0) {
            user.sendCharacterStat(Request.Excl, 0);
            return;
        }
        if (pos < 1 || pos > 8 || item.get(idx).get(pos).ti > 0) {
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
        item.get(idx).set(pos, tradeItem);
        for (int i = 0; i < 2; i++) {
            getUsers().get(i).sendPacket(MiniRoomBaseDlg.onPutItem(i != idx ? 1 : 0, pos, itemBase));
            getUsers().get(i).sendCharacterStat(Request.Excl, 0);
        }
    }

    private void onPutMoney(User user, InPacket packet) {
        int idx;
        if (getCurUsers() > 0 && (!lock[idx = findUserSlot(user)] && getUsers().get(idx > 0 ? 0 : 1) != null)) {
            int money = packet.decodeInt();
            int amount = Inventory.moveMoneyToTemp(user, money);
            for (int i = 0; i < 2; i++) {
                getUsers().get(i).sendPacket(MiniRoomBaseDlg.onPutMoney(i != idx ? 1 : 0, amount));
                getUsers().get(i).sendCharacterStat(Request.Excl, 0);
            }
        } else {
            user.sendCharacterStat(Request.Excl, 0);
        }
    }
    
    private void onTrade(User user, InPacket packet) {
        if (getCurUsers() > 0) {
            int idx = findUserSlot(user);
            if (!lock[idx]) {
                if (getUsers().get(idx > 0 ? 0 : 1) != null) {
                    if (user.getCharacter().getCharacterStat().getLevel() <= 15) {
                        //user.checkTradeLimitTime();
                        int moneyTrading = user.getCharacter().getMoneyTrading();
                        if (moneyTrading + user.getTradeMoneyLimit() > 1000000) {
                            //user.sendPacket(MiniRoomBaseDlg.onExceedLimit());
                            return;
                        }
                        user.setTempTradeMoney(moneyTrading);
                    }
                    lock[idx] = true;
                    getUsers().get(idx > 0 ? 0 : 1).sendPacket(MiniRoomBaseDlg.onTrade());
                    if (lock[0] && lock[1]) {
                        User partner = getUsers().get(idx > 0 ? 0 : 1);
                        if (user.lock()) {
                            try {
                                if (partner.lock()) {
                                    try {
                                        closeRequest(null, doTrade(), 0);
                                    } finally {
                                        partner.unlock();
                                    }
                                }
                            } finally {
                                user.unlock();
                            }
                        }
                    }
                }
            }
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

        public byte getTI() {
            return ti;
        }

        public void setTI(byte ti) {
            this.ti = ti;
        }

        public short getPOS() {
            return pos;
        }

        public void setPOS(short pos) {
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
