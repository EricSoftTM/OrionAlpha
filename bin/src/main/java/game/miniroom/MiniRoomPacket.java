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

import util.Logger;

/**
 *
 * @author sunnyboy
 */
public enum MiniRoomPacket {

    Create(0x0),
    //   CreateResult(0x1),
    InviteUser(0x1),//??
    InviteResult(0x2), // correct
    Enter(0x3), // yes
    EnterFailed(0x4), // correct
    EnterResult(0x5), // yes
    Chat(0x6), // yes
    Avatar(0x7), // yes (?)
    Leave(0x8),// yes
    UserChat(0x9), // ??
    PutItem_TR(0x0C),
    PutMoney(0x0D),
    Trade(0x0E), // 
    // MoveItemToInventory_TR(0xFF),
    // ItemCRC(0xFF),
    //LimitFail(0xFF),
    ;
    private final int packet;

    private MiniRoomPacket(int packet) {
        this.packet = packet;
    }

    public static MiniRoomPacket get(int packet) {
        Logger.logError("MiniRoomPacket Type %d", packet);
        for (MiniRoomPacket mrp : MiniRoomPacket.values()) {
            if (mrp.packet == packet) {
                return mrp;
            }
        }
        return null;
    }

    public int getType() {
        return packet;
    }
}
