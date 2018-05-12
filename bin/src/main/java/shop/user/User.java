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

import common.user.CharacterData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.database.ShopDB;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import shop.ShopPacket;
import shop.field.Stage;
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
    private byte level;
    private int localSocketSN;
    private final Lock lock;
    private final Lock lockGuardData;

    private final Lock lockSocket;
    private boolean migrateOutPosted;
    private boolean msMessenger;
    private int nexonCash;
    private String nexonClubID;

    private long nextCheckCashItemExpire;
    private int privateStatusID;

    private int purchaseExp;

    private FileTime registerDate;
    private ClientSocket socket;
    /*
     nMaplePoint   dd ?
     nGiftToken    dd ?*/
    public byte purchaseType;
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
     usModFlag     dw ?
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

        ShopDB.rawLoadAccount(characterID, User.this);
        this.character = ShopDB.rawLoadCharacter(characterID, User.this);

        if (this.nexonClubID != null && !this.nexonClubID.isEmpty()) {
            this.cashShopAuthorized = true;
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

        this.validateStat(true);
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

    public static synchronized final boolean registerUser(User user) {
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

    public List<CashItemInfo> GetCashItemInfo() {
        return this.cashItemInfo;
    }

    private void checkCashItemExpire(long cur) {
        if (cur - this.nextCheckCashItemExpire >= 0 && this.doCheckCashItemExpire != true) {
            FileTime ftCur;
            if ((ftCur = SystemTime.getLocalTime().systemTimeToFileTime()) != null) {
                this.nextCheckCashItemExpire = cur + 180000;
                for (Iterator<CashItemInfo> it = this.cashItemInfo.iterator(); it.hasNext();) {
                    CashItemInfo cashItem = it.next();
                    if (FileTime.compareFileTime(cashItem.getDateExpire(), ftCur) <= 0) {
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
        // probably save all the changes occured during cs trip
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

    private void onCashItemRequest(InPacket packet) {
        // uncoded still
        switch (packet.decodeByte()) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            case 13:
                break;
            default:
                this.sendRemainCashRequest();
                return;
        }
    }

    private void onChargeParamRequest(InPacket packet) {
        sendPacket(ShopPacket.onQueryCash(this)); // prevent getting stuck
    }

    public void onMigrateInSuccess() {
        Logger.logReport("User login from (%s)", this.characterName);
        sendPacket(Stage.onSetCashShop(this));
        sendPacket(ShopPacket.onQueryCash(this));
    }

    public void onPacket(byte type, InPacket packet) {
        switch (type) {
            case ClientPacket.UserTransferFieldRequest:
                onTransferFieldRequest();
                break;
            case ClientPacket.CashShopChargeParamRequest:
                onChargeParamRequest(packet);
                break;
            case ClientPacket.CashShopQueryCashRequest:
                onQueryCashRequest(packet);
                break;
            case ClientPacket.CashShopCashItemRequest:
                onCashItemRequest(packet);
                break;
        }
    }

    private void onQueryCashRequest(InPacket packet) {
        /*if (this.alreadyAcceptedRequest) {
         this.alreadyAcceptedRequest = true;
         if (tranxState != 0) {
         Logger.logError("Transaction state mismatch [State:%d]", tranxState);
         closeSocket();
         }*/
        sendRemainCashRequest();
        /*  this.tranxState = 4;
         }*/
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

    public boolean update(long cur) {
        if (isBlockedMachineID()) {
            closeSocket();
        }
        checkCashItemExpire(cur);
        return true;
    }

    private void validateStat(boolean calledByConstructor) {

    }
}
