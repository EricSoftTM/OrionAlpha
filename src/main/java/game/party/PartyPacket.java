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
package game.party;

import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class PartyPacket {
    
    public static OutPacket onPartyResult(byte retCode) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(retCode);
        return packet;
    }
    
    public static OutPacket onPartyResult(int inviterID, String applierName) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.InviteParty);
        packet.encodeInt(inviterID);
        packet.encodeString(applierName);
        return packet;
    }
    
    public static OutPacket onPartyResult(int partyID, String characterName, PartyData party) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.JoinParty_Done);
        packet.encodeInt(partyID);
        packet.encodeString(characterName);
        party.encode(packet);
        return packet;
    }
    
    public static OutPacket onPartyResult(int partyID, PartyData party) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.LoadParty_Done);
        packet.encodeInt(partyID);
        party.encode(packet);
        return packet;
    }
    
    public static OutPacket onPartyResult(int partyID, int characterID, boolean leave, boolean kicked, String name, PartyData party) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.WithdrawParty_Done);
        packet.encodeInt(partyID);
        packet.encodeInt(characterID);
        packet.encodeBool(leave);
        if (leave) {
            packet.encodeBool(kicked);
            packet.encodeString(name);
            party.encode(packet);
        }
        return packet;
    }
    
    public static OutPacket onPartyResult(int partyID) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.CreateNewParty_Done);
        packet.encodeInt(partyID);
        return packet;
    }
    
    public static OutPacket onPartyResult(boolean accept, String name) {
        OutPacket packet = new OutPacket(LoopbackPacket.PartyResult);
        packet.encodeByte(PartyResCode.InviteParty_Rejected);
        packet.encodeString(name);
        packet.encodeBool(accept);
        return packet;
    }
}
