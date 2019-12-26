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
package game.field.life.npc;

import game.field.MovePath;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class NpcPool {
    
    public static OutPacket onNpcEnterField(Npc npc) {
        OutPacket packet = new OutPacket(LoopbackPacket.NpcEnterField);
        npc.encodeInitData(packet);
        return packet;
    }
    
    public static OutPacket onNpcLeaveField(int id) {
        OutPacket packet = new OutPacket(LoopbackPacket.NpcLeaveField);
        packet.encodeInt(id);
        return packet;
    }
    
    public static OutPacket onNpcChangeController(Npc npc, boolean ctrl) {
        OutPacket packet = new OutPacket(LoopbackPacket.NpcChangeController);
        packet.encodeBool(ctrl);
        if (ctrl) {
            npc.encodeInitData(packet);
        } else {
            packet.encodeInt(npc.getGameObjectID());
        }
        return packet;
    }
    
    public static OutPacket onMove(int id, byte action, byte chatIdx, MovePath mp) {
        OutPacket packet = new OutPacket(LoopbackPacket.NpcMove);
        packet.encodeInt(id);
        packet.encodeByte(action);
        packet.encodeByte(chatIdx);
        if (mp != null) {
            mp.encode(packet);
        }
        return packet;
    }
}
