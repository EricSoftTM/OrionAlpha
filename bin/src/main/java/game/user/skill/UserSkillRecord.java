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

import common.user.DBChar;
import game.user.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class UserSkillRecord {
    
    public static boolean skillUp(User user, int skillID, boolean decSP, List<SkillRecord> change) {
        if (user.lock()) {
            try {
                if (user.getHP() == 0) {
                    return false;
                }
                int skillRoot = skillID / 10000;
                if (user.getCharacter().getCharacterStat().getSP() <= 0) {
                    return false;
                }
                List<Integer> a = new ArrayList<>();
                SkillAccessor.getSkillRootFromJob(user.getCharacter().getCharacterStat().getJob(), a);
                int i = 0;
                for (;;) {
                    if (a.isEmpty())
                        break;
                    if (i >= a.size() || a.get(i) == skillRoot)
                        break;
                    ++i;
                }
                if (a.isEmpty() || i >= a.size())
                    return false;
                a.clear();
                SkillEntry skillEntry = SkillInfo.getInstance().getSkill(skillID);
                if (skillEntry == null) {
                    return false;
                }
                for (SkillRecord skill : skillEntry.getSkillRequirements()) {
                    if (!user.getCharacter().getSkillRecord().containsKey(skill.getSkillID()))
                        return false;
                    if (user.getCharacter().getSkillRecord().get(skill.getSkillID()) < skill.getInfo())
                        return false;
                }
                int slv = 0;
                if (user.getCharacter().getSkillRecord().containsKey(skillID))
                    slv = user.getCharacter().getSkillRecord().get(skillID);
                if (slv >= skillEntry.getLevelData().length) {
                    return false;
                }
                if ((skillRoot % 100) != 0) {
                    if (!canSkillUp(user, skillEntry)) {
                        return false;
                    }
                }
                if (decSP && !user.incSP(-1, true))
                    return false;
                ++slv;
                
                user.getCharacter().getSkillRecord().put(skillID, slv);
                change.add(new SkillRecord(skillID, slv));
                user.addCharacterDataMod(DBChar.SkillRecord);
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    private static boolean canSkillUp(User user, SkillEntry skill) {
        // TODO: Calculate skill information to validate if they can raise SLV.
        
        return true;
    }
}
