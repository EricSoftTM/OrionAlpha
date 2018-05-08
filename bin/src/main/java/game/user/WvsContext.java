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

import common.user.CharacterStat;
import game.user.item.ChangeLog;
import game.user.item.InventoryManipulator;
import game.user.skill.SkillRecord;
import game.user.stat.SecondaryStat;
import java.util.List;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class WvsContext {
    public class BroadcastMsg {
        public static final byte
                Notice  = 0,
                Alert   = 1
        ;
    }
    public class Request {
        public static final byte
                Normal  = -1,
                None    = 0,
                Excl    = 1
        ;
    }
    
    public static OutPacket onInventoryOperation(List<ChangeLog> changeLog, byte onExclResult) {
        return InventoryManipulator.makeInventoryOperation(onExclResult, changeLog);
    }
    
    public static OutPacket onInventoryGrow(int ti, int slotMax) {
        OutPacket packet = new OutPacket(LoopbackPacket.InventoryGrow);
        packet.encodeByte(ti);
        packet.encodeByte(slotMax);
        return packet;
    }
    
    public static OutPacket onStatChanged(byte onExclRequest, CharacterStat cs, SecondaryStat ss, int flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.StatChanged);
        packet.encodeByte(onExclRequest);
        if (cs != null) {
            cs.encodeChangeStat(packet, flag);
        } else {
            packet.encodeInt(0);
        }
        if (ss != null) {
            ss.encodeForLocal(packet);
        } else {
            packet.encodeInt(0);
        }
        return packet;
    }
    
    public static OutPacket onTemporaryStatReset(int flag) {
        OutPacket packet = new OutPacket(LoopbackPacket.TemporaryStatReset);
        packet.encodeInt(flag);
        return packet;
    }
    
    public static OutPacket onBroadcastMsg(byte type, String msg) {
        OutPacket packet = new OutPacket(LoopbackPacket.BroadcastMsg);
        packet.encodeByte(type);
        packet.encodeString(msg);
        return packet;
    }
    
    public static OutPacket onCharacterInfo(User user) {
        OutPacket packet = new OutPacket(LoopbackPacket.CharacterInfo);
        packet.encodeInt(user.getCharacterID());
        packet.encodeByte(user.getCharacter().getCharacterStat().getLevel());
        packet.encodeShort(user.getCharacter().getCharacterStat().getJob());
        packet.encodeShort(user.getCharacter().getCharacterStat().getPOP());
        packet.encodeString(user.getCommunity());
        return packet;
    }
    
    public static OutPacket onChangeSkillRecordResult(byte onExclRequest, List<SkillRecord> change) {
        OutPacket packet = new OutPacket(LoopbackPacket.ChangeSkillRecordResult);
        packet.encodeByte(onExclRequest);
        packet.encodeShort(change.size());
        for (SkillRecord skill : change) {
            packet.encodeInt(skill.getSkillID());
            packet.encodeInt(skill.getInfo());
        }
        return packet;
    }
    
    public static OutPacket onSkillUseResult(byte onExclRequest) {
        OutPacket packet = new OutPacket(LoopbackPacket.SkillUseResult);
        packet.encodeByte(onExclRequest);
        return packet;
    }
}
