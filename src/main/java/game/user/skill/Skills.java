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

/**
 * All MapleStory Skills
 * 
 * @author Eric
 */
public class Skills {
    
    /**
     * Warrior
     */
    public static class Warrior extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Warrior">
        public static final int
                ImproveBasic = 1000000,
                MHPInc = 1000001,
                Endure = 1000002,
                IronBody = 1001003,
                PowerStrike = 1001004,
                SlashBlast = 1001005
        ;
        // </editor-fold>
    }

    /**
     * Fighter
     */
    public static class Fighter extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Fighter">
        public static final int
                WeaponMastery = 1100000,
                WeaponMasteryEx = 1100001,
                FinalAttack = 1100002,
                FinalAttackEx = 1100003,
                WeaponBooster = 1101004,
                WeaponBoosterEx = 1101005,
                Fury = 1101006,
                PowerGuard = 1101007,
                GroundSmash = 1100008
        ;
        // </editor-fold>
    }
        
    /**
     * Page
     */
    public static class Page extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Page">
        public static final int
                WeaponMastery = 1200000,
                WeaponMasteryEx = 1200001,
                FinalAttack = 1200002,
                FinalAttackEx = 1200003,
                WeaponBooster = 1201004,
                WeaponBoosterEx = 1201005,
                Threaten = 1201006,
                PowerGuard = 1201007,
                GroundSmash = 1201008
        ;
        // </editor-fold>
    }
    
    /**
     * Spearman
     */
    public static class Spearman extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Spearman">
        public static final int
                WeaponMastery = 1300000,
                WeaponMasteryEx = 1300001,
                FinalAttack = 1300002,
                FinalAttackEx = 1300003,
                ImproveBasic = 1300009,
                WeaponBooster = 1301004,
                WeaponBoosterEx = 1301005,
                IronWall = 1301006,
                HyperBody = 1301007,
                GroundSmash = 1301008
        ;
        // </editor-fold>
    }
    
    /**
     * Magician
     */
    public static class Magician extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Magician">
        public static final int
                ImproveBasic = 2000000,
                MMPInc = 2000001,
                MagicGuard = 2001002,
                MagicArmor = 2001003,
                EnergyBolt = 2001004,
                MagicClaw = 2001005
        ;
        // </editor-fold>
    }
    
    /**
     * Fire Poison Wizard
     */
    public static class Wizard1 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Wizard1">
        public static final int
                MPEater = 2100000,
                SpellMastery = 2100006,
                Meditation = 2101001,
                Teleport = 2101002,
                Slow = 2101003,
                FireArrow = 2101004,
                PoisonBreath = 2101005
        ;
        // </editor-fold>
    }
    
    /**
     * Ice Lightning Wizard
     */
    public static class Wizard2 extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Wizard2">
        public static final int
                MPEater = 2200000,
                SpellMastery = 2200006,
                Meditation = 2201001,
                Teleport = 2201002,
                Slow = 2201003,
                ColdBeam = 2201004,
                ThunderBolt = 2201005
        ;
        // </editor-fold>
    }
    
    /**
     * Cleric
     */
    public static class Cleric extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Cleric">
        public static final int
                MPEater = 2300000,
                SpellMastery = 2300006,
                Teleport = 2301001,
                Heal = 2301002,
                Invincible = 2301003,
                Bless = 2301004,
                HolyArrow = 2301004
        ;
        // </editor-fold>
    }
    
    /**
     * Archer/Bowman
     */
    public static class Archer extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Archer">
        public static final int
                AmazonBlessing = 3000000,
                CriticalShot = 3000001,
                AmazonEye = 3000002,
                Focus = 3001003,
                ArrowBlow = 3001004,
                DoubleShot = 3001005
        ;
        // </editor-fold>
    }
    
    /**
     * Hunter
     */
    public static class Hunter extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Hunter">
        public static final int
                BowMastery = 3100000,
                FinalAttack_Bow = 3100001,
                BowBooster = 3101002,
                PowerKnockback = 3101003,
                SoulArrow_Bow = 3101004,
                ArrowBomb = 3101005,
                ImproveBasic = 3101006
        ;
        // </editor-fold>
    }
    
    /**
     * Crossbowman
     */
    public static class Crossbowman extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Crossbowman">
        public static final int
                CrossbowMastery = 3200000,
                FinalAttack_Crossbow = 3200001,
                ImproveBasic = 3200006,
                CrossbowBooster = 3201002,
                PowerKnockback = 3201003,
                SoulArrow_Crossbow = 3201004,
                IronArrow = 3201005
        ;
        // </editor-fold>
    }
    
    /**
     * Rogue
     */
    public static class Rogue extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Rogue">
        public static final int
                NimbleBody = 4000000,
                KeenEyes = 4000001,
                Disorder = 4001002,
                DarkSight = 4001003,
                DoubleStab_Dagger = 4001334,
                LuckySeven = 4001344
        ;
        // </editor-fold>
    }
    
    /**
     * Assassin
     */
    public static class Assassin extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Assassin">
        public static final int
                JavelinMastery = 4100000,
                CriticalThrow = 4100001,
                Endure = 4100002,
                ShadowResistance = 4100006,
                JavelinBooster = 4101003,
                Haste = 4101004,
                Drain = 4101005
        ;
        // </editor-fold>
    }
    
    /**
     * Bandit
     */
    public static class Thief extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Thief">
        public static final int
                DaggerMastery = 4200000,
                Endure = 4200001,
                ShadowResistance = 4200006,
                DaggerBooster = 4201002,
                Haste = 4201003,
                Steal = 4201004,
                SavageBlow_Dagger = 4201005
        ;
        // </editor-fold>
    }
    
    /**
     * Eric Class
     */
    public static class EricClass extends Skills {
        // <editor-fold defaultstate="collapsed" desc="Eric Class">
        public static final int
                Retreat = 40001001,
                Jump = 40001002,
                Camouflage = 40001003,
                EricBlessing = 40001004
        ;
        // </editor-fold>
    }
}
