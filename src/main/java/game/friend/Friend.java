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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eric
 */
public class Friend {
	private boolean logined;
	private int characterID;
	private int friendMax;
	private final List<FriendInfo> friends;
	private long lastAccessed;

	public Friend() {
		this.friends = new ArrayList<>();
		this.lastAccessed = System.currentTimeMillis();
	}

	public void encode(OutPacket packet) {
		packet.encodeByte(friends.size());
		for (FriendInfo friend : friends) {
			friend.encode(packet);
		}
	}

	public FriendInfo find(int friendID, int flag) {
		for (FriendInfo info : friends) {
			if (info.getFriendID() == friendID && (flag < 0 || info.getFlag() == flag)) {
				return info;
			}
		}
		return null;
	}

	public List<FriendInfo> getFriends() {
		return friends;
	}

	public void remove(int friendID) {
		FriendInfo info = find(friendID, -1);
		if (info != null) {
			friends.remove(info);
		}
	}

	public boolean isLogined() {
		return logined;
	}

	public void setLogined(boolean logined) {
		this.logined = logined;
	}

	public int getCharacterID() {
		return characterID;
	}

	public void setCharacterID(int characterID) {
		this.characterID = characterID;
	}

	public int getFriendMax() {
		return friendMax;
	}

	public void setFriendMax(int friendMax) {
		this.friendMax = friendMax;
	}

	public long getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(long lastAccessed) {
		this.lastAccessed = lastAccessed;
	}
}
