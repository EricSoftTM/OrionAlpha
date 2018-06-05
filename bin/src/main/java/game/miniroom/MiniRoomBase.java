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

import game.field.Field;
import game.user.User;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;

/**
 *
 * @author sunnyboy
 */
public abstract class MiniRoomBase {

    public static final Map<Integer, MiniRoomEntry> miniRoomEntry = new HashMap<>();
    public static final AtomicInteger miniRoomSNCounter = new AtomicInteger(30000);

    public int balloonSN;
    public boolean closeRequest = false;
    public int curUsers = 0;
    public final List<Integer> reserved;
    public final List<Long> reservedTime;

    public List<Integer> leaveRequest;
    private final ReentrantLock lockMiniRoom;
    public final Lock lock;
    public int maxUsers = 0;
    public List<MiniRoomBase> miniEntry;
    public int miniRoomSN;
    public int miniRoomSpec;
    public Point pointHost;
    public int round;
    public List<User> users;

    public MiniRoomBase(int nMaxUsers) {
        this.lockMiniRoom = new ReentrantLock();
        this.miniEntry = new LinkedList<>();
        this.miniRoomSN = miniRoomSNCounter.incrementAndGet();
        this.maxUsers = nMaxUsers;
        this.users = new ArrayList<>(nMaxUsers);
        this.leaveRequest = new ArrayList<>(nMaxUsers);
        this.reservedTime = new ArrayList<>(nMaxUsers);
        this.reserved = new ArrayList<>(nMaxUsers);
        this.lock = new ReentrantLock();
        this.pointHost = new Point();
        for (int i = 0; i < nMaxUsers; i++) {
            this.users.add(i, null);
            this.leaveRequest.add(i, -1);
            this.reservedTime.add(i, 0L);
            this.reserved.add(i, 0);
        }
    }

    public static void enter(User user, int sn) {
        MiniRoomBase miniroom = getMiniRoom(sn);
        if (miniroom != null) {
            miniroom.onEnterBase(user, miniroom);
        }
    }

    private static MiniRoomBase getMiniRoom(int sn) {
        if (miniRoomEntry.containsKey(sn)) {
            return miniRoomEntry.get(sn).miniRoom;
        }
        return null;
    }

    public static void inviteResult(int sn, String character, int result) {
        MiniRoomBase miniRoom = getMiniRoom(sn);
        if (miniRoom != null) {
            miniRoom.onInviteResult(miniRoom, character, result);
        }
    }

    private static MiniRoomBase miniRoomFactory(byte type) {
        MiniRoomBase miniRoom = null;
        if (type == MiniRoomType.TradingRoom) {
            miniRoom = new TradingRoom(2);
        }
        return miniRoom;
    }

    public static int create(User userZero, byte type, InPacket inpacket, boolean tournament, int round) {
        int characterID = inpacket.decodeInt();
        User userOne = User.findUser(characterID);
        MiniRoomBase miniroom = miniRoomFactory(type);
        if (miniroom == null) {
            Logger.logError("Impossible MiniRoom created! New MiniRoomType found :: " + type);
            return -1;
        }
        if (userOne == null) {
            userZero.sendPacket(MiniRoomBaseDlg.onInviteResultStatic(MiniRoomInvite.NoCharacter, null));
            return 0;
        }
        int result = miniroom.onCreateBase(userZero, userOne);
        System.err.println("create :: result " + result);
        Logger.logReport("userZero %s miniroom object is %b", userZero.getCharacterName(), userZero.getMiniRoom());
        Logger.logReport("userOne %s miniroom object is %b", userOne.getCharacterName(), userOne.getMiniRoom());
        if (result == 0) {
            System.err.println("create :: 1 ");
            userZero.sendPacket(MiniRoomBaseDlg.onEnterResultStatic(userZero, miniroom));
            userOne.sendPacket(MiniRoomBaseDlg.onInviteStatic(userZero.getCharacterName(), miniroom.miniRoomSN));
        } else if (result == 1) {
            System.err.println("create :: 1 ");
            userZero.sendPacket(MiniRoomBaseDlg.onInviteResultStatic(MiniRoomInvite.CannotInvite, userOne.getCharacterName()));
        }

//        if (result == 0) {
//            if (userOne != null) {
//                Logger.logReport("user %s miniroom object is %b", userZero.getCharacterName(), userZero.getMiniRoom());
//                Logger.logReport("userTraded %s miniroom object is %b", userOne.getCharacterName(), userZero.getMiniRoom());
//                if (userOne.canAttachAdditionalProcess()) {
//                    System.err.println("create :: 1 ");
//                    userZero.sendPacket(MiniRoomBaseDlg.onEnterResultStatic(userZero, miniroom));
//                    userOne.sendPacket(MiniRoomBaseDlg.onInviteStatic(userZero.getCharacterName(), miniroom.miniRoomSN));
//                } else {
//                    System.err.println("create :: 2 ");
//                    userZero.sendPacket(MiniRoomBaseDlg.onInviteResultStatic(MiniRoomInvite.CannotInvite, userOne.getCharacterName()));
//                }
//            } else {
//                System.err.println("create :: 3 ");
//                userZero.sendPacket(MiniRoomBaseDlg.onInviteResultStatic(MiniRoomInvite.NoCharacter, null));
//            }
//        }
        return 0;
    }

