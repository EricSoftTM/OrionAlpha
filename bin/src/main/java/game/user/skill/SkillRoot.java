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
 * @author Arnah
 */
public class SkillRoot {

    private int skillRootID;
    private String bookName;
    private final List<SkillEntry> skills = new ArrayList<>();

    public int getSkillRootID() {
        return skillRootID;
    }

    public void setSkillRootID(int skillRootID) {
        this.skillRootID = skillRootID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public List<SkillEntry> getSkills() {
        return skills;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + skillRootID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SkillRoot))
            return false;
        SkillRoot other = (SkillRoot) obj;
        if (skillRootID != other.skillRootID)
            return false;
        return true;
    }

}
