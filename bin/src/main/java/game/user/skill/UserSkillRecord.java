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
import util.Logger;

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
                /*if (!SkillInfo.getInstance().isSkillVisible(user.getCharacter(), skillID, null)) {
                    Logger.logError("[SkillHack] %s trying to increase invisible skill level", user.getCharacterName());
                    return false;
                }
                int skillRoot = skillID / 10000;
                if (!JobConstants.IsBeginnerJob(skillRoot)) {
                    if (user.getCharacter().getCharacterStat().getSP() <= 0) {
                        return false;
                    }
                    List<Integer> a = new ArrayList<>();
                    SkillConstants.get_skill_root_from_job(user.character.characterStat.nJob, a);
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
                } else {
                    if (SkillConstants.get_novice_skill_point(user.character) <= 0)
                        return false;
                }
                SkillEntry skillEntry = SkillInfo.GetInstance().GetSkill(skillID);
                if (skillEntry == null) {
                    return false;
                }
                for (SkillRecord skill : skillEntry.lReqSkill) {
                    if (!user.getCharacter().skillRecord.containsKey(skill.getSkillID()))
                        return false;
                    if (user.getCharacter().skillRecord.get(skill.getSkillID()) < skill.getInfo())
                        return false;
                }
                int nSLV = 0;
                if (user.getCharacter().skillRecord.containsKey(skillID))
                    nSLV = user.getCharacter().skillRecord.get(skillID);
                if (nSLV >= skillEntry.aLevelData.size()) {
                    return false;
                }
                if ((skillRoot % 100) != 0 && !JobConstants.IsBeginnerJob(user.character.characterStat.nJob) && !JobConstants.IsExtendSPJob(user.character.characterStat.nJob)) {
                    if (JobConstants.IsDualJob(user.character.characterStat.nJob)) {
                        if (!CanSkillUpDualJob(user, skillEntry)) {
                            return false;
                        }
                    } else {
                        if (!CanSkillUp(user, skillEntry)) {
                            return false;
                        }
                    }
                }
                int nMasterLevel = 0;
                if (SkillConstants.IsSkillNeedMasterLevel(skillID)) {
                    nMasterLevel = user.character.mSkillMasterLev.get(skillID);
                    if (nMasterLevel < nSLV + 1)
                        return false;
                }
                if (JobConstants.IsBeginnerJob(skillRoot))
                    decSP = false;
                if (decSP && !user.IncSP(-1, true))
                    return false;
                ++nSLV;*/
                
                int nSLV = user.getCharacter().getSkillRecord().getOrDefault(skillID, 0) + 1;
                user.getCharacter().getSkillRecord().put(skillID, nSLV);
                change.add(new SkillRecord(skillID, nSLV));
                user.addCharacterDataMod(DBChar.SkillRecord);
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
}
