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

import game.miniroom.MiniRoomBase;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class UserCommon {
    
    /**
     * The character chat packet that sends a chat message to the field.
     * 
     * @param characterID The ID of the user sending the message
     * @param text The message itself
     * 
     * @return The chat packet
     */
    public static OutPacket onChat(int characterID, String text) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserChat);
        packet.encodeInt(characterID);
        packet.encodeString(text);
        return packet;
    }
    
    public static OutPacket onMiniRoomBalloon(int characterID, MiniRoomBase miniRoom) {
        OutPacket packet = new OutPacket(LoopbackPacket.UserMiniRoomBalloon);
        packet.encodeInt(characterID);
        if (miniRoom != null) {
            packet.encodeByte(miniRoom.getTypeNumber());
            packet.encodeInt(miniRoom.getMiniRoomSN());
            packet.encodeString(miniRoom.getTitle());
        } else {
            packet.encodeByte(0);
        }
        return packet;
    }
}
