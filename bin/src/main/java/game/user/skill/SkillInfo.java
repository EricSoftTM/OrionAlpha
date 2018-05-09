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

import common.item.ItemAccessor;
import common.user.CharacterData;
import game.user.User;
import game.user.item.ItemInfo;
import game.user.skill.Skills.Assassin;
import java.util.HashMap;
import java.util.Map;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class SkillInfo {
    private static final SkillInfo instance = new SkillInfo();
    private final Map<Integer, SkillEntry> skills;
    
    public SkillInfo() {
        this.skills = new HashMap<>();
    }
    
    public static SkillInfo getInstance() {
        return instance;
    }
    
    public boolean adjustConsumeForActiveSkill(User user, int skillID, byte slv) {
        if (slv <= 0)
            return false;
        if ((skillID / 1000 % 10) == 0) {
            return true;
        }
        
        return false;
    }
    
    public int getBundleItemMaxPerSlot(int itemID, CharacterData cd) {
        int maxPerSlot = 0;
        /*BundleItem info = ItemInfo.GetBundleItem(itemID);
        if (info != null) {
            maxPerSlot = info.nMaxPerSlot;
            if (cd != null && ItemAccessor.isRechargeableItem(itemID)) {
                Pointer<SkillEntry> ppSkill = new Pointer<>();
                byte slv = getSkillLevel(cd, Assassin.JavelinMastery, ppSkill);
                if (slv > 0)
                    maxPerSlot += ppSkill.get().getLevelData(slv).nY;
            }
        }*/
        return maxPerSlot;
    }
}
