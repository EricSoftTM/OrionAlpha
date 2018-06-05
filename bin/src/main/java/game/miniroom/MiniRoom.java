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

import game.user.User;
import game.user.WvsContext;
import game.user.WvsContext.Request;
import network.packet.InPacket;

/**
 *
 * @author sunnyboy
 */
public class MiniRoom {

    private static void onInviteResult(InPacket packet) {
        int sn = packet.decodeInt();
        if (sn > 0) {
            MiniRoomBase.inviteResult(sn, packet.decodeString(), packet.decodeByte());
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
            MiniRoomBase.enter(user, sn);
        }
    }

    private static void onMRForward(User user, MiniRoomPacket type, InPacket packet) {
        MiniRoomBase pMiniRoom = user.getMiniRoom();
        if (pMiniRoom != null) {
            pMiniRoom.onPacketBase(type, user, packet);
        } else if (type.getType() >= MiniRoomPacket.PutMoney.getType() && type.getType() <= MiniRoomPacket.PutMoney.getType()) {
            user.sendCharacterStat(Request.Excl, 0);
        }
    }

    public static void onMiniRoom(User user, InPacket packet) {
        MiniRoomPacket type = MiniRoomPacket.get(packet.decodeByte());
        switch (type) {
            case Create:
                onMRCreate(user, packet);
                break;
            case EnterFailed:
                onInviteResult(packet);
                break;
            case Enter:
                onMREnter(user, packet);
                break;
            default:
                onMRForward(user, type, packet);
                break;
        }
    }
}
