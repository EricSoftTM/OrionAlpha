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
public class MiniRoomEnter {
    public static final int
            Success                         = 0,
            NoRoom                          = 1,
            Full                            = 2,
            Busy                            = 3,
            Dead                            = 4,
            Event                           = 5,
            PermissionDenied                = 6,
            NoTrading                       = 7,
            Etc                             = 8,
            OnlyInSameField                 = 9,
            NearPortal                      = 10,
            ExistMiniRoom                   = 11
    ;
}

