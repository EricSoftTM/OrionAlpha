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
package game.user.stat;

/**
 *
 * @author Eric
 */
public class CharacterTemporaryStat {
    // TODO: Confirm and check these.
    // These are simply the first stats from 0x1->0x20000 that are used in v90.
    // That being said, they should follow the same from BMS too, so these may
    // be correct. However, Stun is definitely wrong - MobSkills don't exist yet!!
    public static final int
            PAD         = 0x1,
            PDD         = 0x2,
            MAD         = 0x4,
            MDD         = 0x8,
            ACC         = 0x10,
            EVA         = 0x20,
            Craft       = 0x40,
            Speed       = 0x80,
            Jump        = 0x100,
            MagicGuard  = 0x200,
            DarkSight   = 0x400,
            Booster     = 0x800,
            PowerGuard  = 0x1000,
            MaxHP       = 0x2000,
            MaxMP       = 0x4000,
            Invincible  = 0x8000,
            SoulArrow   = 0x10000,
            Stun        = 0x20000
    ;
}
