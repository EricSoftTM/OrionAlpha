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
import common.item.ItemAccessor;
import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import game.user.skill.Skills.*;
import game.user.stat.BasicStat;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class SkillAccessor {
    public static final int
            // The maximum damage that can be hit upon a monster from our client
            MAX_CLIENT_DAMAGE   = 9999,
    
            // Skill Options
            FinalRangeAttack = 0x1,
            SoulArrow = 0x2,
            
            // The maximum per stat that a player can obtain and/or use.
            STR_MAX             = 999,
            DEX_MAX             = 999,
            INT_MAX             = 999,
            LUK_MAX             = 999,
            PAD_MAX             = 999,
            PDD_MAX             = 999,
            MAD_MAX             = 999,
            MDD_MAX             = 999,
            ACC_MAX             = 999,
            EVA_MAX             = 999,
            SPEED_MAX           = 130,
            JUMP_MAX            = 123,
            HP_MAX              = 30000,
            MP_MAX              = 10000,
            POP_MAX             = 30000,//Unconfirmed
            AP_MAX              = 255,//Unconfirmed
            SP_MAX              = 255//Unconfirmed
    ;
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
    static final int[][][] INC_HP_MP = { // [5][2][24]
        { {15, 20, 0, 10, 12, 20}, {10, 15, 0, 6, 8, 15} },//Beginner
        { {27, 35, 0, 4, 6, 20}, {20, 25, 0, 2, 4, 15} },//Warrior
        { {10, 15, 0, 22, 24, 20}, {5, 10, 0, 18, 20, 15} },//Magician
        { {22, 27, 0, 14, 16, 20}, {15, 20, 0, 10, 12, 15} },//Bowman
        { {22, 27, 0, 14, 16, 20}, {15, 20, 0, 10, 12, 15} },//Thief
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
    
    public static int getEndureDuration(CharacterData cd) {
        int jobCategory = JobAccessor.getJobCategory(cd.getCharacterStat().getJob());
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        if (jobCategory == JobCategory.Fighter) {
            int slv = SkillInfo.getInstance().getSkillLevel(cd, Warrior.Endure, skillEntry);
            if (slv > 0) {
                return 1000 * skillEntry.get().getLevelData(slv).getTime();
            }
        } else if (jobCategory == JobCategory.Thief) {
            if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.Assassin.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Assassin.Endure, skillEntry);
                if (slv > 0) {
                    return 1000 * skillEntry.get().getLevelData(slv).getTime();
                }
            } else if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.Thief.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Thief.Endure, skillEntry);
                if (slv > 0) {
                    return 1000 * skillEntry.get().getLevelData(slv).getTime();
                }
            }
        }
        return 0;
    }
    
    public static int getHPRecoveryUpgrade(CharacterData cd) {
        int jobCategory = JobAccessor.getJobCategory(cd.getCharacterStat().getJob());
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        if (jobCategory == JobCategory.Fighter) {
            int slv = SkillInfo.getInstance().getSkillLevel(cd, Warrior.ImproveBasic, skillEntry);
            if (slv > 0) {
                return skillEntry.get().getLevelData(slv).getHP();
            }
        } else if (jobCategory == JobCategory.Thief) {
            if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.Assassin.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Assassin.Endure, skillEntry);
                if (slv > 0) {
                    return skillEntry.get().getLevelData(slv).getHP();
                }
            } else if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.Thief.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Thief.Endure, skillEntry);
                if (slv > 0) {
                    return skillEntry.get().getLevelData(slv).getHP();
                }
            }
        }
        return 0;
    }
    
    public static int getMPRecoveryUpgrade(CharacterData cd) {
        int jobCategory = JobAccessor.getJobCategory(cd.getCharacterStat().getJob());
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        if (jobCategory == JobCategory.Wizard) {
            return (int) ((double) SkillInfo.getInstance().getSkillLevel(cd, Magician.ImproveBasic, null) * (double) cd.getCharacterStat().getLevel() * 0.1d);
        } else if (jobCategory == JobCategory.Thief) {
            if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.Assassin.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Assassin.Endure, skillEntry);
                if (slv > 0) {
                    return skillEntry.get().getLevelData(slv).getMP();
                }
            } else if (JobAccessor.isCorrectJobForSkillRoot(cd.getCharacterStat().getJob(), JobAccessor.Thief.getJob())) {
                int slv = SkillInfo.getInstance().getSkillLevel(cd, Thief.Endure, skillEntry);
                if (slv > 0) {
                    return skillEntry.get().getLevelData(slv).getMP();
                }
            }
        }
        return 0;
    }
    
    public static boolean isCorrectItemForBooster(int weaponType, int job) {
        switch (JobAccessor.findJob(job)) {
            case Fighter:
                return weaponType == 30 || weaponType == 31 || weaponType == 40 || weaponType == 41;
            case Page:
                return weaponType == 30 || weaponType == 32 || weaponType == 40 || weaponType == 42;
            case Spearman:
                return weaponType == 43 || weaponType == 44;
            case Wizard_Fire_Poison:
            case Wizard_Thunder_Cold:
            case Cleric:
                return weaponType == 37 || weaponType == 38;
            case Hunter:
                return weaponType == 45;
            case Crossbowman:
                return weaponType == 46;
            case Assassin:
                return weaponType == 47;
            case Thief:
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
    
    public static boolean incMaxHPMP(CharacterData cd, BasicStat bs, int flag, boolean levelUp) {
        int hpInc = 0;
        int mpInc = 0;
        short job = bs.getJob();
        int jobCategory = job / 100;
        boolean inc = false;
        boolean incHP = (flag & CharacterStatType.MHP) != 0;
        boolean incMP = (flag & CharacterStatType.MMP) != 0;
        if (jobCategory < 0 || jobCategory > 4) {
            return inc;
        }
        if (JobAccessor.findJob(job) != null) {
            int minHP  = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][0];
            int maxHP  = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][1];
            int randHP = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][2];//Useless, always 0 and Nexon only has a MP rand for INT.
            int minMP  = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][3];
            int maxMP  = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][4];
            int randMP = INC_HP_MP[jobCategory][!levelUp ? 1 : 0][5];
            if (incHP) {//Nexon uses the C++ engine RNG, we will use the JVM's RNG.
                hpInc = minHP + ThreadLocalRandom.current().nextInt(Short.MAX_VALUE) % (maxHP - minHP + 1);
            }
            if (incMP) {
                mpInc = ThreadLocalRandom.current().nextInt(Short.MAX_VALUE) % (maxMP - minMP + 1) + minMP + bs.getINT() * randMP / 200;
            }
            if (incHP) {
                int skillID = Warrior.MHPInc;
                SkillEntry hpIncSkill = SkillInfo.getInstance().getSkill(skillID);
                if (cd.getSkillRecord().containsKey(skillID)) {
                    int skillLevel;
                    if ((skillLevel = cd.getSkillRecord().get(skillID)) > 0 && hpIncSkill != null) {
                        if (skillLevel >= hpIncSkill.getLevelData().length)
                            skillLevel = hpIncSkill.getLevelData().length;
                        if (levelUp)
                            hpInc += hpIncSkill.getLevelData(skillLevel).getX();
                        else
                            hpInc += hpIncSkill.getLevelData(skillLevel).getY();
                    }
                }
            }
            if (incMP) {
                int skillID = Magician.MMPInc;
                SkillEntry mpIncSkill = SkillInfo.getInstance().getSkill(skillID);
                if (cd.getSkillRecord().containsKey(skillID)) {
                    int skillLevel;
                    if ((skillLevel = cd.getSkillRecord().get(skillID)) > 0 && mpIncSkill != null) {
                        if (skillLevel >= mpIncSkill.getLevelData().length)
                            skillLevel = mpIncSkill.getLevelData().length;
                        if (levelUp)
                            mpInc += mpIncSkill.getLevelData(skillLevel).getX();
                        else
                            mpInc += mpIncSkill.getLevelData(skillLevel).getY();
                    }
                }
            }
            if (incHP) {
                if (cd.getCharacterStat().getMHP() < HP_MAX) {
                    cd.getCharacterStat().setMHP((short) Math.min(Math.max(hpInc + cd.getCharacterStat().getMHP(), 50), HP_MAX));
                    inc = true;
                }
            }
            if (incMP) {
                if (cd.getCharacterStat().getMMP() < MP_MAX) {
                    cd.getCharacterStat().setMMP((short) Math.min(Math.max(mpInc + cd.getCharacterStat().getMMP(), 5), MP_MAX));
                    inc = true;
                }
            }
        }
        return inc;
    }
    
    public static boolean isTeleportSkill(int skillID) {
        final int[] skills = { Wizard1.Teleport, Wizard2.Teleport, Cleric.Teleport };
        
        for (int skill : skills) {
            if (skillID == skill)
                return true;
        }
        return false;
    }
    
    public static int getTeleportSkillLevel(CharacterData cd) {
        int skill = 0;
        switch (JobAccessor.findJob(cd.getCharacterStat().getJob())) {
            case Wizard_Fire_Poison:
                skill = Wizard1.Teleport;
                break;
            case Wizard_Thunder_Cold:
                skill = Wizard2.Teleport;
                break;
            case Cleric:
                skill = Cleric.Teleport;
                break;
        }
        
        return SkillInfo.getInstance().getSkillLevel(cd, skill, null);
    }
    
    public static int getMPStealSkillData(CharacterData cd, int attackType, Pointer<Integer> prop, Pointer<Integer> percent) {
        prop.set(0);
        percent.set(0);
        if (attackType != 3)
            return 0;
        
        int skill = 0;
        switch (JobAccessor.findJob(cd.getCharacterStat().getJob())) {
            case Wizard_Fire_Poison:
                skill = Wizard1.MPEater;
                break;
            case Wizard_Thunder_Cold:
                skill = Wizard2.MPEater;
                break;
            case Cleric:
                skill = Cleric.MPEater;
                break;
        }
        
        if (skill != 0) {
            Pointer<SkillEntry> skillEntry = new Pointer<>(null);
            int slv = SkillInfo.getInstance().getSkillLevel(cd, skill, skillEntry);
            if (slv > 0) {
                if (skillEntry.get() != null) {
                    prop.set(skillEntry.get().getLevelData(slv).getProp());
                    percent.set(skillEntry.get().getLevelData(slv).getX());
                }
                return skill;
            }
        }
        
        return 0;
    }
    
    public static int getMasteryFromSkill(CharacterData cd, int skillID, Pointer<Integer> inc) {
        int mastery = 0;
        
        SkillEntry skill = SkillInfo.getInstance().getSkill(skillID);
        if (skill != null) {
            int slv = SkillInfo.getInstance().getSkillLevel(cd, skillID, null);
            if (slv != 0) {
                if (inc != null) {
                    inc.set(skill.getLevelData(slv).getX());
                }
                mastery = skill.getLevelData(slv).getMastery();
            }
        }
        
        return mastery;
    }

    
    public static int getWeaponMastery(CharacterData cd, int weaponItemID, int attackType, Pointer<Integer> accInc) {
        final int MELEE = 1, SHOOT = 2;
        
        int wt = ItemAccessor.getWeaponType(weaponItemID);
        int mastery = 0;
        switch (wt) {
            case 30: //OneHand_Sword
            case 40: //TowHand_Sword
                if (attackType == MELEE) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Fighter.WeaponMastery, accInc);
                    if (mastery == 0)
                        mastery = SkillAccessor.getMasteryFromSkill(cd, Page.WeaponMastery, accInc);
                }
                break;
            case 31: //OneHand_Axe
            case 41: //TowHand_Axe
                if (attackType == MELEE) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Fighter.WeaponMasteryEx, accInc);
                    if (mastery == 0)
                        mastery = SkillAccessor.getMasteryFromSkill(cd, Page.WeaponMasteryEx, accInc);
                }
                break;
            case 32: //OneHand_Mace
            case 42: //TowHand_Mace
                if (attackType == MELEE) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Page.WeaponMasteryEx, accInc);
                    if (mastery == 0)
                        mastery = SkillAccessor.getMasteryFromSkill(cd, Fighter.WeaponMasteryEx, accInc);
                }
                break;
            case 45: //Bow
                if (attackType == SHOOT) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Hunter.BowMastery, accInc);
                }
                break;
            case 46: //CrossBow
                if (attackType == SHOOT) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Crossbowman.CrossbowMastery, accInc);
                }
                break;
            case 47: //ThrowingGloves
                if (attackType == SHOOT) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Assassin.JavelinMastery, accInc);
                }
                break;
            case 33: //Dagger
                if (attackType == MELEE) {
                    mastery = SkillAccessor.getMasteryFromSkill(cd, Thief.DaggerMastery, accInc);
                }
                break;
        }
        return mastery;
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
