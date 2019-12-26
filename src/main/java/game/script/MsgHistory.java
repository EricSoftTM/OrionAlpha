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
package game.script;

import java.util.ArrayList;
import java.util.List;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class MsgHistory {
    private int type;
    private OutPacket packet;
    private final List<Object> memory;
    private int speakerTypeID;
    private int speakerTemplateID;
    private int couponItemID;
    
    public MsgHistory(int type) {
        this.type = type;
        this.memory = new ArrayList<>();
    }
    
    public int getType() {
        return type;
    }
    
    public OutPacket getPacket() {
        return packet;
    }
    
    public List<Object> getMemory() {
        return memory;
    }
    
    public int getCouponItemID() {
        return couponItemID;
    }
    
    public int getSpeakerTypeID() {
        return speakerTypeID;
    }
    
    public int getSpeakerTemplateID() {
        return speakerTemplateID;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public void setPacket(OutPacket packet) {
        this.packet = packet;
    }
    
    public void setCouponItemID(int itemID) {
        this.couponItemID = itemID;
    }
    
    public void setSpeakerTypeID(int type) {
        this.speakerTypeID = type;
    }
    
    public void setSpeakerTemplateID(int templateID) {
        this.speakerTemplateID = templateID;
    }
}
