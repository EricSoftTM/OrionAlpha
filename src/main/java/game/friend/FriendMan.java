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

import game.user.User;
import network.packet.InPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Eric
 */
public class FriendMan {
	private static final FriendMan instance = new FriendMan();
	
	private final Lock lock;
	private final Map<Integer, Friend> friends;
	
	private FriendMan() {
		this.lock = new ReentrantLock();
		this.friends = new HashMap<>();
	}
	
	public static FriendMan getInstance() {
		return instance;
	}
	
	public Friend getFriend(int characterID) {
		lock.lock();
		try {
			return friends.get(characterID);
		} finally {
			lock.unlock();
		}
	}
	
	public int getFriendCount(int characterID) {
		Friend friend = getFriend(characterID);
		if (friend != null) {
			return friend.getFriends().size();
		}
		return 0;
	}
	
	public void loadFriend(User user) {
		Friend friend = getFriend(user.getCharacterID());
		if (friend == null) {
			friend = new Friend();
			
			// Load from DB
			
			friends.put(user.getCharacterID(), friend);
		}
		user.sendPacket(FriendPacket.onFriendResult(FriendResCode.LoadFriend_Done, friend));
	}
	
	public void notifyLogin(int characterID, int channelID) {
	
	}
	
	private void onAcceptFriend(User user, InPacket packet) {
		int characterID = packet.decodeInt();
		int friendID = packet.decodeInt();
	}
	
	private void onDeleteFriend(User user, InPacket packet) {
		int characterID = packet.decodeInt();
		int friendID = packet.decodeInt();
	}
	
	public void onPacket(User user, InPacket packet) {
		byte retCode = packet.decodeByte();
		switch (retCode) {
			case FriendResCode.LoadFriend:
				break;
			case FriendResCode.SetFriend:
				onSetFriend(user, packet);
				break;
			case FriendResCode.AcceptFriend:
				onAcceptFriend(user, packet);
				break;
			case FriendResCode.DeleteFriend:
				onDeleteFriend(user, packet);
				break;
		}
	}
	
	private void onSetFriend(User user, InPacket packet) {
		int characterID = packet.decodeInt();
		String friendName = packet.decodeString();
	}
}
