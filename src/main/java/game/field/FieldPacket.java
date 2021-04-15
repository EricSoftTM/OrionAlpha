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
package game.field;

import common.WhisperFlags;
import common.WhisperFlags.LocationResult;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class FieldPacket {
    
    /**
     * The "block" packet to restrict users from transferring fields.
     * 
     * In later versions of the game Nexon has types that display messages.
     * However, this seems to just act as a "onExclRequest" type packet, which
     * means it just resets the client's request and updates the last request
     * time to the current time.
     * 
     * @return The ignore field request packet
     */
    public static OutPacket onTransferFieldReqIgnored(byte type) {
        OutPacket packet = new OutPacket(LoopbackPacket.TransferFieldReqIgnored);
        packet.encodeByte(type);
        return packet;
    }
    
    /**
     * The admin "ban" result packet - displays if the ban succeeded/failed.
     * 
     * @param result The result of a ban (-1 for fail, 1 for success)
     * 
     * @return The admin result packet
     */
    public static OutPacket onAdminResult(int result) {
        OutPacket packet = new OutPacket(LoopbackPacket.AdminResult);
        // if < 0: "The blocking failed"
        // if >= 0: "You have successfully blocked access"
        packet.encodeByte(result);
        return packet;
    }
    
    /**
     * The blow weather packet that rains snow and displays a message to the map.
     * 
     * @param weatherItemID The ItemID of the weather item (type 209)
     * @param weatherMsg The message to display for the weather effect
     * 
     * @return The weather packet
     */
    public static OutPacket onBlowWeather(int weatherItemID, String weatherMsg) {
        OutPacket packet = new OutPacket(LoopbackPacket.BlowWeather);
        packet.encodeInt(weatherItemID);
        if (weatherItemID != 0) {//0 = DestroyWeather
            packet.encodeString(weatherMsg);
        }
        return packet;
    }
    
    /**
     * The weird message packet - displays yellow in chat the Name:Message.
     * Oh, and it also renders the same Name:Message above the chat window.
     * 
     * Since groups (buddy, party, guild, etc.) don't exist yet, and no types
     * for BroadcastMsg exist for megaphones, my best guess is that Nexon uses
     * this packet for megaphones in this client. 
     * 
     * @param characterName The name of the character
     * @param message The message to display
     * 
     * @return The message/megaphone packet
     */
    public static OutPacket onGroupMessage(String characterName, String message) {
        OutPacket packet = new OutPacket(LoopbackPacket.GroupMessage);
        packet.encodeString(characterName);
        packet.encodeString(message);
        return packet;
    }
    
    /**
     * The OnWhisper Packet for /find and Whispering purposes.
     * Refer to WhisperFlags for the list of results that are used within
     * this packet.
     * 
     * @param flag The whisper flag to handle (@see common.WhisperFlags)
     * @param find The target you're Whispering or Finding
     * @param receiver Your character name
     * @param msg The message to be sent
     * @param locationResult The targets server location (@see WhisperFlags.LocationResult)
     * @param location The targets location (only FieldID in this version)
     * @param success If the user exists and is online/able to receive messages
     * 
     * @return A whisper and/or find packet.
     */
    public static OutPacket onWhisper(byte flag, String find, String receiver, String msg, byte locationResult, int location, boolean success) {
        OutPacket packet = new OutPacket(LoopbackPacket.Whisper);
        packet.encodeByte(flag);
        switch (flag) {
            case WhisperFlags.ReplyReceive:
                packet.encodeString(receiver);
                packet.encodeString(msg);
                break;
            case WhisperFlags.ReplyResult:
                packet.encodeString(find);
                packet.encodeBool(success);
                break;
            case WhisperFlags.FindResult:
                packet.encodeString(find);
                packet.encodeByte(locationResult);
                packet.encodeInt(location);
                break;
            case WhisperFlags.BlockedResult:
                packet.encodeString(find);
                break;
        }
        return packet;
    }
}
