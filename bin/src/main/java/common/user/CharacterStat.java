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
    
    public void Encode(OutPacket packet) {
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
}
