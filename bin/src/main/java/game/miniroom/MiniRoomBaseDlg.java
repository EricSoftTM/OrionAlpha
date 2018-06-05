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

import common.item.ItemSlotBase;
import game.user.User;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author sunnyboy
 */
public class MiniRoomBaseDlg {

    public static OutPacket onChat(String speakerName, String text) {
        OutPacket oPacket = new OutPacket(LoopbackPacket.MiniRoom);
        oPacket.encodeByte(MiniRoomPacket.Chat.getType());
        oPacket.encodeByte(MiniRoomPacket.UserChat.getType());
        oPacket.encodeString(speakerName + " : " + text);
        return oPacket;
    }

    public static OutPacket onEnterBase(User pUser, MiniRoomBase miniroom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Enter.getType());
        miniroom.encodeAvatar(miniroom.findUserSlot(pUser), packet);
        return packet;
    }

    public static OutPacket onEnterDecline(MiniRoomBase miniroom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.EnterFailed.getType());
        miniroom.encodeAvatar(0, packet);
        return packet;
    }

    public static void onEnterResultBase(User user, MiniRoomBase miniroom, OutPacket packet) {
        packet.encodeByte(miniroom.maxUsers);
        packet.encodeByte(miniroom.findUserSlot(user));
        for (int i = 0; i < miniroom.maxUsers; i++) {
            miniroom.encodeAvatar(i, packet);
        }
        packet.encodeByte(-1);
        miniroom.encodeEnterResult(user, packet);
    }

    public static OutPacket onEnterResultStatic(User pUser, MiniRoomBase miniroom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.EnterResult.getType());
        if (miniroom != null) {
            packet.encodeByte(miniroom.getTypeNumber());
            onEnterResultBase(pUser, miniroom, packet);
        } else {
            packet.encodeByte(0); // You cannot enter the room.
        }
        return packet;
    }

    public static OutPacket onTrade() {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Trade.getType());
        return packet;
    }
//    public static OutPacket onExceedLimit() {
//        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
//        packet.encodeByte(MiniRoomPacket.LimitFail.getType());
//        return packet;
//    }

    public static OutPacket onInviteResultStatic(int invite, String characterName) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.InviteResult.getType());
        packet.encodeByte(invite);
        if (invite > MiniRoomInvite.NoCharacter) {
            packet.encodeString(characterName);
        }
        return packet;
    }

    public static OutPacket onInviteStatic(String inviter, int miniRoomSN) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.InviteUser.getType());
        packet.encodeString(inviter);
        packet.encodeInt(miniRoomSN);
        return packet;
    }

    public static OutPacket onLeave(int slot, User pUser, MiniRoomBase miniRoom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Leave.getType());
        miniRoom.encodeLeave(pUser, packet);
        return packet;
    }

    public static OutPacket onLeaveBase(int slot, int leaveType) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Leave.getType());
        packet.encodeByte(leaveType);
        return packet;
    }

    public static OutPacket onPutItem(int slot, int pos, ItemSlotBase item) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.PutItem_TR.getType());
        packet.encodeByte(slot);
        packet.encodeByte(pos);
        packet.encodeByte(item.getType());
        item.encode(packet);
        return packet;
    }

    public static OutPacket onPutMoney(int slot, int money) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.PutMoney.getType());
        packet.encodeByte(slot);
        packet.encodeInt(money);
        return packet;
    }
}
