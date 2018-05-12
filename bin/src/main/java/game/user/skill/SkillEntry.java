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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 * @author Arnah
 */
public class SkillEntry {

    private int skillID;
    private String name;
    private int skillType;
    private List<SkillRecord> skillRequirements = new ArrayList<>();
    private int weapon;
    private int elemAttr;
    private SkillLevelData[] levelData;

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
