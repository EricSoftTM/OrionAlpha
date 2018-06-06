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
import game.user.User;
import game.user.stat.BasicStat;
import game.user.stat.SecondaryStat;

/**
 * @author Arnah
*/
public class StateChangeItem {

    private int itemID;
    private int hp, mp;
    private int hpR, mpR;
    private int acc, eva, mad, pdd, pad;
    private int speed;
    private int time;
    
    public int apply(User user, int itemID, CharacterData cd, BasicStat bs, SecondaryStat ss, long time, boolean applyBetterOnly) {
        int tempSet = 0;
        
        // TODO
        
        return tempSet;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getHpR() {
        return hpR;
    }

    public void setHpR(int hpR) {
        this.hpR = hpR;
    }

    public int getMpR() {
        return mpR;
    }

    public void setMpR(int mpR) {
        this.mpR = mpR;
    }

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public int getEva() {
        return eva;
    }

    public void setEva(int eva) {
        this.eva = eva;
    }

    public int getMad() {
        return mad;
    }

    public void setMad(int mad) {
        this.mad = mad;
    }

    public int getPdd() {
        return pdd;
    }

    public void setPdd(int pdd) {
        this.pdd = pdd;
    }

    public int getPad() {
        return pad;
    }

    public void setPad(int pad) {
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
