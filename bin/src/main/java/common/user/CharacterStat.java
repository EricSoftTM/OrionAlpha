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
package common.user;

import java.util.concurrent.atomic.AtomicInteger;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class CharacterStat {
    private int characterID;
    private String name;
    private byte gender;
    private int face;
    private int hair;
    private byte skin;
    private byte level;
    private short job;
    private short STR;
    private short DEX;
    private short INT;
    private short LUK;
    private short hp;
    private short mhp;
    private short mp;
    private short mmp;
    private short ap;
    private short sp;
    private final AtomicInteger exp;
    private short pop;
    private final AtomicInteger money;
    private int posMap;
    private byte portal;
    
    public CharacterStat() {
        this.exp = new AtomicInteger(0);
        this.money = new AtomicInteger(0);
    }
    
    public void encode(OutPacket packet) {
        packet.encodeInt(characterID);
        packet.encodeString(name, 13);
        packet.encodeByte(gender);
        packet.encodeInt(face);
        packet.encodeInt(hair);
        packet.encodeByte(skin);
        packet.encodeInt(0); //Unknown
        packet.encodeByte(level);
        packet.encodeShort(job);
        packet.encodeShort(STR);
        packet.encodeShort(DEX);
        packet.encodeShort(INT);
        packet.encodeShort(LUK);
        packet.encodeShort(hp);
        packet.encodeShort(mhp);
        packet.encodeShort(mp);
        packet.encodeShort(mmp);
        packet.encodeShort(ap);
        packet.encodeShort(sp);
        packet.encodeInt(exp.get());
        packet.encodeShort(pop);
        packet.encodeInt(money.get());
        packet.encodeInt(posMap);
        packet.encodeByte(portal);
    }
    
    public int getCharacterID() {
        return characterID;
    }

    public String getName() {
        return name;
    }

    public byte getGender() {
        return gender;
    }

    public int getFace() {
        return face;
    }

    public int getHair() {
        return hair;
    }

    public byte getSkin() {
        return skin;
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

    public short getHP() {
        return hp;
    }

    public short getMHP() {
        return mhp;
    }

    public short getMP() {
        return mp;
    }

    public short getMMP() {
        return mmp;
    }

    public short getAP() {
        return ap;
    }

    public short getSP() {
        return sp;
    }

    public int getEXP() {
        return exp.get();
    }

    public short getPOP() {
        return pop;
    }

    public int getMoney() {
        return money.get();
    }

    public int getPosMap() {
        return posMap;
    }

    public byte getPortal() {
        return portal;
    }
    
    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public void setSkin(byte skin) {
        this.skin = skin;
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

    public void setHP(short hp) {
        this.hp = hp;
    }

    public void setMHP(short mhp) {
        this.mhp = mhp;
    }

    public void setMP(short mp) {
        this.mp = mp;
    }

    public void setMMP(short mmp) {
        this.mmp = mmp;
    }

    public void setAP(short ap) {
        this.ap = ap;
    }

    public void setSP(short sp) {
        this.sp = sp;
    }

    public void setPOP(short pop) {
        this.pop = pop;
    }

    public void setPosMap(int posMap) {
        this.posMap = posMap;
    }

    public void setPortal(byte portal) {
        this.portal = portal;
    }
    
    public void setEXP(int exp) {
        this.exp.set(exp);
    }
    
    public void setMoney(int money) {
        this.money.set(money);
    }
}
