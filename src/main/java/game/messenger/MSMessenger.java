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

import game.user.AvatarLook;
import game.user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author sunnyboy
 */
public class MSMessenger {

    private static final int MAX_CHARACTER = 3;
    
    private static final AtomicInteger msmSNCounter = new AtomicInteger();
    private static final Lock lockMSM = new ReentrantLock();
    private static final Map<Integer, MSMessenger> msMessenger = new HashMap<>();
    
    private final int msmSN;
    private final Lock lock;
    private int userCount;
    private final List<Character> character;

    public MSMessenger() {
        this.msmSN = msmSNCounter.incrementAndGet();
        this.lock = new ReentrantLock();
        this.userCount = 0;
        this.character = new ArrayList<>(MAX_CHARACTER);
        for (int i = 0; i < MAX_CHARACTER; i++) {
            character.add(i, new Character());
        }
    }

    public static void onInvite(User user, String targetName) {
        if (user.getMessenger().getMSM() != null) {
            User target = user.getChannel().findUserByName(targetName, true);
            if (user == target) {
                return;
            }
            if (target != null) {
                user.sendPacket(Messenger.onInviteResult(target.getCharacterName(), true));
                target.sendPacket(Messenger.onInvite(user.getCharacterName(), user.getMessenger().getMSM().getSN()));
            } else {
                user.sendPacket(Messenger.onInviteResult(targetName, false));
            }
        }
    }
    
    public void onBlocked(User user, String blockedUser, boolean blockDeny) {
        lock.lock();
        try {
            if (!isDestroyed()) {
                int idx = findIndex(user);
                if (idx >= 0) {
                    user.sendPacket(Messenger.onBlocked(blockedUser, blockDeny));
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void onAvatar(User user, AvatarLook al) {
        lock.lock();
        try {
            if (!isDestroyed()) {
                int idx = findIndex(user);
                if (idx >= 0) {
                    Character c = character.get(idx);
                    c.setAvatarLook(al);
                    for (int i = 0; i < MAX_CHARACTER; ++i) {
                        if (character.get(i).getUser() != null) {
                            if (idx != i) {
                                character.get(i).getUser().sendPacket(Messenger.onAvatar((byte) idx, c.getUser().getCharacter().getCharacterStat().getGender(), c.getUser().getCharacter().getCharacterStat().getFace(), c.getAvatarLook()));
                            }
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean onCreate(User user, AvatarLook al) {
        lock.lock();
        try {
            if (user.lock()) {
                try {
                    user.getMessenger().setMSM(this);
                    
                    ++userCount;
                    
                    Character c = character.get(0);
                    c.setUser(user);
                    c.setAvatarLook(al);
                    c.setID(user.getCharacter().getCharacterStat().getName());
                    
                    lockMSM.lock();
                    try {
                        msMessenger.put(msmSN, this);
                    } finally {
                        lockMSM.unlock();
                    }
                } finally {
                    user.unlock();
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private boolean onEnter(User user, AvatarLook al) {
        lock.lock();
        try {
            if (!isDestroyed() && userCount < MAX_CHARACTER) {
                int emptyIdx = findIndex(null);
                
                user.getMessenger().setMSM(this);
                
                character.get(emptyIdx).setUser(user);
                character.get(emptyIdx).setAvatarLook(al);
                character.get(emptyIdx).setID(user.getCharacter().getCharacterStat().getName());
                ++userCount;
                user.sendPacket(Messenger.onSelfEnterResult((byte) emptyIdx));
                for (int i = 0; i < MAX_CHARACTER; ++i) {
                    if (character.get(i).getUser() != null) {
                        if (i != emptyIdx) {
                            user.sendPacket(Messenger.onEnter((byte) i, character.get(i).getUser().getCharacter().getCharacterStat().getGender(), character.get(i).getUser().getCharacter().getCharacterStat().getFace(), character.get(i).getAvatarLook(), character.get(i).getID(), false));
                            character.get(i).getUser().sendPacket(Messenger.onEnter((byte) emptyIdx, character.get(emptyIdx).getUser().getCharacter().getCharacterStat().getGender(), character.get(emptyIdx).getUser().getCharacter().getCharacterStat().getFace(), character.get(emptyIdx).getAvatarLook(), character.get(emptyIdx).getID(), true));
                        }
                    }
                }
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public int findIndex(User user) {
        for (int i = 0; i < MAX_CHARACTER; ++i) {
            if (this.character.get(i).getUser() == user) {
                return i;
            }
        }
        return -1;
    }

    public static MSMessenger getMSM(int msmSN) {
        lockMSM.lock();
        try {
            return msMessenger.get(msmSN);
        } finally {
            lockMSM.unlock();
        }
    }

    public boolean isDestroyed() {
        return userCount == 0;
    }

    public static boolean onEnter(int sn, User user, AvatarLook al) {
        MSMessenger msm = null;
        if (sn != 0) {
            msm = getMSM(sn);
        }
        if (msm != null && msm.onEnter(user, al)) {
            return true;
        } else {
            if (sn != 0) {
                user.sendPacket(Messenger.onSelfEnterResult((byte) -1)); // afaik
            }
            msm = new MSMessenger();
            return msm.onCreate(user, al);
        }
    }

    public void onChat(User user, String chat) {
        lock.lock();
        try {
            if (!isDestroyed()) {
                int idx = findIndex(user);
                if (idx >= 0) {
                    for (int i = 0; i < MAX_CHARACTER; ++i) {
                        if (character.get(i).getUser() != null) {
                            User msmUser = character.get(i).getUser();
                            if (msmUser != user) {
                                msmUser.sendPacket(Messenger.onChat(chat));
                            }
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean onLeave(User user) {
        lock.lock();
        try {
            int idx = findIndex(user);
            if (idx >= 0) {
                user.getMessenger().setMSM(null);
                
                character.get(idx).setUser(null);
                character.get(idx).setID("");
                
                --this.userCount;
                for (int i = 0; i < MAX_CHARACTER; ++i) {
                    if (character.get(i).getUser() != null) {
                        character.get(i).getUser().sendPacket(Messenger.onLeave((byte) idx));
                    }
                }
                if (isDestroyed()) {
                    lockMSM.lock();
                    try {
                        msMessenger.remove(msmSN);
                    } finally {
                        lockMSM.unlock();
                    }
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public int getSN() {
        return msmSN;
    }
}
