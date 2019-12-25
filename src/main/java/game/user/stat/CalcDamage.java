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

import common.JobAccessor;
import common.JobCategory;
import common.item.ItemAccessor;
import common.user.CharacterData;
import game.field.life.mob.MobStat;
import game.field.life.mob.MobStats;
import game.user.item.ItemInfo;
import game.user.skill.SkillAccessor;
import game.user.skill.SkillEntry;
import game.user.skill.Skills.Rogue;
import java.util.List;
import util.Rand32;

/**
 *
 * @author Eric
 */
public class CalcDamage {
    private static final int RND_SIZE = 7;
    
    private final Rand32 rndGenForCharacter;
    private final Rand32 rndForCheckDamageMiss;
    private final Rand32 rndGenForMob;
    private int invalidCount;
    
    public CalcDamage() {
        this.rndGenForCharacter = new Rand32();
        this.rndForCheckDamageMiss = new Rand32();
        this.rndGenForMob = new Rand32();
        this.invalidCount = 0;
    }
    
    public boolean checkMDamageMiss(MobStat ms, CharacterData cd, BasicStat bs, SecondaryStat ss, long randForMissCheck) {
        int eva = Math.max(0, Math.min(ss.eva + ss.getStatOption(CharacterTemporaryStat.EVA), SkillAccessor.EVA_MAX));
        int level = bs.getLevel();
        eva -= (ms.getLevel() - level) / 2;
        int mobACC = 0;
        if (level >= ms.getLevel() || eva > 0) 
            mobACC = eva;
        double rand = (double) mobACC;
        double b = rand * 0.1d;
        rand = getRand(randForMissCheck, b, rand);
        int acc = Math.max(0, Math.min(ms.getACC() + ms.getStatOption(MobStats.ACC), SkillAccessor.ACC_MAX));
        return rand >= (double) acc;
    }
    
    public boolean checkPDamageMiss(MobStat ms, CharacterData cd, BasicStat bs, SecondaryStat ss, long randForMissCheck) {
        int eva = Math.max(0, Math.min(ss.eva + ss.getStatOption(CharacterTemporaryStat.EVA), SkillAccessor.EVA_MAX));
        int level = bs.getLevel();
        eva -= (ms.getLevel() - level) / 2;
        int mobACC = 0;
        if (level >= ms.getLevel() || eva > 0) 
            mobACC = eva;
        double rand = (double) mobACC;
        int acc = Math.max(0, Math.min(ms.getACC() + ms.getStatOption(MobStats.ACC), SkillAccessor.ACC_MAX));
        double b = rand / ((double) acc * 4.5) * 100.0;
        if (JobAccessor.getJobCategory(bs.getJob()) == JobCategory.Thief) {
            b = Math.max(5.0, Math.min(b, 95.0));
        } else {
            b = Math.max(2.0, Math.min(b, 80.0));
        }
        return b > (double) (randForMissCheck % 10000000) * 0.0000100000010000001;
    }
    
    public void decInvalidCount() {
        if (this.invalidCount > 0) {
            --this.invalidCount;
        }
    }
    
    public int getInvalidCount() {
        return this.invalidCount;
    }
    
    public boolean isExceedInvalidCount() {
        return this.invalidCount <= 20 && this.invalidCount > 10;
    }
    
    public void incInvalidCount() {
        ++this.invalidCount;
        if (this.invalidCount > 50) {
            this.invalidCount = 0;
        }
    }
    
    public void MDamage(CharacterData cd, BasicStat bs, SecondaryStat ss, MobStat ms, int damagePerMob, int weaponItemID, int action, SkillEntry skill, byte slv, List<Short> damage, int mobCount) {
        // TODO: Magic Damage Calculations
    }
    
    public void PDamage(CharacterData cd, BasicStat bs, SecondaryStat ss, MobStat ms, int damagePerMob, int weaponItemID, int bulletItemID, int attackType, int action, SkillEntry skill, byte slv, List<Short> damage) {
        int idx = 0;
        long[] random = new long[RND_SIZE];
        for (int i = 0; i < random.length; i++) {
            random[i] = rndGenForCharacter.random();
        }
        
        int skillID = 0;
        if (skill != null)
            skillID = skill.getSkillID();
        int wt = ItemAccessor.getWeaponType(weaponItemID);
        int acc = Math.max(0, Math.min(ss.acc + ss.getStatOption(CharacterTemporaryStat.ACC), SkillAccessor.ACC_MAX));
        int mobPDD = acc;
        int eva = Math.max(0, ms.getLevel() - bs.getLevel());
        double a = (double) mobPDD * 100.0 / ((double) eva * 10.0 + 255.0);
        int pad = ss.pad + ss.getStatOption(CharacterTemporaryStat.PAD);
        if (bulletItemID != 0)
            pad += ItemInfo.getBulletPAD(bulletItemID);
        pad = Math.max(0, Math.min(pad, SkillAccessor.PAD_MAX));
        if (damagePerMob <= 0) {
            return;
        }
        
        for (short dmg : damage) {
            mobPDD = Math.max(0, Math.min(ms.getEVA() + ms.getStatOption(MobStats.EVA), SkillAccessor.EVA_MAX));
            
            double b = a * 1.3;
            double dmgDone = a * 0.7;
            long rand = random[idx++ % RND_SIZE];
            
            if (b != dmgDone) {
                b = getRand(rand, dmgDone, b);
            }
            if (b >= (double) mobPDD) {
                if (((wt != 45 && wt != 46 && wt != 47) || (action >= 22 && action <= 27)) && wt != 32) {
                    switch (wt) {
                        case 45: // Bow
                            break;
                        case 46: // CrossBow
                            break;
                        case 41: // TowHand_Axe
                        case 42: // TowHand_Mace
                            if (action >= 5 && action <= 15) {
                                
                            } else {
                                
                            }
                            break;
                        case 43: // Spear
                        case 44: // PoleArm
                            if ((action >= 5 && action <= 15) == (wt == 43)) {
                                
                            } else {
                                
                            }
                            break;
                        case 40: // TowHand_Sword
                            break;
                        case 31: // OneHand_Axe
                        case 32: // OneHand_Mace
                        case 37: // Wand
                        case 38: // Staff
                            if (action >= 5 && action <= 15) {
                                
                            } else {
                                
                            }
                            break;
                        case 30: // OneHand_Sword
                        case 33: // Dagger
                            if (JobAccessor.getJobCategory(bs.getJob()) == JobCategory.Thief && wt == 33) {
                                
                            } else {
                                
                            }
                            break;
                        case 47: // ThrowingGloves
                            if (skillID != 0 && skillID == Rogue.DoubleStab_Dagger) {
                                
                            } else {
                                
                            }
                            break;
                    }
                } else {
                    
                }
                // Begin Critical Calculations
            }
        }
    }
    
    public void setSeed(int s1, int s2, int s3) {
        // We technically initialize all seeds in Rand32 constructors..
    }
    
    public void skip() {
        for (int i = 0; i < RND_SIZE; i++) {
            rndGenForCharacter.random();
        }
    }
    
    public static final double getRand(long rand, double f0, double f1) {
        double random;
        if (f1 != f0) {
            // swap f1 with f0
            if (f1 > f0) {
                double tmp = f1;
                f0 = f1;
                f1 = tmp;
            }
            long val = rand % 10000000;
            random = f1 + (f0 - f1) * val * 0.000000100000010000001;
        } else {
            random = f0;
        }
        return random;
    }
}
