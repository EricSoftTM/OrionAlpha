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

import common.JobAccessor;
import common.JobCategory;
import common.Request;
import common.item.BodyPart;
import common.item.ItemAccessor;
import common.item.ItemType;
import common.user.CharacterStat.CharacterStatType;
import common.user.UserEffect;
import game.party.PartyData;
import game.party.PartyMan;
import game.user.User;
import game.user.WvsContext;
import game.user.skill.Skills.*;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.SecondaryStatOption;
import java.util.ArrayList;
import java.util.List;
import network.packet.InPacket;
import util.Logger;
import util.Pointer;
import util.Rand32;

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
        if (!checkMovementSkill(skillID, slv)) {
            return;
        }
        Pointer<SkillEntry> skillEntry = new Pointer<>();
        if (slv <= 0 || SkillInfo.getInstance().getSkillLevel(user.getCharacter(), skillID, skillEntry) < slv
                || !SkillInfo.getInstance().adjustConsumeForActiveSkill(user, skillID, slv)) {
            sendFailPacket();
            return;
        }
        SkillEntry skill = skillEntry.get();
        if (SkillAccessor.isMobStatChange(skillID)) {
            doActiveSkill_MobStatChange(skill, slv, packet, true);
            return;
        } else if (SkillAccessor.isPartyStatChange(skillID)) {
            doActiveSkill_PartyStatChange(skill, slv, packet);
            return;
        } else if (SkillAccessor.isSelfStatChange(skillID)) {
            doActiveSkill_SelfStatChange(skill, slv, packet);
            return;
        } else if (SkillAccessor.isWeaponBooster(skillID)) {
            doActiveSkill_WeaponBooster(skill, slv, 1, 1);
            return;
        }
        switch (skillID) {
            case Wizard2.Teleport:
            case Wizard1.Teleport:
            case Cleric.Teleport:
                doActiveSkill_Teleport(skill, slv);
                break;
            default: {
                Logger.logReport("Found new skill: %d", skillID);
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
                    user.sendCharacterStat(Request.None, CharacterStatType.SP);
                }
                UserSkillRecord.sendCharacterSkillRecord(user, Request.Excl, change);
                change.clear();
            } finally {
                user.unlock();
            }
        }
    }
    
    public void doActiveSkill_PartyStatChange(SkillEntry skill, byte slv, InPacket packet) {
        int affectedMemberBitmap = packet.decodeByte(true);
        long duration = System.currentTimeMillis() + 1000 * skill.getLevelData(slv).getTime();
        int partyID = PartyMan.getInstance().getPartyID(user.getCharacterID());
        if (partyID == 0) {
            if (affectedMemberBitmap != 128) {
                Logger.logError("Invalid Party Skill Request");
                sendFailPacket();
                return;
            }
        }
        
        int partyCount = 0;
        List<Integer> members = new ArrayList<>();
        if (partyID != 0) {
            PartyData pd = PartyMan.getInstance().getParty(partyID);
            if (pd != null) {
                for (int i = 6; i > 0; i--) {
                    int characterID = pd.getParty().getCharacterID().get(i - 1);
                    if ((affectedMemberBitmap & 1) != 0) {
                        User member = user.getChannel().findUser(characterID);
                        if (member != null && member.getField() != null && member.getField() == user.getField() && member.getHP() != 0 && member != user) {
                            members.add(partyCount++, characterID);
                        }
                    }
                    affectedMemberBitmap >>= 1;
                }
            }
            if (partyCount >= 6) {
                Logger.logError("Invalid Party Skill Request ( Invalid Party Member Count )");
                sendFailPacket();
                return;
            }
        }
        members.add(partyCount++, user.getCharacterID());
        
        int hpRate = 0;
        if (skill.getLevelData(slv).getHP() != 0) {
            int baseInt = user.getBasicStat().getINT();
            int rand = 0;
            if ((baseInt - (baseInt * 0.8d)) > 0) {
                rand = (int) (Rand32.getInstance().random() % (baseInt - (baseInt * 0.8d)));
                rand += (int) (baseInt - (baseInt * 0.8d));
            }
            int rate = user.getSecondaryStat().mad + user.getSecondaryStat().getStatOption(CharacterTemporaryStat.MAD);
            rate = (int) (((double) rand * 1.5d + (double) user.getBasicStat().getLUK()) * (double) rate * 0.01d);
            hpRate = (int) ((double) rate * ((double) partyCount * 0.3d + 1.0d) * (double) skill.getLevelData(slv).getHP() * 0.01d);
        }
        
        if (partyCount > 0) {
            int healPartyBonus = 0;
            for (int characterID : members) {
                User member = user.getChannel().findUser(characterID);
                if (member != null) {
                    if (member.lock()) {
                        try {
                            int skillFlag = member.getUserSkill().processSkill(skill, slv, duration);
                            int statFlag = 0;
                            if (skill.getLevelData(slv).getHP() != 0) {
                                int hp = member.getHP();
                                double inc = Math.ceil((double) (hpRate / partyCount));
                                if (member.incHP((int) (long) inc, false)) {
                                    statFlag |= CharacterStatType.HP;
                                    if (skill.getSkillID() == Cleric.Heal) {
                                        if (member == user) {
                                            if (healPartyBonus != 0) {
                                                statFlag |= user.incEXP(healPartyBonus, false);
                                                user.sendPacket(WvsContext.onIncEXPMessage(true, healPartyBonus));
                                            }
                                        } else {
                                            healPartyBonus += 20 * (member.getHP() - hp) / (8 * member.getLevel() + 190);
                                        }
                                    }
                                }
                            }
                            member.validateStat(false);
                            member.sendCharacterStat(member == user ? Request.Excl : Request.None, statFlag);
                            member.sendTemporaryStatSet(skillFlag);
                            if (member != user) {
                                member.onUserEffect(true, true, UserEffect.SkillAffected, skill.getSkillID(), slv);
                            }
                        } finally {
                            member.unlock();
                        }
                    }
                }
            }
            if (user.getField() != null) {
                user.onUserEffect(false, true, UserEffect.SkillUse, skill.getSkillID(), slv);
            }
        }
    }
    
    public void doActiveSkill_SelfStatChange(SkillEntry skill, byte slv, InPacket packet) {
        int time = 1000 * skill.getLevelData(slv).getTime();
        long cur = System.currentTimeMillis();
        long duration = Math.max(cur + time, 1);
        
        int skillFlag = processSkill(skill, slv, duration);
        if (skill.getSkillID() == Rogue.DarkSight) {
            SecondaryStatOption opt = user.getSecondaryStat().getStat(CharacterTemporaryStat.DarkSight);
            if (opt != null) {
                // tCur = HIDWORD(tCur);
                // Convert the time into timeGetTime() seconds
                cur /= 1000;
                cur = Math.max(1, cur - 3000);
                opt.setModOption((int) cur);
                user.getSecondaryStat().setStat(CharacterTemporaryStat.DarkSight, opt);
            }
        }
        
        int statFlag = 0;
        int hp = skill.getLevelData(slv).getHP();
        if (hp > 0) {
            user.incHP(hp, false);
            statFlag |= CharacterStatType.HP;
        }
        int mp = skill.getLevelData(slv).getMP();
        if (mp > 0) {
            user.incMP(mp, false);
            statFlag |= CharacterStatType.MP;
        }
        
        user.validateStat(true);
        user.sendCharacterStat(Request.Excl, statFlag);
        user.sendTemporaryStatSet(skillFlag);
        if (user.getField() != null) {
            user.onUserEffect(false, true, UserEffect.SkillUse, skill.getSkillID(), slv);
        }
    }
    
    public void doActiveSkill_Teleport(SkillEntry skill, byte slv) {
        user.sendCharacterStat(Request.Excl, 0);
    }
    
    public void doActiveSkill_MobStatChange(SkillEntry skill, byte slv, InPacket packet, boolean sendResult) {
        int count = packet.decodeByte();
        for (int i = 0; i < count; i++) {
            int mobID = packet.decodeInt();
            
            user.getField().getLifePool().onMobStatChangeSkill(user, mobID, skill, slv);
        }
        if (sendResult) {
            user.onUserEffect(false, true, UserEffect.SkillUse, skill.getSkillID(), slv);
        }
        user.sendCharacterStat(Request.Excl, 0);
    }
    
    public void doActiveSkill_WeaponBooster(SkillEntry skill, byte slv, int wt1, int wt2) {
        SkillLevelData levelData = skill.getLevelData(slv);
        int wt = ItemAccessor.getWeaponType(user.getCharacter().getItem(ItemType.Equip, -BodyPart.Weapon).getItemID());
        if (wt > 0 && SkillAccessor.isCorrectItemForBooster(wt, user.getCharacter().getCharacterStat().getJob()) && levelData.getTime() > 0) {
            long duration = System.currentTimeMillis() + 1000 * levelData.getTime();
            user.getSecondaryStat().setStat(CharacterTemporaryStat.Booster, new SecondaryStatOption(1, skill.getSkillID(), duration));
            user.sendCharacterStat(Request.Excl, 0);
            user.sendTemporaryStatSet(CharacterTemporaryStat.Booster);
            if (user.getField() != null) {
                user.onUserEffect(false, true, UserEffect.SkillUse, skill.getSkillID(), slv);
            }
        } else {
            user.sendCharacterStat(Request.Excl, 0);
        }
    }
    
    public boolean checkMovementSkill(int skillID, byte slv) {
        if (user.isGM()) {
            return true;
        }
        String character = user.getCharacterName();
        int fieldID = user.getField().getFieldID();
        short job = user.getCharacter().getCharacterStat().getJob();
        String format = new String();
        if (SkillAccessor.isTeleportSkill(skillID)) {
            if (JobAccessor.getJobCategory(job) == JobCategory.Wizard) {
                int skillLevel = SkillAccessor.getTeleportSkillLevel(user.getCharacter());
                if (slv > skillLevel + 3) {
                    format = String.format("[SkillHack] Illegal Teleport LEVEL Tried [ %s ] Field: %d / SkillID: %d / (CurLev: %d, ReqLev: %d) (DISCONNECTED)", character, fieldID, skillID, skillLevel, slv);
                }
            } else {
                format = String.format("[SkillHack] Illegal Teleport Tried [ %s ] Field: %d / SkillID: %d (DISCONNECTED)", character, fieldID, skillID);
            }
        }
        if (!format.isEmpty()) {
            Logger.logError(format);
            
            user.closeSocket();
            return false;
        }
        return true;
    }
    
    public void sendFailPacket() {
        user.sendPacket(WvsContext.onSkillUseResult(Request.None));
    }
    
    private int processSkill(SkillEntry skill, byte slv, long duration) {
        int flag = 0;
        
        SkillLevelData level = skill.getLevelData(slv);
        if (level.getPAD() != 0) {
            flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.PAD, new SecondaryStatOption(level.getPAD(), skill.getSkillID(), duration));
        }
        if (level.getPDD() != 0) {
            flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.PDD, new SecondaryStatOption(level.getPDD(), skill.getSkillID(), duration));
        }
        if (level.getMAD() != 0) {
            flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.MAD, new SecondaryStatOption(level.getMAD(), skill.getSkillID(), duration));
        }
        if (level.getMDD() != 0) {
            flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.MDD, new SecondaryStatOption(level.getMDD(), skill.getSkillID(), duration));
        }
        if (level.getACC() != 0) {
            flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.ACC, new SecondaryStatOption(level.getACC(), skill.getSkillID(), duration));
        }
        if (level.getEVA() != 0) {
            flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.EVA, new SecondaryStatOption(level.getEVA(), skill.getSkillID(), duration));
        }
        if (level.getSpeed() != 0) {
            flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.Speed, new SecondaryStatOption(level.getSpeed(), skill.getSkillID(), duration));
        }
        if (level.getJump() != 0) {
            flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.Jump, new SecondaryStatOption(level.getJump(), skill.getSkillID(), duration));
        }
        
        switch (skill.getSkillID()) {
            // WARRIOR
            case Fighter.PowerGuard:
            case Page.PowerGuard:
                flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.PowerGuard, new SecondaryStatOption(level.getX(), skill.getSkillID(), duration));
                break;
            case Spearman.HyperBody:
                flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.MaxHP, new SecondaryStatOption(level.getX(), skill.getSkillID(), duration));
                flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.MaxMP, new SecondaryStatOption(level.getY(), skill.getSkillID(), duration));
                break;
            // MAGICIAN
            case Magician.MagicGuard:
                flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.MagicGuard, new SecondaryStatOption(level.getX(), skill.getSkillID(), duration));
                break;
            case Cleric.Invincible:
                flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.Invincible, new SecondaryStatOption(level.getX(), skill.getSkillID(), duration));
                break;
            // BOWMAN
            case Hunter.SoulArrow_Bow:
            case Crossbowman.SoulArrow_Crossbow:
                flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.SoulArrow, new SecondaryStatOption(level.getX(), skill.getSkillID(), duration));
                break;
            // THIEF
            case Rogue.DarkSight:
                flag |= user.getSecondaryStat().setStat(CharacterTemporaryStat.DarkSight, new SecondaryStatOption(level.getX(), skill.getSkillID(), duration));
                break;
        }
        
        return flag;
    }
}
