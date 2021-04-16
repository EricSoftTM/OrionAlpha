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
import game.user.User;
import network.packet.InPacket;

/**
 *
 * @author sunnyboy
 */
public class MiniRoom {

    private static void onMRInviteResult(User user, InPacket packet) {
        int sn = packet.decodeInt();
        if (sn > 0) {
            MiniRoomBase.inviteResult(user, sn, packet.decodeByte());
        }
    }

    private static void onMRCreate(User user, InPacket packet) {
        byte type = packet.decodeByte();
        if (type > 0 && user.getField() != null) {
            MiniRoomBase.create(user, type, packet, false, 0);
        }
    }

    private static void onMREnter(User user, InPacket packet) {
        int sn = packet.decodeInt();
        if (sn > 0) {
            MiniRoomBase.enter(user, sn, packet);
        }
    }

    private static void onMRForward(User user, int type, InPacket packet) {
        MiniRoomBase miniRoom = user.getMiniRoom();
        if (miniRoom != null) {
            miniRoom.onPacketBase(type, user, packet);
        } else {
            user.sendCharacterStat(Request.Excl, 0);
        }
    }

    public static void onMiniRoom(User user, InPacket packet) {
        byte type = packet.decodeByte();
        switch (type) {
            case MiniRoomPacket.Create:
                onMRCreate(user, packet);
                break;
            case MiniRoomPacket.InviteResult:
                onMRInviteResult(user, packet);
                break;
            case MiniRoomPacket.Enter:
                onMREnter(user, packet);
                break;
            default:
                onMRForward(user, type, packet);
                break;
        }
    }
}
