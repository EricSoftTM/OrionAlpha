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

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private long petLockerSN;
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
        packet.encodeLong(petLockerSN);
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
    
    public void encodeChangeStat(OutPacket packet, int flag) {
        packet.encodeInt(flag);
        if ((flag & CharacterStatType.Face) > 0) {
            packet.encodeInt(this.face);
        }
        if ((flag & CharacterStatType.Hair) > 0) {
            packet.encodeInt(this.hair);
        }
        if ((flag & CharacterStatType.PetSN) > 0) {
            packet.encodeLong(this.petLockerSN);
        }
        if ((flag & CharacterStatType.LEV) > 0) {
            packet.encodeByte(this.level);
        }
        if ((flag & CharacterStatType.Job) > 0) {
            packet.encodeShort(this.job);
        }
        if ((flag & CharacterStatType.STR) > 0) {
            packet.encodeShort(this.STR);
        }
        if ((flag & CharacterStatType.DEX) > 0) {
            packet.encodeShort(this.DEX);
        }
        if ((flag & CharacterStatType.INT) > 0) {
            packet.encodeShort(this.INT);
        }
        if ((flag & CharacterStatType.LUK) > 0) {
            packet.encodeShort(this.LUK);
        }
        if ((flag & CharacterStatType.HP) > 0) {
            packet.encodeShort(this.hp);
        }
        if ((flag & CharacterStatType.MHP) > 0) {
            packet.encodeShort(this.mhp);
        }
        if ((flag & CharacterStatType.MP) > 0) {
            packet.encodeShort(this.mp);
        }
        if ((flag & CharacterStatType.MMP) > 0) {
            packet.encodeShort(this.mmp);
        }
        if ((flag & CharacterStatType.AP) > 0) {
            packet.encodeShort(this.ap);
        }
        if ((flag & CharacterStatType.SP) > 0) {
            packet.encodeShort(this.sp);
        }
        if ((flag & CharacterStatType.EXP) > 0) {
            packet.encodeInt(this.exp.get());
        }
        if ((flag & CharacterStatType.POP) > 0) {
            packet.encodeInt(this.pop);//wtf?
        }
        if ((flag & CharacterStatType.Money) > 0) {
            packet.encodeInt(this.money.get());
        }
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
    
    public long getPetLockerSN() {
        return petLockerSN;
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
    
    public void load(ResultSet rs) throws SQLException {
        setCharacterID(rs.getInt("CharacterID"));
        setName(rs.getString("CharacterName"));
        setGender(rs.getByte("Gender"));
        setSkin(rs.getByte("Skin"));
        setPetLockerSN(rs.getLong("PetLockerSN"));
        setFace(rs.getInt("Face"));
        setHair(rs.getInt("Hair"));
        setLevel(rs.getByte("Level"));
        setJob(rs.getShort("Job"));
        setSTR(rs.getShort("STR"));
        setDEX(rs.getShort("DEX"));
        setINT(rs.getShort("INT"));
        setLUK(rs.getShort("LUK"));
        setHP(rs.getShort("HP"));
        setMP(rs.getShort("MP"));
        setMHP(rs.getShort("MaxHP"));
        setMMP(rs.getShort("MaxMP"));
        setAP(rs.getShort("AP"));
        setSP(rs.getShort("SP"));
        setEXP(rs.getInt("EXP"));
        setPOP(rs.getShort("POP"));
        setMoney(rs.getInt("Money"));
        setPosMap(rs.getInt("Map"));
        setPortal(rs.getByte("Portal"));
    }
    
    private void setCharacterID(int characterID) {
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

    public void setSkin(int skin) {
        this.skin = (byte) skin;
    }
    
    public void setPetLockerSN(long sn) {
        this.petLockerSN = sn;
    }

    public void setLevel(int level) {
        this.level = (byte) level;
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

    public void setHP(int hp) {
        this.hp = (short) hp;
    }

    public void setMHP(short mhp) {
        this.mhp = mhp;
    }

    public void setMP(int mp) {
        this.mp = (short) mp;
    }

    public void setMMP(short mmp) {
        this.mmp = mmp;
    }

    public void setAP(int ap) {
        this.ap = (short) ap;
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
    
    public class CharacterStatType {
        public static final int
                Face    = 0x1,
                Hair    = 0x2,
                Skin    = 0x4,//Incorrect
                PetSN   = 0x4,
                LEV     = 0x8,
                Job     = 0x10,
                STR     = 0x20,
                DEX     = 0x40,
                INT     = 0x80,
                LUK     = 0x100,
                HP      = 0x200,
                MHP     = 0x400,
                MP      = 0x800,
                MMP     = 0x1000,
                AP      = 0x2000,
                SP      = 0x4000,
                EXP     = 0x8000,
                POP     = 0x10000,
                Money   = 0x20000
        ;
    }
}
