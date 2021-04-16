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
import game.miniroom.shop.PersonalShop;
import game.user.User;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author sunnyboy
 * @author Eric
 */
public class MiniRoomBaseDlg {
    
    public static OutPacket onEnterResultStatic(User user, MiniRoomBase miniRoom, int messageCode) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.EnterResult);
        if (miniRoom != null) {
            packet.encodeByte(miniRoom.getTypeNumber());
            onEnterResultBase(user, miniRoom, packet);
        } else {
            packet.encodeByte(0);
            packet.encodeByte(messageCode); // You cannot enter the room.
        }
        return packet;
    }
    
    public static OutPacket onEnterBase(User user, MiniRoomBase miniRoom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Enter);
        miniRoom.encodeAvatar(miniRoom.findUserSlot(user), packet);
        miniRoom.encodeEnterResult(user, packet);
        return packet;
    }
    
    public static void onEnterResultBase(User user, MiniRoomBase miniRoom, OutPacket packet) {
        packet.encodeByte(miniRoom.getMaxUsers());
        packet.encodeByte(miniRoom.findUserSlot(user));
        for (int i = 0; i < miniRoom.getMaxUsers(); i++) {
            miniRoom.encodeAvatar(i, packet);
        }
        packet.encodeByte(-1);
        miniRoom.encodeEnterResult(user, packet);
    }
    
    public static OutPacket onAvatar(int slot, User user) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Avatar);
        packet.encodeByte(slot);
        user.encodeAvatar(packet);
        return packet;
    }
    
    public static OutPacket onChat(int slot, String speakerName, String text) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Chat);
        packet.encodeByte(slot);
        packet.encodeString(speakerName + " : " + text);
        return packet;
    }
    
    public static OutPacket onInviteStatic(int type, String inviter, int miniRoomSN) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Invite);
        packet.encodeByte(type);
        packet.encodeString(inviter);
        packet.encodeInt(miniRoomSN);
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
    
    public static OutPacket onLeave(int slot, User user, MiniRoomBase miniRoom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Leave);
        packet.encodeByte(slot);
        miniRoom.encodeLeave(user, packet);
        return packet;
    }
    
    public static OutPacket onLeaveBase(int slot, int leaveType) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Leave);
        packet.encodeByte(slot);
        packet.encodeByte(leaveType);
        return packet;
    }
    
    public static OutPacket onPutItem(int slot, int pos, ItemSlotBase item) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.PutItem_TR);
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

    public static OutPacket onTrade() {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Trade);
        return packet;
    }
    
    public static OutPacket onBuyResult(int result) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.BuyResult);
        packet.encodeByte(result);
        return packet;
    }
    
    public static OutPacket onRefresh(PersonalShop miniRoom) {
        OutPacket packet = new OutPacket(LoopbackPacket.MiniRoom);
        packet.encodeByte(MiniRoomPacket.Refresh);
        miniRoom.encodeItemList(packet);
        return packet;
    }
}
