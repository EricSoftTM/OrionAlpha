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
package game.user;

import game.user.stat.SecondaryStat;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class UserPool {
    
    /**
     * Adds a user into the field.
     * 
     * @param user The user to enter into the field
     * 
     * @return The enter field packet
     */
    public static OutPacket onUserEnterField(User user) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserEnterField);
        packet.encodeInt(user.getCharacterID());
        
        packet.encodeString(user.getCharacterName());
        packet.encodeByte(user.getCharacter().getCharacterStat().getGender());
        
        user.getSecondaryStat().encodeForRemote(packet, SecondaryStat.FilterForRemote);
        
        packet.encodeInt(user.getCharacter().getCharacterStat().getFace());
        packet.encodeInt(0); //AvatarLook.Unknown2 (unused int)
        packet.encodeInt(0); //AvatarLook.Unknown3 (unused int)
        
        user.getAvatarLook().encode(packet);
        packet.encodeInt(0); //Unknown (modifies *(v3 + 161) aka *(v1 + 644))
        
        packet.encodeShort(user.getCurrentPosition().x);
        packet.encodeShort(user.getCurrentPosition().y);
        packet.encodeByte(user.getMoveAction());
        packet.encodeShort(user.getFootholdSN());
        return packet;
    }
    
    /**
     * Removes a user from the field.
     * 
     * @param characterID The ID of the user to remove from the map
     * 
     * @return The leave field packet
     */
    public static OutPacket onUserLeaveField(int characterID) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserLeaveField);
        packet.encodeInt(characterID);
        return packet;
    }
}
