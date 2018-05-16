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
package shop.user;

import common.item.ItemSlotBase;
import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import common.item.ItemSlotType;
import common.item.ItemType;
import common.user.CharacterData;
import game.user.item.ItemInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.database.CommonDB;
import network.database.ShopDB;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import shop.Commodity;
import shop.ShopApp;
import shop.ShopPacket;
import shop.field.Stage;
import shop.user.inventory.Inventory;
import util.FileTime;
import util.Logger;
import util.Rand32;
import util.SystemTime;

/**
 *
 * @author sunnyboy
 */
public class User {

    private int accountID;
    private boolean alreadyAcceptedRequest;
    private byte authenCode;
    private int birthDay;
    private List<CashItemInfo> cashItemInfo;
    private int cashKey;
    private boolean cashShopAuthorized;
    private final CharacterData character;
    private int characterID;
    private String characterName;
    private int countCouponRedeemFailed;
    private boolean doCheckCashItemExpire;
    private List<GiftList> giftList;
    private byte gradeCode;

    private List<Integer> itemCount;
    private long lastCharacterDataFlush;
    private byte level;
    private int localSocketSN;
    private final Lock lock;
    private final Lock lockGuardData;

    private final Lock lockSocket;
    private boolean migrateOutPosted;

    private int modFlag;
    private boolean msMessenger;
    private int price;
    private byte delta;
    private int nexonCash;
    private String nexonClubID;

    private long nextCheckCashItemExpire;
    private int privateStatusID;

    private int purchaseExp;
    private byte purchaseType;

    private FileTime registerDate;
    private int slotCount;
    private ClientSocket socket;
    private byte typeIndex;
    /*tDelayedLoopbackPacket dd ?
     nDelayedLoopbackPacket dd ?
     tLastCheckMalProc dd ?
     tIntervalCheckMalProc dd ?
     bWaitMalProcPacket dd ?
     nPurchaseType dd ?
     bByMaplePoint dd ?
     nDBID         dd ?
     i64ChargeNo   dq ?
     sRcvCharacterName ZXString<char> ?
     sText         ZXString<char> ?
     sOldCharacterName ZXString<char> ?
     sNewCharacterName ZXString<char> ?
     nTransferWorldTargetWorldID dd ?
     nItemID       dd ?
     nNumber       dd ?
     nActivePeriod dd ?
     nCommodityID  dd ?
     nCouponSN     dd ?
     nPrice        dd ?
     nPaybackRate  dd ?
     nGiftPaybackRate dd ?
     nDiscountRate dd ?
     nTI           dd ?
     nDelta        dd ?
     nSlotCount    dd ?
     nFailReason   dd ?
     nItemGender   dd ?
     aciNew        ZArray<GW_CashItemInfo> ?
     nTrunkCount   dd ?
     sCouponID     ZXString<char> ?
     nMaplePointGiven dd ?
     nMesoGiven    dd ?
     aniNew        ZArray<_ULARGE_INTEGER> ?
     aChangeLog    ZArray<CInventoryManipulator::CHANGELOG> ?
     aaItemSlotBackUp ZArray<ZRef<GW_ItemSlotBase> > 6 dup(?)
     nStockState   dd ?
     nMaplePointInPackage dd ?*/
    /*   private ClientSocket socket;
     */
    private static final Lock lockUser = new ReentrantLock();

    private static final Map<String, User> userByName = new LinkedHashMap<>();
    private static final Map<Integer, User> users = new LinkedHashMap<>();

    protected User(int characterID) {
        super();
        this.cashKey = 0;
        this.migrateOutPosted = false;
        this.alreadyAcceptedRequest = false;

        this.giftList = new ArrayList<>();
        this.cashItemInfo = new ArrayList<>();
        this.itemCount = new ArrayList<>();
        this.nexonCash = 0;
        this.purchaseType = -1;
        this.modFlag = 0;
        ShopDB.rawLoadAccount(characterID, User.this);
        this.character = ShopDB.rawLoadCharacter(characterID, User.this);

        if (this.nexonClubID != null && !this.nexonClubID.isEmpty()) {
            this.cashShopAuthorized = true;
            Logger.logReport("User is cashShopAuthorized");
        }
        this.cashKey = Rand32.getInstance().random().intValue();
        this.lockGuardData = new ReentrantLock();
        this.lock = new ReentrantLock();
        this.lockSocket = new ReentrantLock();
    }

