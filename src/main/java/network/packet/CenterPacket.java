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
public class CenterPacket {
    public static final byte
            BEGIN_SOCKET    = 0,
            InitGameSvr     = 1,
            InitShopSvr     = 2,
            END_SOCKET      = 3,
            BEGIN_GAME      = 4,
            ShopMigrateReq  = 5,
            ShopMigrateRes  = 6,
            END_GAME        = 7,
            BEGIN_SHOP      = 8,
            GameMigrateReq  = 9,
            GameMigrateRes  = 10,
            END_SHOP        = 11
    ;
}
