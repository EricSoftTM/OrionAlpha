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
package game.field.life;

/**
 *
 * @author Eric
 */
public class MoveActionType {
    public static final byte
            Walk                = 1,
            Move                = 1,
            Stand               = 2,
            Jump                = 3,
            Alert               = 4,
            Prone               = 5,
            Fly1                = 6,
            Ladder              = 7,
            Rope                = 8,
            Dead                = 9,
            Sit                 = 10,
            Stand0              = 11,
            NO                  = 12
    ;
}