    public User(ClientSocket socket) {
        this(socket.getCharacterID());
        this.socket = socket;
        this.localSocketSN = socket.getLocalSocketSN();
        this.characterID = character.getCharacterStat().getCharacterID();
        this.characterName = character.getCharacterStat().getName();
    }

    public static final void broadcast(OutPacket packet) {
        lockUser.lock();
        try {
            for (User user : users.values()) {
                if (user != null) {
                    user.sendPacket(packet);
                }
            }
        } finally {
            lockUser.unlock();
        }
    }

    public static final void broadcastGMPacket(OutPacket packet) {
        lockUser.lock();
        try {
            for (User user : users.values()) {
                if (user != null && user.isGM()) {
                    user.sendPacket(packet);
                }
            }
        } finally {
            lockUser.unlock();
        }
    }

    public static final synchronized User findUser(int characterID) {
        lockUser.lock();
        try {
            if (users.containsKey(characterID)) {
                User user = users.get(characterID);
                if (user != null) {
                    return user;
                }
            }
            return null;
        } finally {
            lockUser.unlock();
        }
    }

    public static final synchronized User findUserByName(String name, boolean makeLower) {
        lockUser.lock();
        try {
            if (makeLower) {
                name = name.toLowerCase();
            }
            if (userByName.containsKey(name)) {
                User user = userByName.get(name);
                if (user != null) {
                    return user;
                }
            }
            return null;
        } finally {
            lockUser.unlock();
        }
    }

    public static final Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    public static final synchronized boolean registerUser(User user) {
        lockUser.lock();
        try {
            if (users.containsKey(user.characterID)) {
                return false;
            } else {
                users.put(user.characterID, user);
                userByName.put(user.characterName.toLowerCase(), user);
                return true;
            }
        } finally {
            lockUser.unlock();
        }
    }

    public static final void unregisterUser(User user) {
        lockUser.lock();
        try {
            users.remove(user.characterID);
            userByName.remove(user.characterName.toLowerCase());
        } finally {
            lockUser.unlock();
        }
    }

    public List<CashItemInfo> getCashItemInfo() {
        return this.cashItemInfo;
    }

    private void checkCashItemExpire(long time) {
        if (time - this.nextCheckCashItemExpire >= 0 && this.doCheckCashItemExpire != true) {
            FileTime cur;
            if ((cur = SystemTime.getLocalTime().systemTimeToFileTime()) != null) {
                this.nextCheckCashItemExpire = time + 180000;
                for (Iterator<CashItemInfo> it = this.cashItemInfo.iterator(); it.hasNext();) {
                    CashItemInfo cashItem = it.next();
                    if (FileTime.compareFileTime(cashItem.getDateExpire(), cur) <= 0) {
                        this.doCheckCashItemExpire = true;
                        it.remove();
                    }
                }
            }
        }
    }

    private void closeSocket() {
        lockSocket.lock();
        try {
            if (socket != null) {
                socket.postClose();
            }
        } finally {
            lockSocket.unlock();
        }
    }

    public final void destructUser() {
        flushCharacterData(0, true);
        Logger.logReport("User logout");
    }

    private void flushCharacterData(int cur, boolean force) {
        if (lock()) {
            try {
                if (force || cur - lastCharacterDataFlush >= 300000) {
                    ShopApp.getInstance().updateItemInitSN();
                    if (this.modFlag != 0) {
                        if ((modFlag & ModFlag.NexonCash) != 0) {
                            Logger.logReport("Updating NexonCash");
                            if (this.nexonCash < 0) {
                                this.nexonCash = 0;
                            }
                            ShopDB.rawUpdateNexonCash(this.accountID, this.nexonCash);
                        }
                        if ((modFlag & ModFlag.ItemLocker) != 0) {
                            Logger.logReport("Updating ItemLocker");

                            ShopDB.rawUpdateItemLocker(this.characterID, this.cashItemInfo);
                        }
                        if ((modFlag & ModFlag.ItemSlotEquip) != 0) {
                            Logger.logReport("Updating SlotEquip");

                            CommonDB.rawUpdateItemEquip(this.characterID, null, null, this.character.getItemSlot(ItemType.Equip));
                        }
                        if ((modFlag & ModFlag.ItemSlotBundle) != 0 || (modFlag & ModFlag.ItemSlotEtc) != 0) {
                            Logger.logReport("Updating Bundle");
                            //update bundles
                        }
                        if ((modFlag & ModFlag.InventorySize) != 0) {
                            Logger.logReport("Updating SlotCount");
                            ShopDB.rawIncreaseItemSlotCount(this.characterID, this.typeIndex, this.slotCount);
                        }
                        modFlag = 0;
                    }
                    lastCharacterDataFlush = cur;
                }
            } finally {
                unlock();
            }
        }
    }

