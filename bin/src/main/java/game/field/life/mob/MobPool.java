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
package game.field.life.mob;

import game.field.MovePath;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class MobPool {
    
    public static OutPacket onMobEnterField(Mob mob) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobEnterField);
        mob.encodeInitData(packet);
        return packet;
    }
    
    public static OutPacket onMobLeaveField(int mobID, byte deadType) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobLeaveField);
        packet.encodeInt(mobID);
        packet.encodeByte(deadType);
        return packet;
    }
    
    public static OutPacket onAffected(int mobID, int skillID, short delay) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobAffected);
        packet.encodeInt(mobID);
        packet.encodeInt(skillID);
        packet.encodeShort(delay);
        return packet;
    }
    
    public static OutPacket onCtrlAck(int mobID, short mobCtrlSN, boolean nextAttackPossible, short mp) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobCtrlAck);
        packet.encodeInt(mobID);
        packet.encodeShort(mobCtrlSN);
        packet.encodeBool(nextAttackPossible);
        packet.encodeShort(mp);
        return packet;
    }
    
    public static OutPacket onMobChangeController(Mob mob, int mobID, byte level) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobChangeController);
        packet.encodeByte(level);
        if (level != 0) {
            mob.encodeInitData(packet);
        } else {
            packet.encodeInt(mobID);
        }
        return packet;
    }
    
    public static OutPacket onMove(int mobID, boolean nextAttackPossible, byte left, int skillID, MovePath mp) {
        OutPacket packet = new OutPacket(LoopbackPacket.MobMove);
        packet.encodeInt(mobID);
        packet.encodeBool(nextAttackPossible);
        packet.encodeByte(left);
        packet.encodeInt(skillID);
        mp.encode(packet);
        return packet;
    }
}
