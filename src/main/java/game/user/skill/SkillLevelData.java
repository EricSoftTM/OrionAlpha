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

import java.awt.Point;

/**
 * @author Arnah
 */
public class SkillLevelData {

    private int hp, mp;
    private int hpCon, mpCon;
    private int pad, pdd, mad, mdd;
    private int acc, eva;
    private int speed, jump;
    private int attackCount;
    private int bulletCount;
    private int damage;
    private int mastery;
    private int time;
    private int range;
    private int prop;
    private int x, y;
    private Point lt, rt;

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

    public int getHPCon() {
        return hpCon;
    }

    public void setHPCon(int hpCon) {
        this.hpCon = hpCon;
    }

    public int getMPCon() {
        return mpCon;
    }

    public void setMPCon(int mpCon) {
        this.mpCon = mpCon;
    }

    public int getPAD() {
        return pad;
    }

    public void setPAD(int pad) {
        this.pad = pad;
    }

    public int getPDD() {
        return pdd;
    }

    public void setPDD(int pdd) {
        this.pdd = pdd;
    }

    public int getMAD() {
        return mad;
    }

    public void setMAD(int mad) {
        this.mad = mad;
    }

    public int getMDD() {
        return mdd;
    }

    public void setMDD(int mdd) {
        this.mdd = mdd;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getJump() {
        return jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public void setAttackCount(int attackCount) {
        this.attackCount = attackCount;
    }

    public int getBulletCount() {
        return bulletCount;
    }

    public void setBulletCount(int bulletCount) {
        this.bulletCount = bulletCount;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getMastery() {
        return mastery;
    }

    public void setMastery(int mastery) {
        this.mastery = mastery;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getProp() {
        return prop;
    }

    public void setProp(int prop) {
        this.prop = prop;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point getLT() {
        return lt;
    }

    public void setLT(Point lt) {
        this.lt = lt;
    }

    public Point getRT() {
        return rt;
    }

    public void setRT(Point rt) {
        this.rt = rt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + acc;
        result = prime * result + attackCount;
        result = prime * result + bulletCount;
        result = prime * result + damage;
        result = prime * result + eva;
        result = prime * result + hp;
        result = prime * result + hpCon;
        result = prime * result + jump;
        result = prime * result + ((lt == null) ? 0 : lt.hashCode());
        result = prime * result + mad;
        result = prime * result + mastery;
        result = prime * result + mdd;
        result = prime * result + mp;
        result = prime * result + mpCon;
        result = prime * result + pad;
        result = prime * result + pdd;
        result = prime * result + prop;
        result = prime * result + range;
        result = prime * result + ((rt == null) ? 0 : rt.hashCode());
        result = prime * result + speed;
        result = prime * result + time;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SkillLevelData))
            return false;
        SkillLevelData other = (SkillLevelData) obj;
        if (acc != other.acc)
            return false;
        if (attackCount != other.attackCount)
            return false;
        if (bulletCount != other.bulletCount)
            return false;
        if (damage != other.damage)
            return false;
        if (eva != other.eva)
            return false;
        if (hp != other.hp)
            return false;
        if (hpCon != other.hpCon)
            return false;
        if (jump != other.jump)
            return false;
        if (lt == null) {
            if (other.lt != null)
                return false;
        } else if (!lt.equals(other.lt))
            return false;
        if (mad != other.mad)
            return false;
        if (mastery != other.mastery)
            return false;
        if (mdd != other.mdd)
            return false;
        if (mp != other.mp)
            return false;
        if (mpCon != other.mpCon)
            return false;
        if (pad != other.pad)
            return false;
        if (pdd != other.pdd)
            return false;
        if (prop != other.prop)
            return false;
        if (range != other.range)
            return false;
        if (rt == null) {
            if (other.rt != null)
                return false;
        } else if (!rt.equals(other.rt))
            return false;
        if (speed != other.speed)
            return false;
        if (time != other.time)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

}
