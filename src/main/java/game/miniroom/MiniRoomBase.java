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

import game.miniroom.shop.PersonalShop;
import game.user.User;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
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
 * @author Eric
 */
public abstract class MiniRoomBase {

    private static final Map<Integer, MiniRoomEntry> miniRoomEntry = new HashMap<>();
    private static final AtomicInteger miniRoomSNCounter = new AtomicInteger(30000);

    private boolean opened;
    private boolean closeRequest;
    private int curUsers;
    private final List<Integer> reserved;
    private final List<Long> reservedTime;
    private final List<Integer> leaveRequest;
    private final Lock lock;
    private int maxUsers;
    private String title;
    private int balloonSN;
    private final int miniRoomSN;
    private final List<User> users;
    private Point host;

    public MiniRoomBase(int maxUsers) {
        this.miniRoomSN = miniRoomSNCounter.incrementAndGet();
        this.title = "";
        this.maxUsers = maxUsers;
        this.curUsers = 0;
        this.opened = false;
        this.closeRequest = false;
        this.host = new Point();
        this.users = new ArrayList<>(maxUsers);
        this.leaveRequest = new ArrayList<>(maxUsers);
        this.reservedTime = new ArrayList<>(maxUsers);
        this.reserved = new ArrayList<>(maxUsers);
        this.lock = new ReentrantLock();
        for (int i = 0; i < maxUsers; i++) {
            this.users.add(i, null);
            this.leaveRequest.add(i, -1);
            this.reservedTime.add(i, 0L);
            this.reserved.add(i, 0);
        }
    }
    
    private static MiniRoomBase miniRoomFactory(byte type) {
        MiniRoomBase miniRoom = null;
        if (type == MiniRoomType.TradingRoom) {
            miniRoom = new TradingRoom();
        } else if (type == MiniRoomType.PersonalShop) {
            miniRoom = new PersonalShop();
        }
        return miniRoom;
    }

    public static int enter(User user, int sn, InPacket packet) {
        MiniRoomBase miniRoom = getMiniRoom(sn);
        int enterResult = MiniRoomEnter.NoRoom;
        if (miniRoom != null) {
            int result = miniRoom.onEnterBase(user, packet);
            if (result == MiniRoomEnter.Success) {
                return result;
            }
            enterResult = result;
        }
        user.sendPacket(MiniRoomBaseDlg.onEnterResultStatic(user, null, enterResult));
        return enterResult;
    }

    private static MiniRoomBase getMiniRoom(int sn) {
        if (miniRoomEntry.containsKey(sn)) {
            return miniRoomEntry.get(sn).miniRoom;
        }
        return null;
    }

    public static void inviteResult(User user, int sn, int result) {
        MiniRoomBase miniRoom = getMiniRoom(sn);
        if (miniRoom != null) {
            miniRoom.onInviteResult(user, result);
        }
    }

