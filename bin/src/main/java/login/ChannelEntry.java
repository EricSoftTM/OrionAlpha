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
package login;

/**
 *
 * @author Eric
 */
public class ChannelEntry {
    private int userNo;
    private final byte worldID;
    private final byte channelID;
    
    public ChannelEntry(byte worldID, byte channelID) {
        this.worldID = worldID;
        this.channelID = channelID;
    }
    
    public byte getChannelID() {
        return channelID;
    }
    
    public int getUserNo() {
        return userNo;
    }
    
    public byte getWorldID() {
        return worldID;
    }
}
