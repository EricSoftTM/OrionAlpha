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
package game.messenger;

import game.GameApp;
import game.user.AvatarLook;
import game.user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author sunnyboy
 */
public class MSMessenger {

    private final int mSMSN;
    private final ReentrantLock lock;
    private int userCount;
    private final List<Character> character;
    private static final AtomicInteger count = new AtomicInteger();
    private static final Map<Integer, MSMessenger> mSMessenger = new HashMap<>();

    public MSMessenger() {
        this.mSMSN = count.incrementAndGet();
        this.lock = new ReentrantLock();
        this.userCount = 0;
        this.character = new ArrayList<>(3);
        for (int i = 0; i < character.size(); i++) {
            character.add(i, null);
        }
        mSMessenger.put(this.mSMSN, MSMessenger.this);
    }

    public static void onInvite(User user, String targetName) {
        if (user.getMsmMessenger() != null) {
            User target = User.findUserByName(targetName, true);
            if (target != null) {
                user.sendPacket(Messenger.onInviteResult(targetName, true));
                target.sendPacket(Messenger.onInvite(user.getCharacterName(), user.getMsmMessenger().getMSMSN()));
            } else {
                user.sendPacket(Messenger.onInviteResult(targetName, false));
            }
        }
    }

    private void onCreate(User user) {
        if (user != null && user.lock()) {
            if (user.isLogined()) { // does eric wanna use this?
                try {
                    Character msmChar = new Character();
                    msmChar.setAvatarLook(user.getAvatarLook());
                    msmChar.setId(user.getCharacter().getCharacterStat().getName());
                    msmChar.setChannelID(user.getChannelID());
                    character.set(0, msmChar);
                    user.setMsmMessenger(this);
                    ++userCount;
                    user.sendPacket(Messenger.onSelfEnterResult((byte) findIndex(user)));
                } finally {
                    user.unlock();
                }
            }
        }
    }

    private void onEnter(User user) {
        lock.lock();
        try {
            if (!isDestroyed() && userCount < 3) {
                int emptyIdx = findIndex(null);
                if (user.isLogined()) { // does eric wanna use this?
                    user.setMsmMessenger(this);
                    Character msmChar = new Character();
                    msmChar.setAvatarLook(user.getAvatarLook());
                    msmChar.setId(user.getCharacter().getCharacterStat().getName());
                    msmChar.setChannelID(user.getChannelID());
                    character.set(emptyIdx, msmChar);
                    ++userCount;
                    user.sendPacket(Messenger.onSelfEnterResult((byte) emptyIdx));
                    for (int i = 0; i < 3; ++i) {
                        if (character.get(i).getUser() != null) {
                            if (i != emptyIdx) {
                                user.sendPacket(Messenger.onEnter((byte) i, user.getCharacter().getCharacterStat().getGender(), user.getCharacter().getCharacterStat().getFace(), character.get(i).getAvatarLook(), character.get(i).getId(), true));
                                character.get(i).getUser().sendPacket(Messenger.onEnter((byte) emptyIdx, user.getCharacter().getCharacterStat().getGender(), user.getCharacter().getCharacterStat().getFace(), user.getAvatarLook(), character.get(i).getId(), true));
                            }
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public int findIndex(User pUser) {
        for (int i = 0; i < 3; ++i) {
            if (this.character.get(i).getUser() == pUser) {
                return i;
            }
        }
        return -1;
    }

    public static MSMessenger getMSM(int mSNID) {
        if (mSMessenger.containsKey(mSNID)) {
            return mSMessenger.get(mSNID);
        }
        return null;
    }

    public boolean isDestroyed() {
        return userCount == 0;
    }

    public static void onEnter(int sn, User user, AvatarLook avatarLook) {
        MSMessenger pMessenger = null;
        if (sn > 0) {
            pMessenger = getMSM(sn);
        }
        if (pMessenger != null) {
            pMessenger.onEnter(user);
        } else {
            if (sn > 0) {
                user.sendPacket(Messenger.onSelfEnterResult((byte) 255)); // afaik
            }
            pMessenger = new MSMessenger();
            pMessenger.onCreate(user);
        }
    }

    public void onChat(User user, String chat) {
        lock.lock();
        try {
            if (!isDestroyed()) {
                int idx = findIndex(user);
                if (idx >= 0) {
                    for (int i = 0; i < 3; ++i) {
                        if (character.get(i).getUser() != null) {
                            User msmUsers = character.get(i).getUser();
                            if (msmUsers != user) {
                                msmUsers.sendPacket(Messenger.onChat(chat));
                            }
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }

    }

    public void onLeave(User user) {
        int idx = findIndex(user);
        if (idx >= 0) {
            lock.lock();
            try {
                user.setMsmMessenger(null);
                
               // character.get(idx).setUser(null);
              //  character.get(idx).setId(null);
                
                this.character.set(idx, null); // do they keep user and just set the vars to empty/null?
                
                --this.userCount;
                for (int i = 0; i < 3; ++i) {
                    if (character.get(i).getUser() != null) {
                        character.get(i).getUser().sendPacket(Messenger.onLeave((byte) idx));
                    }
                }
                if (isDestroyed()) {
                    character.clear();
                    mSMessenger.remove(this.mSMSN);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public int getMSMSN() {
        return mSMSN;
    }
}
