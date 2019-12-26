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

import common.Request;
import java.util.HashMap;
import java.util.Map;

import common.item.ItemAccessor;
import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import game.field.life.mob.AttackElem;
import game.user.User;
import game.user.item.BundleItem;
import game.user.item.ItemInfo;
import game.user.skill.Skills.Assassin;
import java.util.Collection;
import util.Pointer;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 * @author Arnah
 */
public class SkillInfo {

    private static final SkillInfo instance = new SkillInfo();
    private final Map<Integer, SkillEntry> skills;
    private final Map<Integer, SkillRoot> skillRoots;

    public SkillInfo() {
        this.skills = new HashMap<>();
        this.skillRoots = new HashMap<>();
        iterateSkillInfo();
    }

    public static SkillInfo getInstance() {
        return instance;
    }

    public boolean adjustConsumeForActiveSkill(User user, int skillID, byte slv) {
        if (slv <= 0) {
            return false;
        }
        if ((skillID / 1000 % 10) == 0) {
            return true;
        }
        Pointer<SkillEntry> curSkillEntry = new Pointer<>();
        if (getSkillLevel(user.getCharacter(), skillID, curSkillEntry) < slv) {
            return false;
        }
        SkillEntry skillEntry = curSkillEntry.get();
        SkillLevelData level = skillEntry.getLevelData(slv);
        int hp = level.getHPCon();
        int mp = level.getMPCon();
        if (user.lock()) {
            try {
                int flag = 0;
                if (hp > 0) {
                    flag = CharacterStatType.HP;
                }
                if (mp > 0) {
                    flag |= CharacterStatType.MP;
                }
                if (user.getHP() == 0 || hp > 0 && hp >= user.getHP() || mp > 0 && mp > user.getCharacter().getCharacterStat().getMP()) {
                    if (flag != 0)
                        user.sendCharacterStat(Request.None, flag);
                    return false;
                }
                if (hp > 0) {
                    user.incHP(-hp, true);
                }
                if (mp > 0) {
                    user.incMP(-mp, true);
                }
                if (flag != 0) {
                    user.sendCharacterStat(Request.None, flag);
                }
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
    
    public Collection<SkillEntry> getAllSkills() {
        return skills.values();
    }

    public int getBundleItemMaxPerSlot(int itemID, CharacterData cd) {
        int maxPerSlot = 0;
        BundleItem info = ItemInfo.getBundleItem(itemID);
        if (info != null) {
            maxPerSlot = info.getSlotMax();
            if (cd != null && ItemAccessor.isRechargeableItem(itemID)) {
                Pointer<SkillEntry> skill = new Pointer<>();
                int slv = getSkillLevel(cd, Assassin.JavelinMastery, skill);
                if (slv > 0) {
                    maxPerSlot += skill.get().getLevelData(slv).getY();
                }
            }
        }
        return maxPerSlot;
    }

    public int getSkillLevel(CharacterData cd, int skillID) {
        Integer level = cd.getSkillRecord().get(skillID);
        if (level == null) {
            return 0;
        }
        return level;
    }
    
    public int getSkillLevel(CharacterData cd, int skillID, Pointer<SkillEntry> skillEntry) {
        SkillEntry skill = null;
        if (skillEntry != null)
            skill = skillEntry.get();
        if (skill == null)
            skill = skills.get(skillID);
        if (skillEntry != null && skillEntry.get() == null)
            skillEntry.set(skill);
        if (cd != null && skill != null) {
            if (cd.getSkillRecord().containsKey(skillID)) {
                return Math.min(cd.getSkillRecord().get(skillID), skill.getLevelData().length);
            }
        }
        return 0;
    }

    public SkillRoot getSkillRoot(int skillRootID) {
        return skillRoots.get(skillRootID);
    }

    public SkillEntry getSkill(int skillID) {
        return skills.get(skillID);
    }

    private void iterateSkillInfo() {
        WzPackage skillDir = new WzFileSystem().init("Skill").getPackage();
        if (skillDir != null) {
            for (WzProperty root : skillDir.getEntries().values()) {
                loadSkillRoot(root);
            }
            skillDir.release();
        }
        skillDir = null;
    }

    private void loadSkillRoot(WzProperty root) {
        int skillRootID = Integer.parseInt(root.getNodeName().replace(".img", ""));
        SkillRoot skillRoot = new SkillRoot();
        skillRoot.setSkillRootID(skillRootID);
        WzProperty info = root.getNode("info");
        if (info != null) {
            skillRoot.setBookName(WzUtil.getString(info.getNode("bookName"), "NULL"));
        }

        WzProperty skillNode = root.getNode("skill");
        if (skillNode != null) {
            for (WzProperty skillData : skillNode.getChildNodes()) {
                loadSkill(skillRoot, skillData);
            }
        }

        skillRoots.put(skillRootID, skillRoot);
    }


    private void loadSkill(SkillRoot skillRoot, WzProperty skillData) {
        SkillEntry entry = new SkillEntry();
        entry.setSkillID(Integer.parseInt(skillData.getNodeName().replace(".img", "")));
        entry.setName(WzUtil.getString(skillData.getNode("name"), "NULL"));
        entry.setSkillType(WzUtil.getInt32(skillData.getNode("skillType"), 0));
        entry.setWeapon(WzUtil.getInt32(skillData.getNode("weapon"), 0));
        String elemAttr = WzUtil.getString(skillData.getNode("elemAttr"), "P");
        entry.setElemAttr(AttackElem.getElementAttribute(elemAttr.charAt(0)));
        WzProperty req = skillData.getNode("req");
        if (req != null) {
            loadReqSkill(entry, req);
        }

        WzProperty level = skillData.getNode("level");
        if (level != null) {
            entry.setLevelData(level.getChildNodes().size());
            loadLevelData(entry, level);
        }

        skills.put(entry.getSkillID(), entry);
        skillRoot.getSkills().add(entry);
    }

    private void loadReqSkill(SkillEntry entry, WzProperty req) {
        for (WzProperty skill : req.getChildNodes()) {
            int skillid = Integer.parseInt(skill.getNodeName());
            int level = WzUtil.getInt32(skill, 0);
            if (level <= 0)
                continue;
            entry.getSkillRequirements().add(new SkillRecord(skillid, level));
        }
    }

    private void loadLevelData(SkillEntry entry, WzProperty levelProp) {
        for (WzProperty level : levelProp.getChildNodes()) {
            int skillLevel = Integer.parseInt(level.getNodeName());
            SkillLevelData levelData = new SkillLevelData();

            levelData.setHP(WzUtil.getInt32(level.getNode("hp"), 0));
            levelData.setMP(WzUtil.getInt32(level.getNode("mp"), 0));
            levelData.setHPCon(WzUtil.getInt32(level.getNode("hpCon"), 0));
            levelData.setMPCon(WzUtil.getInt32(level.getNode("mpCon"), 0));

            levelData.setPAD(WzUtil.getInt32(level.getNode("pad"), 0));
            levelData.setPDD(WzUtil.getInt32(level.getNode("pdd"), 0));
            levelData.setMAD(WzUtil.getInt32(level.getNode("mad"), 0));
            levelData.setMDD(WzUtil.getInt32(level.getNode("mdd"), 0));

            levelData.setACC(WzUtil.getInt32(level.getNode("acc"), 0));
            levelData.setEVA(WzUtil.getInt32(level.getNode("eva"), 0));
            levelData.setSpeed(WzUtil.getInt32(level.getNode("speed"), 0));
            levelData.setJump(WzUtil.getInt32(level.getNode("jump"), 0));

            levelData.setAttackCount(WzUtil.getInt32(level.getNode("attackCount"), 0));
            levelData.setBulletCount(WzUtil.getInt32(level.getNode("bulletCount"), 0));
            levelData.setDamage(WzUtil.getInt32(level.getNode("damage"), 0));
            levelData.setMastery(WzUtil.getInt32(level.getNode("mastery"), 0));
            levelData.setTime(WzUtil.getInt32(level.getNode("time"), 0));
            levelData.setRange(WzUtil.getInt32(level.getNode("range"), 0));
            levelData.setProp(WzUtil.getInt32(level.getNode("prop"), 0));

            levelData.setX(WzUtil.getInt32(level.getNode("x"), 0));
            levelData.setY(WzUtil.getInt32(level.getNode("y"), 0));

            levelData.setLT(WzUtil.getPoint(level.getNode("lt"), null));
            levelData.setRT(WzUtil.getPoint(level.getNode("rt"), null));

            entry.setLevelData(skillLevel, levelData);
        }
    }
}
