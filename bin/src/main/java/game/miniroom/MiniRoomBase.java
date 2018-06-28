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

    private static final Map<Integer, MiniRoomEntry> miniRoomEntry = new HashMap<>();
    private static final AtomicInteger miniRoomSNCounter = new AtomicInteger(30000);

    private int balloonSN;
    private boolean closeRequest = false;
    private int curUsers = 0;
    private final List<Integer> reserved;
    private final List<Long> reservedTime;

    private final List<Integer> leaveRequest;
    private final ReentrantLock lockMiniRoom;
    private final Lock lock;
    private int maxUsers = 0;
    private final List<MiniRoomBase> miniEntry;
    private int miniRoomSN;
    private int miniRoomSpec;
    private Point pointHost;
    private int round;
    private final List<User> users;

    public MiniRoomBase(int maxUsers) {
        this.lockMiniRoom = new ReentrantLock();
        this.miniEntry = new LinkedList<>();
        this.miniRoomSN = miniRoomSNCounter.incrementAndGet();
        this.maxUsers = maxUsers;
        this.users = new ArrayList<>(maxUsers);
        this.leaveRequest = new ArrayList<>(maxUsers);
        this.reservedTime = new ArrayList<>(maxUsers);
        this.reserved = new ArrayList<>(maxUsers);
        this.lock = new ReentrantLock();
        this.pointHost = new Point();
        for (int i = 0; i < maxUsers; i++) {
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
            miniRoom = new TradingRoom();
        }
        return miniRoom;
    }

    public static int create(User userZero, byte type, InPacket packet, boolean tournament, int round) {
        int characterID = packet.decodeInt();
        User userOne = userZero.getChannel().findUser(characterID);
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
            for (User user : users) {
                if (user != null && user != except) {
                    user.sendPacket(packet);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void doLeave(int idx, int leaveType, boolean broadCast) {
        lock.lock();
        try {
            User user = this.users.get(idx);
            Field field;
            if (user != null && (field = user.getField()) != null && field.lock()) {
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
                    field.unlock();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void closeRequest(User user, int leaveType, int leaveType2) {
        lock.lock();
        try {
            this.closeRequest = true;
            for (int i = 0; i < this.maxUsers; i++) {
                this.leaveRequest.set(i, users.get(i) != user ? leaveType : leaveType2);
            }
        } finally {
            lock.unlock();
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

    public void encodeLeave(User user, OutPacket packet) {

    }

    public User findUser(int playerIdx) {
        lock.lock();
        try {
            if (maxUsers <= 0) {
                return null;
            }
            return users.get(playerIdx);
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
            int idx = 1;
            if (maxUsers <= 1) {
                idx = -1;
            } else {
                while (users.get(idx) != null || reserved.get(idx) > 0 && characterID > 0 && reserved.get(idx) != characterID) {
                    ++idx;
                    if (idx >= maxUsers) {
                        idx = -1;
                        break;
                    }
                }
            }
            return idx;
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
            byte slot = 0;
            if (maxUsers <= 0) {
                return -1;
            }
            while (users.get(slot) != user) {
                ++slot;
                if (slot >= maxUsers) {
                    break;
                }
            }
            return slot;
        } finally {
            lock.unlock();
        }
    }
    
    public int getCurUsers() {
        return curUsers;
    }
    
    public int getMaxUsers() {
        return maxUsers;
    }
    
    public List<User> getUsers() {
        return users;
    }

    public abstract int getCloseType();

    public abstract int getTypeNumber();

    public int isAdmitted(User user, boolean onCreate) {
        if (user.getCharacter().getCharacterStat().getHP() <= 0) {
            return 4;//MiniRoomEnter.Dead
        }
        return MiniRoomEnter.Success;
    }

    private boolean isEntrusted() {
        return false;
    }

    private void onChat(User user, InPacket packet) {
        lock.lock();
        try {
            int slot = findUserSlot(user);
            if (this.curUsers > 0 && slot >= 0) {
                String chatMsg = packet.decodeString();
                if (chatMsg != null) {
                    broadCast(MiniRoomBaseDlg.onChat(slot, user.getCharacterName(), chatMsg), null);
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
                    return MiniRoomEnter.Success;
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
        int result = isAdmitted(user, false);
        if (result > 0) {
            Logger.logReport("onEnterBase Stop %d", 5);
            user.setMiniRoom(null);
            return result;
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
            user.sendPacket(MiniRoomBaseDlg.onEnterDecline(miniRoom, character, result));
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

    public abstract void onPacket(int type, User user, InPacket packet);

    public void onPacketBase(int type, User user, InPacket packet) {
        switch (type) {
            case MiniRoomPacket.Chat:
                onChat(user, packet);
                break;
            case MiniRoomPacket.Leave:
                onLeaveBase(user, packet);
                break;
            default:
                onPacket(type, user, packet);
        }
        processLeaveRequest();
    }

    public void processLeaveRequest() {
        lock.lock();
        try {
            for (int i = 0; i < this.maxUsers; ++i) {
                if (this.users.get(i) != null && this.leaveRequest.get(i) >= 0) {
                    boolean broadCast = !this.closeRequest;
                    doLeave(i, this.leaveRequest.get(i), broadCast);
                }
            }
        } finally {
            lock.unlock();
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
