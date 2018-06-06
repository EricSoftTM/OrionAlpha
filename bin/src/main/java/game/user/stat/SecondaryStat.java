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
package game.user.stat;

import common.JobAccessor;
import common.JobCategory;
import common.item.BodyPart;
import common.item.ItemSlotBase;
import common.item.ItemSlotEquip;
import common.user.CharacterData;
import game.user.skill.SkillAccessor;
import game.user.skill.SkillEntry;
import game.user.skill.SkillInfo;
import game.user.skill.Skills;
import game.user.skill.Skills.Archer;
import game.user.skill.Skills.Rogue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import network.packet.OutPacket;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class SecondaryStat {
    /* The constant Stat Option held as an empty holder for non-buffed stats */
    static final SecondaryStatOption EMPTY_OPTION;
    /* Constant defined sets for comparing the symmetrical difference of two sets */
    public static final int 
            MovementAffecting,
            FilterForRemote
    ;
    
    private final Map<Integer, SecondaryStatOption> stats;
    public int pad;
    public int pdd;
    public int mad;
    public int mdd;
    public int acc;
    public int eva;
    public int craft;
    public int speed;
    public int jump;
    
    public SecondaryStat() {
        this.stats = new LinkedHashMap<>();
    }
    
    public void clear() {
        this.pad = 0;
        this.pdd = 0;
        this.mad = 0;
        this.mdd = 0;
        this.acc = 0;
        this.eva = 0;
        this.craft = 0;
        this.speed = 0;
        this.jump = 0;
        this.stats.clear();
    }
    
    public void encodeForLocal(OutPacket packet, int flag) {
        packet.encodeInt(flag);
        for (int cts : stats.keySet()) {
            if ((cts & flag) != 0) {
                SecondaryStatOption opt = getStat(cts);
                packet.encodeShort(opt.getOption());
                packet.encodeInt(opt.getReason());
            }
        }
    }
    
    public void encodeForRemote(OutPacket packet, int flag) {
        int toSend = 0;
        List<Integer> data = new ArrayList<>();
        
        if ((flag & CharacterTemporaryStat.PAD) != 0 && getStatOption(CharacterTemporaryStat.PAD) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.PAD));
        }
        if ((flag & CharacterTemporaryStat.PDD) != 0  && getStatOption(CharacterTemporaryStat.PDD) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.PDD));
        }
        if ((flag & CharacterTemporaryStat.MAD) != 0  && getStatOption(CharacterTemporaryStat.MAD) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.MAD));
        }
        if ((flag & CharacterTemporaryStat.MDD) != 0  && getStatOption(CharacterTemporaryStat.MDD) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.MDD));
        }
        if ((flag & CharacterTemporaryStat.ACC) != 0  && getStatOption(CharacterTemporaryStat.ACC) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.ACC));
        }
        if ((flag & CharacterTemporaryStat.EVA) != 0  && getStatOption(CharacterTemporaryStat.EVA) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.EVA));
        }
        if ((flag & CharacterTemporaryStat.Craft) != 0  && getStatOption(CharacterTemporaryStat.Craft) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.Craft));
        }
        if ((flag & CharacterTemporaryStat.Speed) != 0  && getStatOption(CharacterTemporaryStat.Speed) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.Speed));
        }
        if ((flag & CharacterTemporaryStat.Jump) != 0  && getStatOption(CharacterTemporaryStat.Jump) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.Jump));
        }
        if ((flag & CharacterTemporaryStat.MagicGuard) != 0  && getStatOption(CharacterTemporaryStat.MagicGuard) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.MagicGuard));
        }
        if ((flag & CharacterTemporaryStat.DarkSight) != 0  && getStatOption(CharacterTemporaryStat.DarkSight) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.DarkSight));
        }
        if ((flag & CharacterTemporaryStat.Booster) != 0  && getStatOption(CharacterTemporaryStat.Booster) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.Booster));
        }
        if ((flag & CharacterTemporaryStat.Unknown) != 0  && getStatOption(CharacterTemporaryStat.Unknown) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.Unknown));
        }
        if ((flag & CharacterTemporaryStat.PowerGuard) != 0  && getStatOption(CharacterTemporaryStat.PowerGuard) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.PowerGuard));
        }
        if ((flag & CharacterTemporaryStat.MaxHP) != 0  && getStatOption(CharacterTemporaryStat.MaxHP) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.MaxHP));
        }
        if ((flag & CharacterTemporaryStat.MaxMP) != 0  && getStatOption(CharacterTemporaryStat.MaxMP) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.MaxMP));
        }
        if ((flag & CharacterTemporaryStat.Invincible) != 0  && getStatOption(CharacterTemporaryStat.Invincible) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.Invincible));
        }
        if ((flag & CharacterTemporaryStat.SoulArrow) != 0  && getStatOption(CharacterTemporaryStat.SoulArrow) != 0) {
            data.add(getStatReason(CharacterTemporaryStat.SoulArrow));
        }
        
        packet.encodeInt(toSend);
        for (int val : data) {
            packet.encodeInt(val);
        }
    }
    
    public static boolean filterForRemote(int flag) {
        return (flag & FilterForRemote) != 0;
    }
    
    public SecondaryStatOption getStat(int cts) {
        if (stats.containsKey(cts)) {
            return stats.get(cts);
        }
        return EMPTY_OPTION;
    }
    
    public long getStatDuration(int cts) {
        return getStat(cts).getDuration();
    }
    
    public short getStatOption(int cts) {
        return getStat(cts).getOption();
    }
    
    public int getStatReason(int cts) {
        return getStat(cts).getReason();
    }
    
    public static boolean isMovementAffectingStat(int flag) {
        return (flag & MovementAffecting) != 0;
    }
    
    public boolean isSetted(int reason) {
        for (SecondaryStatOption opt : stats.values()) {
            if (opt.getReason() == reason) {
                return true;
            }
        }
        return false;
    }
    
    public int reset() {
        int reset = 0;
        for (int cts : stats.keySet()) {
            reset |= cts;
        }
        stats.clear();
        return reset;
    }
    
    public int resetByCTS(int cts) {
        int reset = 0;
        if (stats.containsKey(cts)) {
            reset |= cts;
            stats.remove(cts);
        }
        return reset;
    }
    
    public int resetByReasonID(int reasonID) {
        int reset = 0;
        for (Iterator<Map.Entry<Integer, SecondaryStatOption>> it = stats.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, SecondaryStatOption> stat = it.next();
            if (stat.getValue().getReason() == reasonID) {
                reset |= stat.getKey();
                it.remove();
            }
        }
        return reset;
    }
    
    public int resetByTime(long time) {
        int reset = 0;
        for (Iterator<Map.Entry<Integer, SecondaryStatOption>> it = stats.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, SecondaryStatOption> stat = it.next();
            if (stat.getValue().getOption() != 0 && time - stat.getValue().getDuration() > 0) {
                reset |= stat.getKey();
                it.remove();
            }
        }
        return reset;
    }
    
    public void setFrom(BasicStat bs, List<ItemSlotBase> realEquip, List<ItemSlotBase> realEquip2, CharacterData cd) {
        short job = bs.getJob();
        int jc = JobAccessor.getJobCategory(job);
        int slv;
        this.pad = 0;
        this.pdd = 0;
        this.mad = bs.getINT();
        this.mdd = bs.getINT();
        this.eva = (bs.getDEX() / 4 + bs.getLUK() / 2);
        if (jc == JobCategory.Archer || jc == JobCategory.Thief) {
            this.acc = (short) (bs.getDEX() * 0.6 + bs.getLUK() * 0.3);
        } else {
            this.acc = (short) (bs.getDEX() * 0.8 + bs.getLUK() * 0.5);
        }
        this.craft = bs.getLUK() + bs.getDEX() + bs.getINT();
        this.speed = 100;
        this.jump = 100;
        
        final int BodyPartCount = BodyPart.BP_Count;
        for (int pos = 1; pos <= BodyPartCount; pos++) {
            ItemSlotEquip item = (ItemSlotEquip) realEquip.get(pos);
            if (item != null) {
                this.pad += item.iPAD;
                this.pdd += item.iPDD;
                this.mad += item.iMAD;
                this.mdd += item.iMDD;
                this.acc += item.iACC;
                this.eva += item.iEVA;
                this.craft += item.iCraft;
                this.speed += item.iSpeed;
                this.jump += item.iJump;
            }
        }
        
        for (int pos = 1; pos <= BodyPartCount; pos++) {
            ItemSlotEquip item = (ItemSlotEquip) realEquip.get(pos);
            if (item != null) {
                this.pad += item.iPAD;
                this.pdd += item.iPDD;
                this.mad += item.iMAD;
                this.mdd += item.iMDD;
                this.acc += item.iACC;
                this.eva += item.iEVA;
                this.craft += item.iCraft;
                this.speed += item.iSpeed;
                this.jump += item.iJump;
            }
        }
        
        SkillEntry archerAmazonBlessing = SkillInfo.getInstance().getSkill(Archer.AmazonBlessing);
        slv = SkillInfo.getInstance().getSkillLevel(cd, Archer.AmazonBlessing, new Pointer<>(archerAmazonBlessing));
        if (slv != 0) {
            this.acc += archerAmazonBlessing.getLevelData(slv).getX();
        }
        SkillEntry thiefNimbleBody = SkillInfo.getInstance().getSkill(Rogue.NimbleBody);
        slv = SkillInfo.getInstance().getSkillLevel(cd, Rogue.NimbleBody, new Pointer<>(thiefNimbleBody));
        if (slv != 0) {
            this.acc += thiefNimbleBody.getLevelData(slv).getX();
            this.eva += thiefNimbleBody.getLevelData(slv).getY();
        }
        
        int attackType = 1;//MELEE
        if (jc == JobCategory.Archer || job / 10 == 41)
            attackType = 2;//SHOOT
        int weaponItemID = 0;
        ItemSlotBase item = realEquip.get(BodyPart.Weapon);
        if (item != null)
            weaponItemID = item.getItemID();
        
        Pointer<Integer> padInc = new Pointer<>(0);
        Pointer<Integer> accInc = new Pointer<>(0);
        if (SkillAccessor.getWeaponMastery(cd, weaponItemID, attackType, accInc, padInc) != 0) {
            this.acc += accInc.get();
            this.pad += padInc.get();
        }
        
        this.pad = Math.max(Math.min(this.pad, SkillAccessor.PAD_MAX), 0);
        this.pdd = Math.max(Math.min(this.pdd, SkillAccessor.PDD_MAX), 0);
        this.mad = Math.max(Math.min(this.mad, SkillAccessor.MAD_MAX), 0);
        this.mdd = Math.max(Math.min(this.mdd, SkillAccessor.MDD_MAX), 0);
        this.acc = Math.max(Math.min(this.acc, SkillAccessor.ACC_MAX), 0);
        this.eva = Math.max(Math.min(this.eva, SkillAccessor.EVA_MAX), 0);
        this.craft = Math.max(Math.min(this.craft, SkillAccessor.CRAFT_MAX), 0);
        this.speed = Math.max(Math.min(this.speed, SkillAccessor.SPEED_MAX), 100);
        this.jump = Math.max(Math.min(this.jump, SkillAccessor.JUMP_MAX), 100);
    }
    
    public int setStat(int cts, SecondaryStatOption opt) {
        stats.put(cts, opt);
        return cts;
    }
    
    public void setStatOption(int cts, int option) {
        if (stats.containsKey(cts)) {
            SecondaryStatOption opt = stats.get(cts);
            opt.setOption(option);
        }
    }
    
    static {
        EMPTY_OPTION = new SecondaryStatOption();
        
        MovementAffecting = CharacterTemporaryStat.Speed | CharacterTemporaryStat.Jump;
        FilterForRemote = CharacterTemporaryStat.Speed | CharacterTemporaryStat.DarkSight | CharacterTemporaryStat.SoulArrow;
    }
}
