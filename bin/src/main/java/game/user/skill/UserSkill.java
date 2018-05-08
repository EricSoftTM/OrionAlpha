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

import common.user.CharacterStat;
import game.user.User;
import game.user.WvsContext;
import game.user.WvsContext.Request;
import game.user.skill.Skills.Cleric;
import game.user.skill.Skills.Wizard1;
import game.user.skill.Skills.Wizard2;
import java.util.ArrayList;
import java.util.List;
import network.packet.InPacket;

/**
 *
 * @author Eric
 */
public class UserSkill {
    private final User user;
    
    public UserSkill(User user) {
        this.user = user;
    }
    
    public void onSkillUseRequest(InPacket packet) {
        int skillID = packet.decodeInt();
        byte slv = packet.decodeByte();
        if (user.getField() == null) {
            sendFailPacket();
            return;
        }
        if (user.getField().lock(1000)) {
            try {
                if (user.lock()) {
                    try {
                        if (slv <= 0) {//TODO: SkillInfo checks here..
                            sendFailPacket();
                            return;
                        }
                        SkillEntry skill = null;
                        if (SkillAccessor.isPartyStatChange(skillID)) {
                            DoActiveSkill_PartyStatChange(skill, slv, packet);
                            return;
                        } else if (SkillAccessor.isSelfStatChange(skillID)) {
                            DoActiveSkill_SelfStatChange(skill, slv, packet);
                            return;
                        } else if (SkillAccessor.isWeaponBooster(skillID)) {
                            DoActiveSkill_WeaponBooster(skill, slv, 1, 1);
                            return;
                        }
                        switch (skillID) {
                            case Wizard2.Teleport:
                            case Wizard1.Teleport:
                            case Cleric.Teleport:
                                DoActiveSkill_Teleport(skill, slv);
                                break;
                        }
                    } finally {
                        user.unlock();
                    }
                }
            } finally {
                user.getField().unlock();
            }
        }
    }
    
    public void onSkillUpRequest(InPacket packet) {
        if (user.lock()) {
            try {
                int skillID = packet.decodeInt();
                List<SkillRecord> change = new ArrayList<>();
                if (UserSkillRecord.skillUp(user, skillID, true, change)) {
                    user.validateStat(false);
                    user.sendCharacterStat(WvsContext.Request.None, CharacterStat.CharacterStatType.SP);
                }
                user.sendPacket(WvsContext.onChangeSkillRecordResult(WvsContext.Request.Excl, change));
                change.clear();
            } finally {
                user.unlock();
            }
        }
    }
    
    public void DoActiveSkill_PartyStatChange(SkillEntry skill, byte slv, InPacket packet) {
        int affectedMemberBitmap = packet.decodeByte(true);
    }
    
    public void DoActiveSkill_SelfStatChange(SkillEntry skill, byte slv, InPacket packet) {
        
    }
    
    public void DoActiveSkill_Teleport(SkillEntry skill, byte slv) {
        user.sendCharacterStat(Request.Excl, 0);
    }
    
    public void DoActiveSkill_WeaponBooster(SkillEntry skill, byte slv, int wt1, int wt2) {
        
    }
    
    public void sendFailPacket() {
        user.sendPacket(WvsContext.onSkillUseResult(Request.None));
    }
}
