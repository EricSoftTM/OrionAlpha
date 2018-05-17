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
package game.field.life.miniroombase;

import game.user.User;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import network.packet.InPacket;

/**
 *
 * @author sunnyboy
 */
public abstract class MiniRoomBase {

    public int balloonSN;
    public boolean closeRequest = false;
    public int curUsers = 0;
    public List<Integer> leaveRequest;
    private ReentrantLock lockMiniRoom = new ReentrantLock(); 
    public int maxUsers = 0;
    public List<MiniRoomBase> miniEntry = new LinkedList<>();
    public int miniRoomSN;
    public AtomicInteger miniRoomSNCounter;
    public int miniRoomSpec;
    public boolean opened; // prob dont need
    public Point pointHost = new Point();
    public int round;
    public List<User> users;
    public static Map<Integer, MiniRoomEntry> miniRoomEntry = new HashMap<>();

    public MiniRoomBase(int nMaxUsers) {
        this.lockMiniRoom = new ReentrantLock();
        this.miniEntry = new LinkedList<>();
        this.miniRoomSNCounter = new AtomicInteger(0);
        this.miniRoomSN = miniRoomSNCounter.incrementAndGet();
        this.maxUsers = nMaxUsers;
        this.users = new ArrayList<>(8);
        this.leaveRequest = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            this.users.add(i, null);
            this.leaveRequest.add(i, -1);
        }
    }

    public abstract int getCloseType();

    public abstract int getTypeNumber();

    public int isAdmitted(User user, InPacket packet, boolean onCreate) {
        return 0;
    }

    public abstract void onLeave(User user, int leaveType);

    public abstract void onPacket(byte type, User user, InPacket packet);

    public class MiniRoomEntry {

        public MiniRoomBase miniRoom;
    }
}
