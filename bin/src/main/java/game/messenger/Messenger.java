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
    private MSMessenger msm;

    public Messenger(User user) {
        this.user = user;
        this.msm = null;
    }

    public MSMessenger getMSM() {
        return msm;
    }
    
    public void notifyAvatarChanged() {
        if (user.isMSMessenger() && getMSM() != null) {
            getMSM().onAvatar(user, user.getAvatarLook());
        }
    }

    public void onMSMEnter(InPacket packet) {
        if (user.isMSMessenger()) {
            Logger.logError("Invalid messenger packet");
            user.closeSocket();
        } else {
            user.setMSMessenger(true);
            
            int sn = packet.decodeInt();
            MSMessenger.onEnter(sn, user, user.getAvatarLook());
        }
    }

    public void onMSMLeave(MSMessenger msm, InPacket packet) {
        if (user.isMSMessenger()) {
            user.setMSMessenger(false);
            msm.onLeave(user);
        } else {
            Logger.logError("Invalid messenger packet");
            user.closeSocket();
        }
    }
    
    public void onMSMForward(byte type, InPacket packet) {
        if (!user.isMSMessenger() && type != MessengerPacket.Blocked) {
            Logger.logError("Invalid messenger packet");
            user.closeSocket();
            return;
        }
        if (type == MessengerPacket.Invite) {
            String targetName = packet.decodeString();
            MSMessenger.onInvite(user, targetName);
        } else if (type == MessengerPacket.Chat) {
            String chat = packet.decodeString();
            // Temporary way to invite other users since korean is a meme.
            if (chat.contains("invite")) {
                String targetName = chat.split("invite ")[1];
                MSMessenger.onInvite(user, targetName);
                return;
            }
            if (getMSM() != null) {
                getMSM().onChat(user, chat);
            }
        } else if (type == MessengerPacket.Blocked) {
            String inviteUser = packet.decodeString();
            String blockedUser = packet.decodeString();
            boolean blockedDeny = packet.decodeBool();
            
            User target = User.findUserByName(inviteUser, true);
            if (target != null) {
                if (target.getMessenger().getMSM() != null) {
                    target.getMessenger().getMSM().onBlocked(target, blockedUser, blockedDeny);
                }
            }
        }
    }

    public void onMessenger(InPacket packet) {
        byte type = packet.decodeByte();
        switch (type) {
            case MessengerPacket.Enter:
                onMSMEnter(packet);
                break;
            case MessengerPacket.Leave:
                onMSMLeave(msm, packet);
                break;
            default: {
                onMSMForward(type, packet);
            }
        }
    }
    
    public void setMSM(MSMessenger msm) {
        this.msm = msm;
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
