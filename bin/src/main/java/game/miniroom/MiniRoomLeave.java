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
package game.miniroom;

/**
 * Mini Room Leave Types
 *
 * @author Eric
 */
public enum MiniRoomLeave {

    // NOT EVEN SURE IF THIS IS EVEN A THING YET
    
    // MiniRoomLeave
    UserRequest(0),// idk 
    WrongPosition(1),// idk 
    Closed(2), // doesn't exist
    HostOut(3),// doesn't exist ?
    Booked(4), // doesn't exist
    Kicked(5), // doesn't exist
    OpenTimeOver(6), // doesn't exist
    // TradingRoomLeave
    TradeDone(7), // idk 
    TradeFail(8),// idk 
    TradeFail_OnlyItem(9),// idk 
    TradeFail_Expired(10),// idk 
    TradeFail_Denied(11);// idk 
    private final int type;

    private MiniRoomLeave(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static MiniRoomLeave Get(int type) {
        for (MiniRoomLeave mrl : MiniRoomLeave.values()) {
            if (mrl.type == type) {
                return mrl;
            }
        }
        return null;
    }
}
