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

    public static OutPacket onChat(int slot, String speakerName, String text) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Chat);
        packet.encodeByte(slot);
        packet.encodeString(speakerName + " : " + text);
        return packet;
    }

    public static OutPacket onEnterBase(User user, MiniRoomBase miniroom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Enter);
        miniroom.encodeAvatar(miniroom.findUserSlot(user), packet);
        return packet;
    }

    public static OutPacket onEnterDecline(MiniRoomBase miniroom, String characterName, int result) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.EnterFailed);
        packet.encodeString(characterName);
        packet.encodeByte(result);
        return packet;
    }

    public static void onEnterResultBase(User user, MiniRoomBase miniroom, OutPacket packet) {
        packet.encodeByte(miniroom.getMaxUsers());
        packet.encodeByte(miniroom.findUserSlot(user));
        for (int i = 0; i < miniroom.getMaxUsers(); i++) {
            miniroom.encodeAvatar(i, packet);
        }
        packet.encodeByte(-1);
        miniroom.encodeEnterResult(user, packet);
    }
    
    public static OutPacket onAvatar(int slot, MiniRoomBase miniroom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Avatar);
        miniroom.encodeAvatar(slot, packet);
        return packet;
    }

    public static OutPacket onEnterResultStatic(User user, MiniRoomBase miniroom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.EnterResult);
        if (miniroom != null) {
            packet.encodeByte(miniroom.getTypeNumber());
            onEnterResultBase(user, miniroom, packet);
        } else {
            packet.encodeByte(0); // You cannot enter the room.
        }
        return packet;
    }

    public static OutPacket onTrade() {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Trade);
        return packet;
    }

    public static OutPacket onInviteResultStatic(int invite, String targetName) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.InviteResult);
        packet.encodeByte(invite);
        if (invite > MiniRoomInvite.NoCharacter) {
            packet.encodeString(targetName);
        }
        return packet;
    }

    public static OutPacket onInviteStatic(String inviter, int miniRoomSN) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Invite);
        packet.encodeString(inviter);
        packet.encodeInt(miniRoomSN);
        return packet;
    }

    // Nexon's OnLeave packet simply calls EncodeLeave from TradingRoomDlg.
    // Sadly, the EncodeLeave is a nullsub, so it's not implemented yet..
    public static OutPacket onLeave(int slot, User user, MiniRoomBase miniRoom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Leave);
        miniRoom.encodeLeave(user, packet);
        return packet;
    }

    public static OutPacket onPutItem(int slot, int pos, ItemSlotBase item) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.PutItem);
        packet.encodeByte(slot);
        packet.encodeByte(pos);
        packet.encodeByte(item.getType());
        item.encode(packet);
        return packet;
    }

    public static OutPacket onPutMoney(int slot, int money) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.PutMoney);
        packet.encodeByte(slot);
        packet.encodeInt(money);
        return packet;
    }
}