    public void broadCast(OutPacket packet, User except) {
        lock.lock();
        try {
            for (User pUser : users) {
                if (pUser != null && pUser != except) {
                    pUser.sendPacket(packet);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void doLeave(int idx, int leaveType, boolean broadCast) {
        lockMiniRoom.lock();
        try {
            User user = this.users.get(idx);
            Field pField;
            if (user != null && (pField = user.getField()) != null && pField.lock()) {
                try {
                    Logger.logReport("DoLeave nLeaveType " + leaveType);
                    onLeave(user, leaveType);
                    if (leaveType > 0) {
                        user.sendPacket(MiniRoomBaseDlg.onLeave(idx, user, this)); // does this even do anything
                    }
                    user.setMiniRoom(null);
                    if (this.users.get(idx) != null) {
                        this.users.set(idx, null);
                    }
                    --this.curUsers;
                    if (broadCast) {
                        broadCast(MiniRoomBaseDlg.onLeave(0, user, this), user);// does this even do anything
                    }
                    if (this.curUsers == 0) {
                        removeMiniRoom();
                    }
                } finally {
                    pField.unlock();
                }
            }
        } finally {
            lockMiniRoom.unlock();
        }
    }

    public void closeRequest(User user, int leaveType, int leaveType2) {
        lockMiniRoom.lock();
        try {
            this.closeRequest = true;
            if (maxUsers > 0) {
                for (int i = 0; i < this.maxUsers; i++) {
                    int leaveTypeFinal = leaveType2;
                    if (this.users.get(i) != user) {
                        leaveTypeFinal = leaveType;
                    }
                    this.leaveRequest.set(i, leaveTypeFinal);
                }
            }
        } finally {
            lockMiniRoom.unlock();
        }
    }

    public void encodeAvatar(int idx, OutPacket packet) {
        if (idx != 0 || !isEntrusted()) {
            User user = this.users.get(idx);
            if (user != null) {
                packet.encodeByte(idx); // might be outside
                packet.encodeByte(user.getCharacter().getCharacterStat().getGender());
                packet.encodeInt(user.getCharacter().getCharacterStat().getFace());
                user.getAvatarLook().encode(packet);
                packet.encodeString(user.getCharacterName());
            }
        }
    }

    public void encodeEnterResult(User user, OutPacket packet) {
    }

    public void encodeLeave(User pUser, OutPacket packet) {

    }

    public User findUser(int idx) {
        lock.lock();
        try {
            if (maxUsers <= 0) {
                return null;
            }
            return users.get(idx);
        } finally {
            lock.unlock();
        }
    }

    private int findEmptySlot(int characterID) {
        lock.lock();
        try {
            if (maxUsers > 0) {
                for (int i = 0; i < maxUsers; i++) {
                    if (System.currentTimeMillis() - 30000 > reservedTime.get(i)) {
                        reservedTime.set(i, 0L);
                        reserved.set(i, 0);
                    }
                }
            }
            int nIdx = 1;
            if (maxUsers <= 1) {
                nIdx = -1;
            } else {
                while (users.get(nIdx) != null || reserved.get(nIdx) > 0 && characterID > 0 && reserved.get(nIdx) != characterID) {
                    ++nIdx;
                    if (nIdx >= maxUsers) {
                        nIdx = -1;
                        break;
                    }
                }
            }
            return nIdx;
        } finally {
            lock.unlock();
        }

    }

    public byte findUserSlot(User user) {
        if (user == null) {
            return -1;
        }
        lock.lock();
        try {
            byte nSlot = 0;
            if (maxUsers <= 0) {
                return -1;
            }
            while (users.get(nSlot) != user) {
                ++nSlot;
                if (nSlot >= maxUsers) {
                    break;
                }
            }
            return nSlot;
        } finally {
            lock.unlock();
        }

    }

    public abstract int getCloseType();

    public abstract int getTypeNumber();

    public int isAdmitted(User user, boolean onCreate) {
        if (user.getCharacter().getCharacterStat().getHP() <= 0) {
            return 4;
        }
        return 0;
    }

    private boolean isEntrusted() {
        return false;
    }

    private void onChat(User user, InPacket packet) {
        lock.lock();
        try {
            int nSlot = findUserSlot(user);
            if (this.curUsers > 0 && nSlot >= 0) {
                String sChatMsg = packet.decodeString();
                if (sChatMsg != null) {
                    broadCast(MiniRoomBaseDlg.onChat(user.getCharacterName(), sChatMsg), null);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private int onCreateBase(User userZero, User userOne) {
        lockMiniRoom.lock();
        try {
            // redo this add both user checks in 

            if (userZero.canAttachAdditionalProcess()) { // i'm available to begin a trade
                Logger.logReport("onCreateBase 1");
                if (userOne.canAttachAdditionalProcess()) { // but are you ready?
                    //   you're good? okay, let me setup my data
                    Logger.logReport("onCreateBase 2");
                    userZero.setMiniRoom(this);
                    this.pointHost = userZero.getCurrentPosition();
                    int admitted = this.isAdmitted(userZero, true);
                    if (admitted > 0) {
                        Logger.logReport("onCreateBase 3");
                        userZero.setMiniRoom(null);
                        return 2;
                    } else {
                        Logger.logReport("onCreateBase 4");
                        users.set(0, userZero);
                        leaveRequest.set(0, -1);
                        curUsers = 1;
                        MiniRoomEntry roomEntry = new MiniRoomEntry();
                        roomEntry.miniRoom = this;
                        miniRoomEntry.put(this.miniRoomSN, roomEntry);
                    }
                    return 0;
                } else {
                    return 1;
                }
            }
            Logger.logReport("onCreateBase 5");
            return 2;

//            int nRes = 0;
//            if (userZero.canAttachAdditionalProcess()) {
//                System.err.println("onCreateBase passed ");
//                userZero.setMiniRoom(this);
//                this.pointHost = userZero.getCurrentPosition();
//                int admitted = this.isAdmitted(userZero, true);
//                if (admitted > 0) {
//                    userZero.setMiniRoom(null);
//                } else {
//                    System.err.println("onCreateBase passed 2");
//                    users.set(0, userZero);
//                    //  adwReserved.set(0, 0);
//                    // anLeaveRequest.set(0, -1);
//                    curUsers = 1;
//                    MiniRoomEntry roomEntry = new MiniRoomEntry();
//                    roomEntry.miniRoom = this;
//                    miniRoomEntry.put(this.miniRoomSN, roomEntry);
//                }
//            } else {
//                nRes = 1;
//            }
//            return nRes;
        } finally {
            lockMiniRoom.unlock();
        }
    }

    private int onEnterBase(User user, MiniRoomBase miniroom) {
        int slot = findEmptySlot(user.getCharacterID());

        if (curUsers == 0 || users.get(0) == null) {
            Logger.logReport("onEnterBase Stop %d", 1);
            return 8;
        }
        if (slot < 0) {
            Logger.logReport("onEnterBase Stop %d", 2);
            return MiniRoomEnter.Full;
        }
        if (findUser(slot) != null) {
            Logger.logReport("onEnterBase Stop %d", 3);
            return 8;
        }
        if (!user.canAttachAdditionalProcess()) {
            Logger.logReport("onEnterBase Stop %d", 4);
            return MiniRoomEnter.Busy;
        }
        user.setMiniRoom(this);
        int nResult = isAdmitted(user, false);
        if (nResult > 0) {
            Logger.logReport("onEnterBase Stop %d", 5);
            user.setMiniRoom(null);
            return nResult;
        } else {
            Logger.logReport("onEnterBase Stop %d", 6);
            users.set(slot, user);
            reserved.set(slot, 0);
            leaveRequest.set(slot, -1);
            ++curUsers;
            User firstIndexUser = users.get(0);
            if (firstIndexUser != null) {
                firstIndexUser.sendPacket(MiniRoomBaseDlg.onEnterBase(user, miniroom));
            }
            user.sendPacket(MiniRoomBaseDlg.onEnterResultStatic(user, miniroom));
        }
        return MiniRoomEnter.Success;
    }

    private void onInviteResult(MiniRoomBase miniRoom, String character, int result) {
        User user;
        if ((user = users.get(0)) != null) {
            user.sendPacket(MiniRoomBaseDlg.onEnterDecline(miniRoom));
            // do i send another packet with char name and result? idunno lmao
            //   user.sendPacket(MiniRoomBaseDlg.onInviteResultStatic(result, character));
        }
    }

    public abstract void onLeave(User user, int leaveType);

    private void onLeaveBase(User user, InPacket packet) {
        int slot = this.findUserSlot(user);
        int leaveType, leaveType2;
        if (this.curUsers > 0 && slot >= 0) {
            if (slot != 0) {
                System.err.println("tryna trigger doLeave");
                this.doLeave(slot, 0, true);
            }
            leaveType2 = 3;
            leaveType = 3;
            closeRequest(user, leaveType, leaveType2);
        }
    }

    public abstract void onPacket(MiniRoomPacket type, User user, InPacket packet);

    public void onPacketBase(MiniRoomPacket type, User user, InPacket packet) {
        switch (type) {
            case Chat:
                onChat(user, packet);
                break;
            case Leave:
                onLeaveBase(user, packet);
                break;
            default:
                onPacket(type, user, packet);
                break;
        }
        processLeaveRequest();
    }

    public void processLeaveRequest() {
        lockMiniRoom.lock();
        try {
            for (int i = 0; i < this.maxUsers; ++i) {
                if (this.users.get(i) != null && this.leaveRequest.get(i) >= 0) {
                    boolean bBroadCast = !closeRequest;
                    this.doLeave(i, this.leaveRequest.get(i), bBroadCast);
                }
            }
        } finally {
            lockMiniRoom.unlock();
        }
    }

    private void removeMiniRoom() {
        miniRoomEntry.remove(this.miniRoomSN);
        this.curUsers = 0;

    }

    public class MiniRoomEntry {

        public MiniRoomBase miniRoom;
    }
}
