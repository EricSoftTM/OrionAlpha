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
package game.user;

import game.field.MovePath;
import game.field.life.AttackIndex;
import game.user.User.UserEffect;
import game.user.stat.SecondaryStat;
import java.awt.Point;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class UserRemote {
    
    public static OutPacket onEffect(int characterID, int userEffect, int skillID, int slv) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEffectLocal);
        packet.encodeInt(characterID);
        packet.encodeByte(userEffect);
        switch (userEffect) {
            case UserEffect.LevelUp:
                break;
            case UserEffect.SkillUse:
                packet.encodeInt(skillID);
                packet.encodeByte(slv);
                break;
            case UserEffect.SkillAffected:
                packet.encodeInt(skillID);
                packet.encodeByte(slv);
                break;
        }
        return packet;
    }
    
    public static OutPacket onTemporaryStatSet(int characterID, SecondaryStat ss, int flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserTemporaryStatSet);
        packet.encodeInt(characterID);
        ss.encodeForRemote(packet, flag);
        return packet;
    }
    
    public static OutPacket onResetTemporaryStat(int characterID, int flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserTemporaryStatReset);
        packet.encodeInt(characterID);
        packet.encodeInt(flag);
        return packet;
    }
    
    public static OutPacket onEmotion(int characterID, int emotion) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEmotion);
        packet.encodeInt(characterID);
        packet.encodeInt(emotion);
        return packet;
    }
    
    // TODO: Probably handle this in LifePool actually, ignore it here.
    public static OutPacket onAttack(int characterID) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserMeleeAttack);
        packet.encodeInt(characterID);
        return packet;
    }
    
    public static OutPacket onMove(int characterID, MovePath mp) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserMove);
        packet.encodeInt(characterID);
        mp.encode(packet);
        return packet;
    }
    
    public static OutPacket onHit(int characterID, byte mobAttackIdx, int clientDamage, int mobTemplateID, byte left, byte reflect, int mobID, byte hitAction, Point hit) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserHit);
        packet.encodeInt(characterID);
        packet.encodeByte(mobAttackIdx);
        packet.encodeInt(clientDamage);
        if (mobAttackIdx > AttackIndex.Counter) {
            packet.encodeInt(mobTemplateID);
            packet.encodeByte(left);
            packet.encodeByte(reflect);
            if (reflect > 0) {
                packet.encodeInt(mobID);
                packet.encodeByte(hitAction);
                packet.encodeShort(hit.x);
                packet.encodeShort(hit.y);
            }
        }
        return packet;
    }
    
    public static OutPacket onAvatarModified(User user, int avatarModFlag) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserAvatarModified);
        packet.encodeInt(user.getCharacterID());
        packet.encodeInt(avatarModFlag);
        if ((avatarModFlag & AvatarLook.Face) != 0) {
            packet.encodeInt(user.getCharacter().getCharacterStat().getFace());
        }
        if ((avatarModFlag & AvatarLook.Unknown2) != 0) {
            packet.encodeInt(0);
        }
        if ((avatarModFlag & AvatarLook.Unknown3) != 0) {
            packet.encodeInt(0);
        }
        if ((avatarModFlag & AvatarLook.Look) != 0) {
            packet.encodeBool(true);
            user.getAvatarLook().encode(packet);
            packet.encodeInt(0);
        } else {
            packet.encodeBool(false);
        }
        return packet;
    }
}
