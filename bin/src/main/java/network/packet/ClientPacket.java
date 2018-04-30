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
package network.packet;

/**
 *
 * @author Eric
 */
public class ClientPacket {
    public static final short
            BEGIN_SOCKET = 0,
            CheckPassword = 1,
            WorldRequest = 2,
            SelectCharacter = 3,
            MigrateIn = 4,
            CreateNewCharacter = 6,
            DeleteCharacter = 7,
            AliveAck = 8,
            END_SOCKET = 11,
            BEGIN_USER = 12,
            UserTransferFieldRequest = 13,
            UserMigrateToCashShopRequest = 14,
            UserMove = 15,
            UserMeleeAttack = 16,
            UserShootAttack = 17,
            UserMagicAttack = 18,
            UserHit = 20,
            UserChat = 21,
            UserSelectNpc = 23,
            UserChangeStatRequest = 32
    ;
}
