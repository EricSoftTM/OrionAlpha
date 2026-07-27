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
package common;

import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import game.user.skill.SkillAccessor;
import game.user.stat.BasicStat;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class ExpAccessor {
    public static final int MAX_LEVEL = 200;
    public static final int[] EXP_USER = new int[201];
    
    static {
        // NEXTLEVEL::NEXTLEVEL
        for (int i = 1; i <= 5; i++) {
            EXP_USER[i] = i * (i * i / 2 + 15);
        }
        for (int i = 6; i <= 50; i++) {
            EXP_USER[i] = i * i / 3 * (i * i / 3 + 19);
        }
        for (int i = 51; i <= MAX_LEVEL; i++) {
            EXP_USER[i] = (int) ((float) EXP_USER[i - 1] * 1.0548f);
        }
    }
    
    /**
     * Processes EXP gains and if requirements are met, will level up the user.
     * 
     * @param cd The user's active CharacterData
     * @param bs The user's active BasicStat
     * @param inc The amount of EXP gained
     * @param reachMaxLv After processing the level up, if the user has reached max level
     * @return If the user has physically leveled up.
     */
    public static boolean tryProcessLevelUp(CharacterData cd, BasicStat bs, int inc, Pointer<Boolean> reachMaxLv) {
        long exp = cd.getCharacterStat().getEXP() + inc;
        
        int level = cd.getCharacterStat().getLevel();
        if (level >= MAX_LEVEL) {
            cd.getCharacterStat().setLevel(MAX_LEVEL);
            cd.getCharacterStat().setEXP(0);
            return false;
        }
        
        int max = getNextLevelExp(level);
        if (exp < max) {
            cd.getCharacterStat().setEXP((int) exp);
            return false;
        }
        
        exp -= getNextLevelExp(level); level++;
        cd.getCharacterStat().setLevel(level);
        if (level == MAX_LEVEL) reachMaxLv.set(true);
        max = getNextLevelExp(level);
        cd.getCharacterStat().setEXP((exp >= max) ? Math.max(0, max - 1) : (int) exp);
        
        SkillAccessor.incMaxHPMP(cd, bs, CharacterStatType.MHP | CharacterStatType.MMP, true);
        if (cd.getCharacterStat().getHP() > 0) {
            cd.getCharacterStat().setHP(cd.getCharacterStat().getMHP());
            cd.getCharacterStat().setMP(cd.getCharacterStat().getMMP());
        }
        return true;
    }
    
    /**
     * Used to decrease a player's EXP if they die.
     * This function is only ever called if the user does not have a Safety Charm/etc.
     * 
     * @param c The user's active CharacterData
     * @param bs The user's active BasicStat
     */
    public static void decreaseExp(CharacterData c, BasicStat bs) {
        if (c.getCharacterStat().getJob() != 0) {
            int exp = getNextLevelExp(c.getCharacterStat().getLevel());
            int dec = (int) ((0.2 / (double) bs.getLUK() + 0.05) * (double) exp);
            c.getCharacterStat().setEXP(Math.min(Math.max(0, c.getCharacterStat().getEXP() - dec), exp - 1));
        }
    }
    
    /**
     * 
     * @param level The user's level
     * @return The total EXP required for the next level
     */
    public static int getNextLevelExp(int level) {
        if (level > MAX_LEVEL) {
            return Integer.MAX_VALUE;
        }
        return EXP_USER[Math.max(1, level)];
    }
    
    /**
     * Does the reverse. Instead of giving the EXP, it will find the player's level.
     * Note that this specifically only checks for Explorers.
     * 
     * @param exp The user's EXP
     * @return The user's current level determined from their EXP
     */
    public static int getLevel(final int exp) {
        int level = 0;
        while (exp >= EXP_USER[level]) {
            level++;
            if (level >= MAX_LEVEL) {
                return MAX_LEVEL;
            }
        }
        return level + 1;
    }
}
