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

import network.packet.OutPacket;

/**
 * GW_Friend
 *
 * @author Eric
 */
public class FriendInfo {
	private int friendID;
	private String friendName;
	private byte flag;
	private int channelID;

	public FriendInfo() {
		this.friendName = "";
		this.channelID = -1;
	}

	public void encode(OutPacket packet) {
		packet.encodeInt(friendID);
		packet.encodeString(friendName, 13);
		packet.encodeByte(flag);
		packet.encodeInt(channelID);
	}

	public int getFriendID() {
		return friendID;
	}

	public String getFriendName() {
		return friendName;
	}

	public byte getFlag() {
		return flag;
	}

	public int getChannelID() {
		return channelID;
	}

	public void setFriendID(int characterID) {
		this.friendID = characterID;
	}

	public void setFriendName(String name) {
		this.friendName = name;
	}

	public void setFlag(int flag) {
		this.flag = (byte) flag;
	}

	public void setChannelID(int channelID) {
		this.channelID = channelID;
	}
}