    public static int create(User user, byte type, InPacket packet, boolean tournament, int round) {
        MiniRoomBase miniRoom = miniRoomFactory(type);
        if (miniRoom == null) {
            Logger.logError("Impossible MiniRoom created! New MiniRoomType found :: " + type);
            return MiniRoomEnter.Etc;
        }
        int result = miniRoom.onCreateBase(user, packet, round);
        if (result != MiniRoomEnter.Success) {
            user.sendPacket(MiniRoomBaseDlg.onEnterResultStatic(user, null, result));
        } else {
            user.sendPacket(MiniRoomBaseDlg.onEnterResultStatic(user, miniRoom, result));
        }
        return result;
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
            if (user != null && user.getField() != null) {
                onLeave(user, leaveType);
                if (leaveType != MiniRoomLeave.UserRequest) {
                    user.sendPacket(MiniRoomBaseDlg.onLeaveBase(idx, leaveType));
                }
                user.setMiniRoom(null);
                this.users.set(idx, null);
                --this.curUsers;
                if (broadCast) {
                    broadCast(MiniRoomBaseDlg.onLeave(idx, user, this), user);
                }
                if (this.curUsers == 0) {
                    removeMiniRoom();
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
                packet.encodeByte(idx);
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
                while (users.get(idx) != null || reserved.get(idx) != 0 && characterID != 0 && reserved.get(idx) != characterID) {
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
        if (user != null && maxUsers > 0) {
            lock.lock();
            try {
                for (byte i = 0; i < maxUsers; i++) {
                    if (users.get(i) == user) {
                        return i;
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return -1;
    }
    
    public int getBalloonSN() {
        return balloonSN;
    }

    public int getCurUsers() {
        return curUsers;
    }

    public int getMaxUsers() {
        return maxUsers;
    }
    
    public int getMiniRoomSN() {
        return miniRoomSN;
    }
    
    public String getTitle() {
        return title;
    }

    public List<User> getUsers() {
        return users;
    }

    public abstract int getCloseType();

    public abstract int getTypeNumber();

    public int isAdmitted(User user, InPacket packet, boolean onCreate) {
        if (user.getHP() <= 0) {
            return MiniRoomEnter.Dead;
        }
        int fieldID = 0;
        if (user.getField() != null) {
            fieldID = user.getField().getFieldID();
        }
        if (fieldID / 1000000 % 100 == 9) {
            return MiniRoomEnter.Event;
        }
        /*if (getTypeNumber() == MiniRoomType.PersonalShop && (fieldID == 0 || !user.getField().isPersonalShop())) {
            return MiniRoomEnter.NotAvailableField_PersonalShop;
        }*/
        if (onCreate) {
            if (getTypeNumber() == MiniRoomType.TradingRoom) {
                int characterID = packet.decodeInt();
                User target = user.getChannel().findUser(characterID);
                if (target == null) {
                    user.sendPacket(MiniRoomBaseDlg.onInviteResultStatic(MiniRoomInvite.NoCharacter, null));
                    return MiniRoomEnter.Success;
                }
                target.sendPacket(MiniRoomBaseDlg.onInviteStatic(getTypeNumber(), user.getCharacterName(), getMiniRoomSN()));
            } else if (getTypeNumber() == MiniRoomType.PersonalShop) {
                setTitle(packet.decodeString());
            }
        }
        return MiniRoomEnter.Success;
    }

    private boolean isEntrusted() {
        return false;
    }
    
    public boolean isOpened() {
        return opened;
    }
    
    public void onAvatarChanged(User user) {
        lock.lock();
        try {
            byte slot = findUserSlot(user);
            if (getCurUsers() != 0 && slot >= 0) {
                broadCast(MiniRoomBaseDlg.onAvatar(slot, user), user);
            }
        } finally {
            lock.unlock();
        }
    }

    private void onBalloonBase(User user, InPacket packet) {
        if (user.getField() == null) {
            return;
        }
        lock.lock();
        try {
            if (findUserSlot(user) != 0) {
                return;
            }
            if (user.getFootholdSN() > 0 && Math.abs(user.getCurrentPosition().x - host.x) <= 10 && Math.abs(user.getCurrentPosition().y - host.y) <= 10) {
                this.opened = true;
                user.setMiniRoomBalloon(packet.decodeBool());
            } else {
                onLeave(user, MiniRoomLeave.HostOut);
                user.sendPacket(MiniRoomBaseDlg.onLeaveBase(0, MiniRoomLeave.Closed));//WrongPosition
                user.setMiniRoom(null);
                users.set(0, null);
                removeMiniRoom();
            }
        } finally {
            lock.unlock();
        }
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
    
    private int onCreateBase(User user, InPacket packet, int round) {
        if (user.lock()) {
            try {
                this.host = new Point(user.getCurrentPosition());
                if (user.canAttachAdditionalProcess()) {
                    int result = isAdmitted(user, packet, true);
                    if (result == MiniRoomEnter.Success) {
                        user.setMiniRoom(this);
                        this.users.set(0, user);
                        this.leaveRequest.set(0, -1);
                        this.curUsers = 1;
                        miniRoomEntry.put(getMiniRoomSN(), new MiniRoomEntry(this));
                    }
                    return result;
                }
            } finally {
                user.unlock();
            }
        }
        return MiniRoomEnter.Busy;
    }

    private int onEnterBase(User user, InPacket packet) {
        int slot = findEmptySlot(user.getCharacterID());
        if (curUsers == 0 || users.get(0) == null) {
            return MiniRoomEnter.Etc;
        }
        if (slot < 0) {
            return MiniRoomEnter.Full;
        }
        if (findUser(slot) != null) {
            return MiniRoomEnter.Etc;
        }
        if (user.canAttachAdditionalProcess()) {
            int result = isAdmitted(user, packet, false);
            if (result == MiniRoomEnter.Success) {
                user.setMiniRoom(this);
                users.set(slot, user);
                reserved.set(slot, 0);
                leaveRequest.set(slot, -1);
                ++curUsers;
                broadCast(MiniRoomBaseDlg.onEnterBase(user, this), user);
                user.sendPacket(MiniRoomBaseDlg.onEnterResultStatic(user, this, 0));
                if (getTypeNumber() == MiniRoomType.PersonalShop && getCurUsers() == getMaxUsers()) {
                    setBalloon(true);
                }
            }
            return result;
        }
        return MiniRoomEnter.Busy;
    }
    
    private void onInviteBase(User user, InPacket packet) {
    
    }

    private void onInviteResult(User user, int result) {
        User inviter;
        if ((inviter = users.get(0)) != null) {
            inviter.sendPacket(MiniRoomBaseDlg.onInviteResultStatic(result, user.getCharacterName()));
        }
    }

    public void onLeave(User user, int leaveType) {
    
    }

    private void onLeaveBase(User user, InPacket packet) {
        int slot = findUserSlot(user);
        int leaveType, leaveType2;
        if (this.curUsers != 0 && slot >= 0) {
            if (getCloseType() == CloseType.Anyone) {
                leaveType = MiniRoomLeave.Closed;
                leaveType2 = MiniRoomLeave.UserRequest;
            } else {
                if (getCloseType() != CloseType.Host || slot != 0) {
                    doLeave(slot, MiniRoomLeave.UserRequest, true);
                    if (isOpened() && (getTypeNumber() != MiniRoomType.PersonalShop || getCurUsers() == getMaxUsers() - 1)) {
                        setBalloon(true);
                    }
                    return;
                }
                leaveType = leaveType2 = MiniRoomLeave.HostOut;
            }
            closeRequest(user, leaveType, leaveType2);
        }
    }

    public void onPacket(int type, User user, InPacket packet) {
    
    }

    public void onPacketBase(int type, User user, InPacket packet) {
        switch (type) {
            case MiniRoomPacket.Invite:
                onInviteBase(user, packet);
                break;
            case MiniRoomPacket.Chat:
                onChat(user, packet);
                break;
            case MiniRoomPacket.Leave:
                onLeaveBase(user, packet);
                break;
            case MiniRoomPacket.Balloon:
                onBalloonBase(user, packet);
                break;
            default:
                onPacket(type, user, packet);
        }
        processLeaveRequest();
    }
    
    public void onUserLeave(User user) {
        onPacketBase(MiniRoomPacket.Leave, user, null);
    }

    public void processLeaveRequest() {
        lock.lock();
        try {
            for (int i = 0; i < this.maxUsers; ++i) {
                if (this.users.get(i) != null && this.leaveRequest.get(i) >= 0) {
                    if (this.closeRequest && i == 0) {
                        setBalloon(false);
                    }
                    boolean broadCast = !this.closeRequest;
                    doLeave(i, this.leaveRequest.get(i), broadCast);
                    if (isOpened() && (getTypeNumber() != MiniRoomType.PersonalShop || getCurUsers() == getMaxUsers() - 1)) {
                        setBalloon(true);
                    }
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
    
    public void setBalloon(boolean open) {
        User user = users.get(0);
        if (user != null) {
            this.opened = open;
            user.setMiniRoomBalloon(open);
        }
    }
    
    public void setBalloonSN(int sn) {
        this.balloonSN = sn;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public class MiniRoomEntry {

        public MiniRoomBase miniRoom;
        
        public MiniRoomEntry(MiniRoomBase miniRoom) {
            this.miniRoom = miniRoom;
        }
    }
}
