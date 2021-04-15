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

import game.field.life.mob.AttackElemAttr;
import game.user.skill.Skills.Hunter;
import game.user.skill.Skills.Crossbowman;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 * @author Arnah
 */
public class SkillEntry {
    private static final double[][] aadRate = new double[AttackElemAttr.Count][15];//Element Attributes (5) * Maximum Damage Per Mob (15)
    
    static {
        aadRate[AttackElemAttr.None]      = new double[] { 0.666667, 0.222222, 0.074074, 0.024691, 0.008229999999999999, 0.002743, 0.000914, 0.000305, 0.000102, 0.000033, 0.000011, 0.000004, 0.000001, 0.0, 0.0 };
        aadRate[AttackElemAttr.Damage0]   = new double[] { 1.0, 0.9, 0.8100000000000001, 0.729, 0.6561, 0.59049, 0.5314410000000001, 0.478296, 0.430467, 0.38741, 0.348678, 0.31381, 0.282429, 0.254186, 0.228767 };
        aadRate[AttackElemAttr.Damage50]  = new double[] { 1.0, 0.7, 0.49, 0.343, 0.2401, 0.16807, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        aadRate[AttackElemAttr.Damage150] = new double[] { 1.0, 1.2, 1.44, 1.728, 2.0736, 2.48832, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        aadRate[AttackElemAttr.Damage200] = new double[] { 1.0, 1.0, 1.0, 0.95, 0.95, 0.95, 0.9, 0.9, 0.9, 0.85, 0.85, 0.85, 0.8, 0.8, 0.8 };
    }
    
    private int skillID;
    private String name;
    private int skillType;
    private final List<SkillRecord> skillRequirements;
    private int weapon;
    private int elemAttr;
    private SkillLevelData[] levelData;
    
    public SkillEntry() {
        this.skillRequirements = new ArrayList<>();
        this.levelData = null;
    }
    
    public boolean adjustDamageDecRate(byte slv, int order, List<Short> damage, boolean finalAfterSlashBlast) {
        boolean adjust = false;
        
        double rate;
        switch (this.skillID) {
            case Hunter.ArrowBomb:
                rate = (double)getLevelData(slv).getX() / 100.0;
                if (order == 0 && damage.get(0) == 0) {
                    adjust = true;
                }
                break;
            case Crossbowman.IronArrow:
                rate = aadRate[AttackElemAttr.Damage0][order];
                break;
            default: {
                if (!finalAfterSlashBlast) {
                    return false;
                }
                rate = aadRate[AttackElemAttr.None][order];
            }
        }
    
        for (int i = 0; i < 15; i++) {
            damage.set(i, (short)(int)((double)damage.get(i) * rate));
        }
        return adjust;
    }

    public int getSkillID() {
        return skillID;
    }

    public void setSkillID(int skillID) {
        this.skillID = skillID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSkillType() {
        return skillType;
    }

    public void setSkillType(int skillType) {
        this.skillType = skillType;
    }

    public List<SkillRecord> getSkillRequirements() {
        return skillRequirements;
    }

    public int getWeapon() {
        return weapon;
    }

    public void setWeapon(int weapon) {
        this.weapon = weapon;
    }

    public int getElemAttr() {
        return elemAttr;
    }

    public void setElemAttr(int elemAttr) {
        this.elemAttr = elemAttr;
    }

    public void setLevelData(int maxLevel) {
        levelData = new SkillLevelData[maxLevel];
    }

    public SkillLevelData[] getLevelData() {
        return levelData;
    }

    public SkillLevelData getLevelData(int level) {
        return levelData[level - 1];
    }

    public void setLevelData(int level, SkillLevelData levelData) {
        this.levelData[level - 1] = levelData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + skillID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SkillEntry))
            return false;
        SkillEntry other = (SkillEntry) obj;
        if (skillID != other.skillID)
            return false;
        return true;
    }

}
