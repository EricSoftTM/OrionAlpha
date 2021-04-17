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
 *
 * @author Eric
 */
public class Friend {
	private final List<FriendInfo> friends;
	
	public Friend() {
		this.friends = new ArrayList<>();
	}
	
	public void encode(OutPacket packet) {
		packet.encodeByte(friends.size());
		for (FriendInfo friend : friends) {
			friend.encode(packet);
		}
	}
	
	public int findIndex(int friendID) {
		for (int i = 0; i < friends.size(); i++) {
			FriendInfo friend = friends.get(i);
			if (friend != null && friend.getFriendID() == friendID) {
				return i;
			}
		}
		return -1;
	}
	
	public List<FriendInfo> getFriends() {
		return friends;
	}
	
	public void remove(int friendID) {
		int idx = findIndex(friendID);
		if (idx >= 0) {
			friends.remove(idx);
		}
	}
	
	public void setFieldID(int friendID, int fieldID) {
		int idx = findIndex(friendID);
		if (idx >= 0) {
			FriendInfo friend = friends.get(idx);
			friend.setFieldID(fieldID);
		}
	}
	
	public void setFriend(int friendID, FriendInfo friend) {
		for (int i = 0; i < friends.size(); i++) {
			FriendInfo f = friends.get(i);
			if (f.getFriendID() == friendID) {
				f.setFieldID(friend.getFieldID());
				f.setFriendName(friend.getFriendName());
				f.setFlag(friend.getFlag());
				break;
			}
		}
	}
}
