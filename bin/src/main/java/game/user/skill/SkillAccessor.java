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
package game.user.skill;

import game.user.skill.Skills.*;

/**
 *
 * @author Eric
 */
public class SkillAccessor {
    
    public static boolean isSelfStatChange(int skillID) {
        switch (skillID) {
            case Warrior.IronBody:
            case Fighter.PowerGuard:
            case Page.PowerGuard:
            case Magician.MagicGuard:
            case Magician.MagicArmor:
            case Cleric.Invincible:
            case Archer.Focus:
            case Hunter.SoulArrow_Bow:
            case Crossbowman.SoulArrow_Crossbow:
            case Rogue.DarkSight:
                return true;
            default: {
                return false;
            }
        }
    }
    
    public static boolean isMobStatChange(int skillID) {
        switch (skillID) {
            case Page.Threaten:
            case Wizard1.Slow:
            case Wizard2.Slow:
                return true;
            default: {
                return false;
            }
        }
    }
    
    public static boolean isWeaponBooster(int skillID) {
        switch (skillID) {
            case Fighter.WeaponBooster:
            case Fighter.WeaponBoosterEx:
            case Page.WeaponBooster:
            case Page.WeaponBoosterEx:
            case Spearman.WeaponBooster:
            case Spearman.WeaponBoosterEx:
            case Hunter.BowBooster:
            case Crossbowman.CrossbowBooster:
            case Assassin.JavelinBooster:
            case Thief.DaggerBooster:
                return true;
            default: {
                return false;
            }
        }
    }
    
    public static boolean isPartyStatChange(int skillID) {
        switch (skillID) {
            case Fighter.Fury:
            case Spearman.IronWall:
            case Spearman.HyperBody:
            case Wizard1.Meditation:
            case Wizard2.Meditation:
            case Cleric.Heal:
            case Cleric.Bless:
            case Assassin.Haste:
            case Thief.Haste:
                return true;
            default: {
                return false;
            }
        }
    }
}
