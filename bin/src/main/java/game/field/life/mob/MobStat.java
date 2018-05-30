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

import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class MobStat {
    
    public MobStat() {
        
    }
    
    public void encodeTemporary(OutPacket packet, int flag) {
        packet.encodeInt(flag);
        if ((flag & MobStats.PAD) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
        if ((flag & MobStats.PDD) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
        if ((flag & MobStats.MAD) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
        if ((flag & MobStats.MDD) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
        if ((flag & MobStats.ACC) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
        if ((flag & MobStats.EVA) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
        if ((flag & MobStats.Speed) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
        if ((flag & MobStats.Stun) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
        if ((flag & MobStats.Freeze) != 0) {
            packet.encodeShort(0);//nOption
            packet.encodeInt(0);//rOption
            packet.encodeShort(0);//tDuration
        }
    }
    
    public short getACC() {
        return 0;//TODO
    }
    
    public short getEVA() {
        return 0;//TODO
    }
    
    public int getLevel() {
        return 0;//TODO
    }
    
    public int getStatOption(int cts) {
        return 0;//TODO
    }
    
    public void setFrom(MobTemplate template) {
        //TODO
    }
}
