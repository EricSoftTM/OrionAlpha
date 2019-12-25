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
package game.user.stat;

/**
 *
 * @author Eric
 */
public class SecondaryStatOption {
    private short skillOption;
    private int skillReason;
    private int skillModOption;
    private long skillDuration;
    
    public SecondaryStatOption() {
        this.skillOption = 0;
        this.skillReason = 0;
        this.skillDuration = 0;
        this.skillModOption = 0;
    }
    
    public SecondaryStatOption(int option, int reason, long duration) {
        this.skillOption = (short) option;
        this.skillReason = reason;
        this.skillDuration = duration;
        this.skillModOption = 0;
    }
    
    public int getModOption() {
        return skillModOption;
    }
    
    public short getOption() {
        return skillOption;
    }
    
    public int getReason() {
        return skillReason;
    }
    
    public long getDuration() {
        return skillDuration;
    }
    
    public void setModOption(int mod) {
        this.skillModOption = mod;
    }
    
    public void setOption(int option) {
        this.skillOption = (short) option;
    }
    
    public void setReason(int reason) {
        this.skillReason = reason;
    }
    
    public void setDuration(long duration) {
        this.skillDuration = duration;
    }
}