    public int getAccountID() {
        return this.accountID;
    }

    public CharacterData getCharacter() {
        return character;
    }

    public int getCharacterID() {
        return characterID;
    }

    public int getNexonCash() {
        return nexonCash;
    }

    private boolean isBlockedMachineID() {
        return ((authenCode & 8) != 0);
    }

    public boolean isGM() {
        return gradeCode >= 3;//TODO: Proper GradeCode
    }

    public final boolean lock() {
        return lock(700);
    }

    public final boolean lock(long timeout) {
        try {
            return lock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }

    private void onBuyDone(InPacket packet) {
        int commoditySN = packet.decodeInt();
        Commodity comm = ShopApp.getInstance().findCommodity(commoditySN);
        if (comm != null && ItemInfo.isCashItem(comm.getItemID()) && this.getNexonCash() >= comm.getPrice()) {
            CashItemInfo cashItem = new CashItemInfo();
            cashItem.setCashItemSN(ShopApp.getInstance().getNextCashSN());
            cashItem.setAccountID(this.accountID);
            cashItem.setCharacterID(this.characterID);
            cashItem.setItemID(comm.getItemID());
            cashItem.setCommodityID(commoditySN);
            cashItem.setNumber(comm.getCount());
            cashItem.setBuyCharacterName(this.characterName);
            FileTime ftExpire = FileTime.systemTimeToFileTime();
            ftExpire.add(FileTime.FILETIME_DAY, comm.getPeriod());
            cashItem.setDateExpire(ftExpire);
            this.cashItemInfo.add(cashItem);
            this.nexonCash -= comm.getPrice();
            this.modFlag |= ModFlag.NexonCash | ModFlag.ItemLocker;
            sendPacket(ShopPacket.onBuyDone(cashItem));
            this.sendRemainCashRequest();

            // Always keep the shop server's initSN up-to-date.
            ShopApp.getInstance().updateItemInitSN();
        }
    }

    private void onCashItemRequest(InPacket packet) {
        byte type = packet.decodeByte();
        Logger.logReport("onCashItemRequest triggered with type " + type);

        switch (type) {
            case 1:
                onBuyDone(packet);
                break;
            case 2: // probably gift
                break;
            case 3:
                onIncSlotCount(packet);
                break;
            case 6:
                onMoveLToS(packet);
                break;
            case 7:
                onMoveSToL(packet);
                break;
        }
    }

    private void onChargeParamRequest(InPacket packet) {
        sendPacket(ShopPacket.onQueryCash(this)); // prevent getting stuck
    }

    private void onIncSlotCount(InPacket packet) {
        if (!this.cashShopAuthorized) {
            sendPacket(ShopPacket.onIncSlotCountFailed((byte) 30));
            return;
        }
        byte ti = packet.decodeByte();
        if (ti < ItemType.NotDefine || ti > ItemType.Etc) {
            return;
        }
        this.price = 4800;
        this.delta = 4;
        this.typeIndex = ti;
        if (this.price > this.getNexonCash()) {
            sendPacket(ShopPacket.onIncSlotCountFailed((byte) 31));
        } else {
            int newSlotCount = this.delta + this.character.getItemSlotCount(ti);
            if ((newSlotCount - this.delta) * getIncreasedSlotCount(ti, this.character.getCharacterStat().getJob()) > 80) {
                //  packet error
                return;
            }
            if (Inventory.incItemSlotCount(this, ti, this.delta)) {
                this.slotCount = newSlotCount;
                this.nexonCash -= this.price;
                this.modFlag |= ModFlag.NexonCash | ModFlag.InventorySize;
                this.sendRemainCashRequest();
                sendPacket(ShopPacket.onIncSlotCountDone(ti, (short) newSlotCount));
            }
        }
    }

    public static int getIncreasedSlotCount(byte ti, short job) {
        int count = 0;
        switch (job / 100) {
            case 1:
                count = 1;
                if (job / 10 % 10 != 0 && (ti == ItemType.Consume || ti == ItemType.Etc)) {
                    ++count;
                }
                break;
            case 2:
                if (job / 10 % 10 != 0 && ti == ItemType.Etc) {
                    count = 1;
                }
                break;
            case 3:
                if (ti == ItemType.Equip || ti == ItemType.Consume) {
                    count = 1;
                }
                if (job / 10 % 10 != 0 && ti == ItemType.Etc) {
                    ++count;
                }
                break;
            case 4:
                if (ti == ItemType.Equip || ti == ItemType.Etc) {
                    count = 1;
                }
                if (job / 10 % 10 != 0 && ti == ItemType.Consume) {
                    ++count;
                }
                break;
            default:
                return count;
        }
        return count;
    }

    public void onMigrateInSuccess() {
        Logger.logReport("User login from (%s)", this.characterName);
        sendPacket(Stage.onSetCashShop(this));
        sendPacket(ShopPacket.onLoadLockerDone(this.cashItemInfo));
        sendPacket(ShopPacket.onQueryCash(this));

    }

    private void onMoveLToS(InPacket packet) {
        if (this.cashShopAuthorized) {
            long sn = packet.decodeLong();
            byte ti = packet.decodeByte();
            short pos = packet.decodeShort();

            if (ti < ItemType.NotDefine || ti > ItemType.Etc) {
                Logger.logError("Invalid item type index (sn: %d, pos: %d for ti %d)", sn, pos, ti);
                return;
            }
            if (pos <= 0 || pos > this.character.getItemSlotCount(ti) || this.character.getItem(ti, pos) != null) {
                Logger.logError("Invalid item slot position (sn: %d) at pos: %d", sn, pos);
                return;
            }
            if (this.character.findEmptySlotPosition(ti) < 0) {
                Logger.logError("No valid slot remains for sn: %d", sn);
                return;
            }
            System.err.println("CashItemSN from onMoveLtoS " + sn);
            ItemSlotBase item = null;
            if (ti == ItemType.Equip) {
                item = (ItemSlotEquip) ItemSlotBase.createItem(ItemSlotType.Equip);
            } else if (ti == ItemType.Consume || ti == ItemType.Etc) {
                item = (ItemSlotBundle) ItemSlotBase.createItem(ItemSlotType.Bundle);
            }
            if (item != null) {
                for (Iterator<CashItemInfo> it = this.cashItemInfo.iterator(); it.hasNext();) {
                    CashItemInfo cashItem = it.next();
                    if (cashItem != null && cashItem.getCashItemSN() == sn) {
                        //assign the item it's stats and remove from array
                        item.setCashItemSN(cashItem.getCashItemSN());
                        item.setAccountID(cashItem.getAccountID());
                        item.setCharacterID(cashItem.getCharacterID());
                        item.setItemID(cashItem.getItemID());
                        item.setCommodityID(cashItem.getCommodityID());
                        item.setItemNumber(cashItem.getNumber());
                        item.setBuyCharacterName(cashItem.getBuyCharacterName());
                        item.setDateExpire(cashItem.getDateExpire());
                        it.remove();
                        break;// should only be 1 anyway
                    }
                }
                this.character.setItem(ti, pos, item);
                sendPacket(ShopPacket.onMoveLToS(pos, item, ti));
                this.modFlag |= ModFlag.ItemLocker | (ti == ItemType.Equip ? ModFlag.ItemSlotEquip : (ti == ItemType.Consume ? ModFlag.ItemSlotBundle : ModFlag.ItemSlotEtc));
            }
        }
    }

    private void onMoveSToL(InPacket packet) {
        if (this.cashShopAuthorized) {
            long sn = packet.decodeLong();
            byte ti = packet.decodeByte();
            int pos = character.findCashItemSlotPosition(ti, Math.abs(sn));
            if (pos <= 0) {
                Logger.logError("Inexistent cash item(sn: %d, ti: %d) in locker for characterID %d ", sn, ti, this.characterID);
                return;
            }
            ItemSlotBase item = this.character.getItem(ti, pos);
            if (item != null) {
                CashItemInfo cashItem = new CashItemInfo();
                cashItem.setCashItemSN(item.getCashItemSN());
                cashItem.setAccountID(item.getAccountID());
                cashItem.setCharacterID(item.getCharacterID());
                cashItem.setItemID(item.getItemID());
                cashItem.setCommodityID(item.getCommodityID());
                cashItem.setNumber(item.getItemNumber());
                cashItem.setBuyCharacterName(item.getBuyCharacterName());
                cashItem.setDateExpire(item.getDateExpire());
                this.cashItemInfo.add(cashItem);
                this.modFlag |= ModFlag.ItemLocker | (ti == ItemType.Equip ? ModFlag.ItemSlotEquip : (ti == ItemType.Consume ? ModFlag.ItemSlotBundle : ModFlag.ItemSlotEtc));
                this.character.setItem(ti, pos, null);
                sendPacket(ShopPacket.onMoveSToL(cashItem));
            }
        }
    }

    public void onPacket(byte type, InPacket packet) {
        Logger.logReport("[Packet Logger] [0x" + Integer.toHexString(type).toUpperCase() + "]: " + packet.dumpString());
        switch (type) {
            case ClientPacket.UserTransferFieldRequest:
                onTransferFieldRequest();
                break;
            case ClientPacket.CashShopChargeParamRequest:
                onChargeParamRequest(packet);
                break;
            case ClientPacket.CashShopQueryCashRequest:
                onQueryCashRequest();
                break;
            case ClientPacket.CashShopCashItemRequest:
                onCashItemRequest(packet);
                break;
        }
    }

    private void onQueryCashRequest() {
        sendRemainCashRequest();
    }

    public void onSocketDestroyed(boolean migrate) {
        User.unregisterUser(this);
        lockSocket.lock();
        try {
            socket = null;
        } finally {
            lockSocket.unlock();
        }
    }

    private void onTransferFieldRequest() {
        sendMigrateOutPacket();
    }

    private void sendMigrateOutPacket() {
        socket.onFilterMigrateOut();
    }

    public void sendPacket(OutPacket packet) {
        lockSocket.lock();
        try {
            if (socket != null) {
                socket.sendPacket(packet, false);
            }
        } finally {
            lockSocket.unlock();
        }
    }

    private void sendRemainCashRequest() {
        if (this.cashShopAuthorized) {
            sendPacket(ShopPacket.onQueryCash(this));
        }
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public void setNexonCash(int nexonCash) {
        this.nexonCash = nexonCash;
    }

    public void setNexonClubID(String nexonClubID) {
        this.nexonClubID = nexonClubID;
    }

    public final void unlock() {
        lock.unlock();
    }

    public boolean update(long cur) {
        if (isBlockedMachineID()) {
            closeSocket();
        }
        // checkCashItemExpire(cur); for now
        return true;
    }

    public class CashItemRequest {

        public static final byte LoadLockerDone = 10;
        public static final byte LoadLockerFailed = 11;
        public static final byte BuyDone = 12;
        public static final byte BuyFailed = 13;
        public static final byte GiftDone = 14;
        public static final byte GiftFailed = 15;
        public static final byte IncSlotCountDone = 16;
        public static final byte IncSlotCountFailed = 17;
        public static final byte MoveLtoSDone = 18;
        public static final byte MoveLToSFailed = 19 /*not sure if this is legit*/;
        public static final byte MoveSToLDone = 20;
        public static final byte MoveSToLFailed = 21;
        public static final byte DestroyDone = 22;
        public static final byte DestroyFailed = 23;
    }

    public class ModFlag {

        private static final byte NexonCash = 0x1;
        private static final byte ItemLocker = 0x2;
        private static final byte ItemSlotEquip = 0x4;
        private static final byte ItemSlotBundle = 0x8;
        private static final byte ItemSlotEtc = 0x10;
        private static final byte InventorySize = 0x20;
    }
}
