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
package game.user.item;

import common.user.CharacterData;
import common.user.CharacterStat.CharacterStatType;
import game.user.User;
import game.user.stat.BasicStat;
import game.user.stat.CharacterTemporaryStat;
import game.user.stat.SecondaryStat;
import game.user.stat.SecondaryStatOption;

/**
 *
 * @author Eric
 */
public class StateChangeInfo {
    private int flagTemp;
    private int flagRate, flag;
    private int hp, mp;
    private int acc, eva, mad, pdd, pad;
    private int speed;
    private int time;
    
    public int apply(User user, int itemID, CharacterData cd, BasicStat bs, SecondaryStat ss, long cur, boolean applyBetterOnly) {
        final double amountBonusRate = 1.0d; //Alchemist doesn't exist yet :P
        final double timeBonusRate = 1.0d;
        
        int tempSet = 0;
        
        if ((flag & CharacterStatType.HP) != 0) {
            double hpInc = (double) hp * amountBonusRate;
            if ((flagRate & CharacterStatType.HP) != 0) {
                hpInc = (double) (hp * bs.getMHP() / 100);
            }
            int inc = Math.min(Math.max(0, (int) ((double) cd.getCharacterStat().getHP() + hpInc)), bs.getMHP());
            cd.getCharacterStat().setHP(inc);
        }
        if ((flag & CharacterStatType.MP) != 0) {
            double mpInc = (double) mp * amountBonusRate;
            if ((flagRate & CharacterStatType.MP) != 0) {
                mpInc = (double) (mp * bs.getMMP() / 100);
            }
            int inc = Math.min(Math.max(0, (int) ((double) cd.getCharacterStat().getMP() + mpInc)), bs.getMMP());
            cd.getCharacterStat().setMP(inc);
        }
        
        long duration = (long) ((double) time * timeBonusRate + (double) cur);
        if ((flagTemp & CharacterTemporaryStat.PAD) != 0) {
            int padOption = ss.getStatOption(CharacterTemporaryStat.PAD);
            if (!applyBetterOnly || (pad <= 0) || (padOption <= pad) && (padOption != pad || ss.getStatDuration(CharacterTemporaryStat.PAD) <= duration)) {
                tempSet |= ss.setStat(CharacterTemporaryStat.PAD, new SecondaryStatOption(pad, -itemID, duration));
            }
        }
        if ((flagTemp & CharacterTemporaryStat.PDD) != 0) {
            int padOption = ss.getStatOption(CharacterTemporaryStat.PDD);
            if (!applyBetterOnly || (pdd <= 0) || (padOption <= pdd) && (padOption != pdd || ss.getStatDuration(CharacterTemporaryStat.PDD) <= duration)) {
                tempSet |= ss.setStat(CharacterTemporaryStat.PDD, new SecondaryStatOption(pdd, -itemID, duration));
            }
        }
        if ((flagTemp & CharacterTemporaryStat.MAD) != 0) {
            int padOption = ss.getStatOption(CharacterTemporaryStat.MAD);
            if (!applyBetterOnly || (mad <= 0) || (padOption <= mad) && (padOption != mad || ss.getStatDuration(CharacterTemporaryStat.MAD) <= duration)) {
                tempSet |= ss.setStat(CharacterTemporaryStat.MAD, new SecondaryStatOption(mad, -itemID, duration));
            }
        }
        if ((flagTemp & CharacterTemporaryStat.ACC) != 0) {
            int padOption = ss.getStatOption(CharacterTemporaryStat.ACC);
            if (!applyBetterOnly || (acc <= 0) || (padOption <= acc) && (padOption != acc || ss.getStatDuration(CharacterTemporaryStat.ACC) <= duration)) {
                tempSet |= ss.setStat(CharacterTemporaryStat.ACC, new SecondaryStatOption(acc, -itemID, duration));
            }
        }
        if ((flagTemp & CharacterTemporaryStat.EVA) != 0) {
            int padOption = ss.getStatOption(CharacterTemporaryStat.EVA);
            if (!applyBetterOnly || (eva <= 0) || (padOption <= eva) && (padOption != eva || ss.getStatDuration(CharacterTemporaryStat.EVA) <= duration)) {
                tempSet |= ss.setStat(CharacterTemporaryStat.EVA, new SecondaryStatOption(eva, -itemID, duration));
            }
        }
        if ((flagTemp & CharacterTemporaryStat.Speed) != 0) {
            int padOption = ss.getStatOption(CharacterTemporaryStat.Speed);
            if (!applyBetterOnly || (speed <= 0) || (padOption <= speed) && (padOption != speed || ss.getStatDuration(CharacterTemporaryStat.Speed) <= duration)) {
                tempSet |= ss.setStat(CharacterTemporaryStat.Speed, new SecondaryStatOption(speed, -itemID, duration));
            }
        }
        
        user.validateStat(true);
        return tempSet;
    }
    
    public int getFlagTemp() {
        return flagTemp;
    }
    
    public void addFlagTemp(int mask) {
        this.flagTemp |= mask;
    }
    
    public int getFlagRate() {
        return flagRate;
    }
    
    public void addFlagRate(int mask) {
        this.flagRate |= mask;
    }
    
    public void setFlagRate(int flagRate) {
        this.flagRate = flagRate;
    }
    
    public int getFlag() {
        return flag;
    }
    
    public void addFlag(int flag) {
        this.flag |= flag;
    }
    
    public void setFlag(int flag) {
        this.flag = flag;
    }
    
    public int getHP() {
        return hp;
    }

    public void setHP(int hp) {
        this.hp = hp;
    }

    public int getMP() {
        return mp;
    }

    public void setMP(int mp) {
        this.mp = mp;
    }

    public int getACC() {
        return acc;
    }

    public void setACC(int acc) {
        this.acc = acc;
    }

    public int getEVA() {
        return eva;
    }

    public void setEVA(int eva) {
        this.eva = eva;
    }

    public int getMAD() {
        return mad;
    }

    public void setMAD(int mad) {
        this.mad = mad;
    }

    public int getPDD() {
        return pdd;
    }

    public void setPDD(int pdd) {
        this.pdd = pdd;
    }

    public int getPAD() {
        return pad;
    }

    public void setPAD(int pad) {
        this.pad = pad;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
