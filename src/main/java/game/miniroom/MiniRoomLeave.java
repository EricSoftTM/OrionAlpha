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
 * Mini Room Leave Types
 *
 * @author Eric
 */
public class MiniRoomLeave {
    public static final byte
            // MiniRoomLeave
            UserRequest = 0,
            Closed = 1,
            HostOut = 2,
            Booked = 3,
            // TradingRoomLeave
            TradeDone = 4,
            TradeFail = 5,
            // PersonalShopLeave
            NoMoreItem = 6;
}
