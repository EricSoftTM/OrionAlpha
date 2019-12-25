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
package game;

import game.user.User;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.GameAcceptor;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class Channel {
    private final byte channelID;
    private final String addr;
    private final short port;
    private final GameAcceptor acceptor;
    private final CenterSocket socket;
    private final Lock lockUser;
    private final Map<Integer, User> users;
    private final Map<String, User> userByName;
    
    public Channel(int channel, String addr, int port) {
        this.channelID = (byte) channel;
        this.addr = addr;
        this.port = (short) port;
        this.acceptor = new GameAcceptor(new InetSocketAddress(addr, port));
        this.socket = new CenterSocket(channel);
        this.lockUser = new ReentrantLock();
        this.users = new LinkedHashMap<>();
        this.userByName = new LinkedHashMap<>();
    }
    
    public final void broadcast(OutPacket packet) {
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
    
    public final void broadcastGMPacket(OutPacket packet) {
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
    
    public synchronized final User findUser(int characterID) {
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
    
    public synchronized final User findUserByName(String name, boolean makeLower) {
        lockUser.lock();
        try {
            if (makeLower)
                name = name.toLowerCase();
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
    
    public GameAcceptor getAcceptor() {
        return acceptor;
    }
    
    public String getAddr() {
        return addr;
    }
    
    public CenterSocket getCenter() {
        return socket;
    }
    
    public byte getChannelID() {
        return channelID;
    }
    
    public short getPort() {
        return port;
    }
    
    public final Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users.values());
    }
    
    public synchronized final boolean registerUser(User user) {
        lockUser.lock();
        try {
            if (users.containsKey(user.getCharacterID())) {
                return false;
            } else {
                users.put(user.getCharacterID(), user);
                userByName.put(user.getCharacterName().toLowerCase(), user);
                return true;
            }
        } finally {
            lockUser.unlock();
        }
    }
    
    public final void unregisterUser(User user) {
        lockUser.lock();
        try {
            users.remove(user.getCharacterID());
            userByName.remove(user.getCharacterName().toLowerCase());
        } finally {
            lockUser.unlock();
        }
    }
}
