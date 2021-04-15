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
package common.user;

/**
 *
 * @author Eric
 */
public class DBChar {
    public static final int
            Character       = 0x1,
            ItemSlotEquip   = 0x2,
            ItemSlotConsume = 0x4,
            ItemSlotInstall = 0x8,
            ItemSlotEtc     = 0x10,
            ItemSlotCash    = 0x20,
            SkillRecord     = 0x40,
            QuestRecord     = 0x80,
            All             = 0xFF
    ;
}
