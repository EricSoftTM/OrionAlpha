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

import common.item.ItemSlotBase;
import common.item.ItemSlotEquip;
import common.user.CharacterData;
import game.user.skill.SkillAccessor;
import java.util.List;

/**
 *
 * @author Eric
 */
public class BasicStat {
    private byte gender;
    private byte level;
    private short job;
    private short STR, DEX, INT, LUK;
    private short pop;
    private short mhp, mmp;
    
    public void clear() {
        this.job = 0;
        this.level = 0;
        this.gender = 0;
        this.LUK = 0;
        this.INT = 0;
        this.DEX = 0;
        this.STR = 0;
        this.mmp = 0;
        this.mhp = 0;
        this.pop = 0;
    }
    
    public byte getGender() {
        return gender;
    }

    public byte getLevel() {
        return level;
    }

    public short getJob() {
        return job;
    }

    public short getSTR() {
        return STR;
    }

    public short getDEX() {
        return DEX;
    }

    public short getINT() {
        return INT;
    }

    public short getLUK() {
        return LUK;
    }

    public short getPOP() {
        return pop;
    }

    public short getMHP() {
        return mhp;
    }

    public short getMMP() {
        return mmp;
    }
    
    public void setFrom(CharacterData c, List<ItemSlotBase> realEquip, List<ItemSlotBase> realEquip2, int maxHPIncRate, int maxMPIncRate) {
        this.gender = c.getCharacterStat().getGender();
        this.level = c.getCharacterStat().getLevel();
        this.job = c.getCharacterStat().getJob();
        this.STR = c.getCharacterStat().getSTR();
        this.INT = c.getCharacterStat().getINT();
        this.DEX = c.getCharacterStat().getDEX();
        this.LUK = c.getCharacterStat().getLUK();
        this.pop = c.getCharacterStat().getPOP();
        this.mhp = c.getCharacterStat().getMHP();
        this.mmp = c.getCharacterStat().getMMP();
        
        for (ItemSlotBase equip : realEquip) {
            if (equip != null) {
                ItemSlotEquip item = (ItemSlotEquip) equip;
                this.STR += item.iSTR;
                this.INT += item.iINT;
                this.DEX += item.iDEX;
                this.LUK += item.iLUK;
                this.mhp += item.iMaxHP;
                this.mmp += item.iMaxMP;
            }
        }
        for (ItemSlotBase equip : realEquip2) {
            if (equip != null) {
                ItemSlotEquip item = (ItemSlotEquip) equip;
                this.STR += item.iSTR;
                this.INT += item.iINT;
                this.DEX += item.iDEX;
                this.LUK += item.iLUK;
                this.mhp += item.iMaxHP;
                this.mmp += item.iMaxMP;
            }
        }
        
        this.mhp += maxHPIncRate;
        this.mmp += maxMPIncRate;
        this.mhp = (short) Math.min(this.mhp, SkillAccessor.HP_MAX);
        this.mmp = (short) Math.min(this.mmp, SkillAccessor.MP_MAX);
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public void setJob(short job) {
        this.job = job;
    }

    public void setSTR(short STR) {
        this.STR = STR;
    }

    public void setDEX(short DEX) {
        this.DEX = DEX;
    }

    public void setINT(short INT) {
        this.INT = INT;
    }

    public void setLUK(short LUK) {
        this.LUK = LUK;
    }

    public void setPOP(short pop) {
        this.pop = pop;
    }

    public void setMHP(short mhp) {
        this.mhp = mhp;
    }

    public void setMMP(short mmp) {
        this.mmp = mmp;
    }
}
