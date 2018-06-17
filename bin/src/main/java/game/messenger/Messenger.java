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
import java.util.List;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Logger;

/**
 *
 * @author Eric
 * @author sunnyboy
 */
public class Messenger {

    private final User user;

    public Messenger(User user) {
        this.user = user;
    }

    public void notifyAvatarChanged() {

    }

    public void onMSMEnter(InPacket packet) {
        if (user.isMsMessenger()) {
            user.setMsMessenger(true);
            MSMessenger.onEnter(packet.decodeInt(), user, user.getAvatarLook());
        }
    }

    private void onMSMLeave(MSMessenger msm, InPacket packet) {
        if (user.isMsMessenger()) {
            user.setMsMessenger(false);
            msm.onLeave(user);
        }
    }

    public void onMessenger(InPacket packet) {
        Logger.logReport("[Messenger] %s", packet.dumpString());

        MSMessenger msm = null;
        if ((user == null) || ((msm = user.getMsmMessenger()) == null)) {
            return;
        }
        byte type = packet.decodeByte();
        switch (type) {
            case MessengerPacket.Enter:
                onMSMEnter(packet);
                break;
            case MessengerPacket.Leave:
                onMSMLeave(msm, packet);
                break;
            case MessengerPacket.Blocked:
                break;
            case MessengerPacket.Invite:
                MSMessenger.onInvite(user, packet.decodeString());
                break;
            case MessengerPacket.Chat:
                msm.onChat(user, packet.decodeString());
                break;
        }
    }

    public static OutPacket onEnter(byte idx, byte gender, int face, AvatarLook avatarLook, String characterName, boolean isNew) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.Enter);
        packet.encodeByte(idx);
        packet.encodeByte(gender);
        packet.encodeInt(face);
        avatarLook.encode(packet);
        packet.encodeString(characterName);
        packet.encodeBool(isNew);
        return packet;
    }

    public static OutPacket onInvite(String inviter, int sn) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.Invite);
        packet.encodeString(inviter);
        packet.encodeInt(sn);
        return packet;
    }

    public static OutPacket onInviteResult(String targetName, boolean blockDeny) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.InviteResult);
        packet.encodeString(targetName);
        packet.encodeBool(blockDeny);
        return packet;
    }

    public static OutPacket onBlocked(String blockedUser, boolean blockDeny) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.Blocked);
        packet.encodeString(blockedUser);
        packet.encodeBool(blockDeny); // true: block, false: deny
        return packet;
    }

    public static OutPacket onChat(String text) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.Chat);
        packet.encodeString(text);
        return packet;
    }

    public static OutPacket onAvatar(byte idx, byte gender, int face, AvatarLook avatarLook) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.Avatar);
        packet.encodeByte(idx);
        packet.encodeByte(gender);
        packet.encodeInt(face);
        avatarLook.encode(packet);
        return packet;
    }

    public static OutPacket onSelfEnterResult(byte idx) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.SelfEnterResult);
        packet.encodeByte(idx);
        return packet;
    }

    public static OutPacket onLeave(byte idx) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.Leave);
        packet.encodeByte(idx);
        return packet;
    }

    public static OutPacket onMigrated(List<Integer> flags, List<User> users) {
        OutPacket packet = new OutPacket(LoopbackPacket.Messenger);
        packet.encodeByte(MessengerPacket.Migrated);
        for (int i = 0; i < 3; i++) {
            packet.encodeByte(flags.get(i));
            if (flags.get(i) != 0 && flags.get(i) != 1) {
                packet.encodeByte(users.get(i).getCharacter().getCharacterStat().getGender());
                packet.encodeInt(users.get(i).getCharacter().getCharacterStat().getFace());
                users.get(i).getAvatarLook().encode(packet);
                packet.encodeString(users.get(i).getCharacterName());
            }
        }
        return packet;
    }
}
