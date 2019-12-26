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
package game.field.life.mob;

import game.field.life.MoveAbility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class MobStat {
    /* The constant Stat Option held as an empty holder for non-buffed stats */
    static final MobStatOption EMPTY_OPTION;
    /* Constant defined sets for comparing the symmetrical difference of two sets */
    public static final int MovementAffecting;
    
    private final Map<Integer, MobStatOption> stats;
    private final List<Integer> damagedElemAttr;
    private byte level;
    private short pad;
    private short pdd;
    private short mad;
    private short mdd;
    private short acc;
    private short eva;
    private byte speed;
    
    public MobStat() {
        this.stats = new LinkedHashMap<>();
        this.damagedElemAttr = new ArrayList<>();
    }
    
    public void adjustDamagedElemAttr(int skillID) {
        this.damagedElemAttr.set(AttackElem.Light, AttackElemAttr.None);
        this.damagedElemAttr.set(AttackElem.Poison, AttackElemAttr.None);
        this.damagedElemAttr.set(AttackElem.Holy, AttackElemAttr.None);
    }
    
    public void clear() {
        this.pad = 0;
        this.pdd = 0;
        this.mad = 0;
        this.mdd = 0;
        this.acc = 0;
        this.eva = 0;
        this.speed = 0;
        this.stats.clear();
    }
    
    public void encodeTemporary(OutPacket packet, int flag) {
        packet.encodeInt(flag);
        for (int cts : stats.keySet()) {
            if ((cts & flag) != 0) {
                MobStatOption opt = getStat(cts);
                packet.encodeShort(opt.getOption());
                packet.encodeInt(opt.getReason());
                packet.encodeShort((int) ((opt.getDuration() - System.currentTimeMillis()) / 500));
            }
        }
    }
    
    public short getACC() {
        return acc;
    }
    
    public short getEVA() {
        return eva;
    }
    
    public byte getLevel() {
        return level;
    }
    
    public short getMAD() {
        return mad;
    }
    
    public short getMDD() {
        return mdd;
    }
    
    public short getPAD() {
        return pad;
    }
    
    public short getPDD() {
        return pdd;
    }
    
    public byte getSpeed() {
        return speed;
    }
    
    public MobStatOption getStat(int cts) {
        if (stats.containsKey(cts)) {
            return stats.get(cts);
        }
        return EMPTY_OPTION;
    }
    
    public long getStatDuration(int cts) {
        return getStat(cts).getDuration();
    }
    
    public int getStatOption(int cts) {
        return getStat(cts).getOption();
    }
    
    public int getStatReason(int cts) {
        return getStat(cts).getReason();
    }
    
    public static boolean isMovementAffectingStat(int flag) {
        return (flag & MovementAffecting) != 0;
    }
    
    public int reset() {
        int reset = 0;
        for (int stat : stats.keySet()) {
            reset |= stat;
        }
        stats.clear();
        return reset;
    }
    
    public int reset(int reset) {
        for (Iterator<Integer> it = stats.keySet().iterator(); it.hasNext();) {
            int stat = it.next();
            if ((stat & reset) != 0) {
                it.remove();
            }
        }
        return reset;
    }
    
    public void resetDamagedElemAttr(List<Integer> originalDamagedElemAttr) {
        damagedElemAttr.clear();
        damagedElemAttr.addAll(originalDamagedElemAttr);
    }
    
    public int resetTemporary(long time) {
        int reset = 0;
        for (Iterator<Map.Entry<Integer, MobStatOption>> it = stats.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Integer, MobStatOption> stat = it.next();
            if (stat.getValue().getOption() != 0 && time - stat.getValue().getDuration() > 0) {
                reset |= stat.getKey();
                it.remove();
            }
        }
        return reset;
    }
    
    public void setACC(int acc) {
        this.acc = (short) acc;
    }
    
    public void setEVA(int eva) {
        this.eva = (short) eva;
    }
    
    public void setFrom(MobTemplate template) {
        this.level = template.getLevel();
        this.damagedElemAttr.clear();
        this.damagedElemAttr.addAll(template.getDamagedElemAttr());
        this.pad = template.getPAD();
        this.pdd = template.getPDD();
        this.mad = template.getMAD();
        this.mdd = template.getMDD();
        this.acc = template.getACC();
        this.eva = template.getEVA();
        this.speed = template.getSpeed();
        if (template.getMoveAbility() == MoveAbility.Fly) {
            //this.speed = template.getFlySpeed();
        }
    }
    
    public void setLevel(int level) {
        this.level = (byte) level;
    }
    
    public void setMAD(int mad) {
        this.mad = (short) mad;
    }
    
    public void setMDD(int mdd) {
        this.mdd = (short) mdd;
    }
    
    public void setPAD(int pad) {
        this.pad = (short) pad;
    }
    
    public void setPDD(int pdd) {
        this.pdd = (short) pdd;
    }
    
    public void setSpeed(int speed) {
        this.speed = (byte) speed;
    }
    
    public int setStat(int cts, MobStatOption opt) {
        stats.put(cts, opt);
        return cts;
    }
    
    public void setStatOption(int cts, int option) {
        if (stats.containsKey(cts)) {
            MobStatOption opt = stats.get(cts);
            opt.setOption(option);
        }
    }
    
    static {
        EMPTY_OPTION = new MobStatOption();
        
        MovementAffecting = MobStats.Speed | MobStats.Stun;
    }
}
