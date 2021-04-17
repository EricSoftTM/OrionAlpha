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
package game.friend;

import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class FriendPacket {
	
	public static OutPacket onFriendResult(byte retCode) {
		OutPacket packet = new OutPacket(LoopbackPacket.FriendResult);
		packet.encodeByte(retCode);
		return packet;
	}
	
	public static OutPacket onFriendResult(byte retCode, Friend friend) {
		OutPacket packet = new OutPacket(LoopbackPacket.FriendResult);
		packet.encodeByte(retCode);//LoadFriend_Done, SetFriend_Done, DeleteFriend_Done
		friend.encode(packet);
		return packet;
	}
	
	public static OutPacket onFriendResult(int friendID, int fieldID) {
		OutPacket packet = new OutPacket(LoopbackPacket.FriendResult);
		packet.encodeByte(FriendResCode.Notify);
		packet.encodeInt(friendID);
		packet.encodeInt(fieldID);
		return packet;
	}
	
	public static OutPacket onFriendResult(int friendID, String friendName, FriendInfo friend) {
		OutPacket packet = new OutPacket(LoopbackPacket.FriendResult);
		packet.encodeByte(FriendResCode.Invite);
		packet.encodeInt(friendID);
		packet.encodeString(friendName);
		friend.encode(packet);
		return packet;
	}
}
