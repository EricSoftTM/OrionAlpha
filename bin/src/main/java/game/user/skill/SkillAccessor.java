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

import common.JobAccessor;
import common.JobCategory;
import common.user.CharacterData;
import game.user.skill.Skills.*;
import java.util.List;

/**
 *
 * @author Eric
 */
public class SkillAccessor {
    public static final int
            // The maximum damage that can be hit upon a monster from our client
            MAX_CLIENT_DAMAGE   = 9999,
            
            // The maximum per stat that a player can obtain and/or use.
            STR_MAX             = 999,
            DEX_MAX             = 999,
            INT_MAX             = 999,
            LUK_MAX             = 999,
            PAD_MAX             = 1999,
            MAD_MAX             = 1999,
            ACC_MAX             = 999,//Hmm
            EVA_MAX             = 999,//Hmm
            SPEED_MAX           = 140,
            JUMP_MAX            = 123,
            HP_MAX              = 30000,
            MP_MAX              = 30000,
            POP_MAX             = 30000,
            AP_MAX              = 200,
            SP_MAX              = 200
    ;
    // All available Teleport skills
    static final int[] TELEPORT = {
        Wizard1.Teleport, Wizard2.Teleport, Cleric.Teleport
    };
    /**
     * The formula that controls the randomized ranges between a HP/MP increase.
     * 
     * The formulation of the array is as follows:
     * [Job Category][Level Up][Inc Val]
     * 
     * Where IncVal arrays are as follows:
     * [Min HP Inc, MaxHP Inc, HP Inc Modulo Rand] [Min MP Inc, Max MP Inc, MP Inc Modulo Rand]
     * 
     * The numbers will always follow the same sequence for each job.
     */
    static final int[][][] incHPMP = { // [5][2][24]
        { {12, 16, 0, 10, 12, 20}, {8, 12, 0, 6, 8, 15} },//Beginner
        { {24, 28, 0, 4, 6, 20}, {20, 24, 0, 2, 4, 15,} },//Warrior
        { {10, 14, 0, 22, 24, 20}, {6, 10, 0, 18, 20, 15} },//Magician
        { {20, 24, 0, 14, 16, 20}, {16, 20, 0, 10, 12, 15} },//Bowman
        { {20, 24, 0, 14, 16, 20}, {16, 20, 0, 10, 12, 15} },//Thief
    };
    
    public static List<Integer> getSkillRootFromJob(int job, List<Integer> a) {
        if (JobAccessor.findJob(job) != null) {
            int jobCode;
            if (job % 1000 / 100 > 0) {
                jobCode = 100 * (10 * job / 1000);
                a.add(jobCode);
                if (job % 100 / 10 > 0) {
                    jobCode += 10 * (job % 100 / 10);
                    a.add(jobCode);
                }
            }
        }
        return a;
    }
    
    public static boolean isCorrectItemForBooster(int weaponType, int job) {
        switch (job) {
            case 110: // Fighter
                return weaponType == 30 || weaponType == 31 || weaponType == 40 || weaponType == 41;
            case 120: // Page
                return weaponType == 30 || weaponType == 32 || weaponType == 40 || weaponType == 42;
            case 130: // Spearman
                return weaponType == 43 || weaponType == 44;
            case 210: // Wizard, Ice Lightning
            case 220: // Wizard, Fire Poison
            case 230: // Cleric
                return weaponType == 37 || weaponType == 38;
            case 310: // Bowman
                return weaponType == 45;
            case 320: // Crossbowman
                return weaponType == 46;
            case 410: // Rogue
                return weaponType == 47;
            case 420: // Bandit
                return weaponType == 33;
            default: {
                return false;
            }
        }
    }
    
    public static int decHPVal(int job) {
        int val;
        switch (JobAccessor.getJobCategory(job)) {
            case JobCategory.None:
                val = 12;
                break;
            case JobCategory.Fighter:
                val = 54;
                break;
            case JobCategory.Wizard:
                val = 10;
                break;
            case JobCategory.Archer:
            case JobCategory.Thief:
                val = 20;
                break;
            default: {
                val = 0;
            }
        }
        return val;
    }
    
    public static int decMPVal(int job, int INT) {
        int val;
        switch (JobAccessor.getJobCategory(job)) {
            case JobCategory.None:
                val = 8;
                break;
            case JobCategory.Fighter:
                val = 4;
                break;
            case JobCategory.Wizard:
                val = -30 - 3 * INT / 40;
                break;
            case JobCategory.Archer:
            case JobCategory.Thief:
                val = 12;
                break;
            default: {
                val = 0;
            }
        }
        return val;
    }
    
    public static int incHPVal(int job) {
        int val;
        switch (JobAccessor.getJobCategory(job)) {
            case JobCategory.None:
                val = 8;
                break;
            case JobCategory.Fighter:
                val = 20;
                break;
            case JobCategory.Wizard:
                val = 6;
                break;
            case JobCategory.Archer:
            case JobCategory.Thief:
                val = 16;
                break;
            default: {
                val = 0;
            }
        }
        return val;
    }
    
    public static int incMPVal(int job) {
        int val;
        switch (JobAccessor.getJobCategory(job)) {
            case JobCategory.None:
                val = 6;
                break;
            case JobCategory.Fighter:
                val = 2;
                break;
            case JobCategory.Wizard:
                val = 18;
                break;
            case JobCategory.Archer:
            case JobCategory.Thief:
                val = 10;
                break;
            default: {
                val = 0;
            }
        }
        return val;
    }
    
    public static boolean isTeleportSkill(int skillID) {
        for (int teleport : TELEPORT) {
            if (skillID == teleport)
                return true;
        }
        return false;
    }
    
    public static int getTeleportSkillLevel(CharacterData cd) {
        int slv = 0;
        for (int teleport : TELEPORT) {
            if ((slv = SkillInfo.getInstance().getSkillLevel(cd, teleport, null)) > 0)
                return slv;
        }
        return slv;
    }
    
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
