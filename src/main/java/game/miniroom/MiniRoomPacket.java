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

/**
 *
 * @author sunnyboy
 */
public class MiniRoomPacket {
    public static final byte
            Create          = 0,
            Invite          = 1,//0049CB90
            InviteResult    = 2,//0049CEE0
            Enter           = 3,//0049D1C0
            EnterFailed     = 4,//004F8730
            EnterResult     = 5,//0049CA90
            Chat            = 6,//004F8BC0
            Avatar          = 7,//0049D270
            Leave           = 8,//0049C730
            GameMessage     = 9,//004F8D60
            Unknown10       = 10,//Can't find in client.
            Unknown11       = 11,//Can't find in client.
            PutItem         = 12,//004F8940
            PutMoney        = 13,//004F8B20
            Trade           = 14//004F8940
            // MoveItemToInventory
            // ItemCRC
            // LimitFail
    ;
}
