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

import game.GameApp;
import game.user.User;
import network.database.GameDB;
import network.packet.InPacket;
import util.Logger;
import util.Pointer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eric
 */
public class FriendMan {
	private static final FriendMan instance = new FriendMan();

	private final Map<Integer, Friend> friends;

	private FriendMan() {
		this.friends = new ConcurrentHashMap<>();
	}

	public static FriendMan getInstance() {
		return instance;
	}

	public Friend getFriend(int characterID) {
		return friends.get(characterID);
	}

	public int getFriendCount(int characterID) {
		Friend friend = getFriend(characterID);
		if (friend != null) {
			return friend.getFriends().size();
		}
		return 0;
	}

	public int getChannel(int characterId) {
		User user = GameApp.getInstance().findUser(characterId);
		if (user != null) {
			return user.getChannelID();
		}
		return -1;
	}

	public Friend loadFriend(int characterID, String characterName, boolean send) {
		User user;
		if (characterID != 0) {
			user = GameApp.getInstance().findUser(characterID);
		} else {
			user = GameApp.getInstance().findUserByName(characterName, true);
		}

		int friendMax;
		if (user != null) {
			characterID = user.getCharacterID();
			friendMax = user.getCharacter().getFriendMax();
		} else {
			Pointer<Integer> targetID = new Pointer<>(0);
			if (characterName != null) {
				if (!GameDB.getCharacterInfoByName(characterName, targetID)) {
					return null;
				}
				characterID = targetID.get();
			}
			friendMax = GameDB.getFriendCount(characterID);
			if (friendMax <= 0) {
				return null;
			}
		}

		Friend entry = friends.get(characterID);
		if (entry != null) {
			entry.setFriendMax(friendMax);
			entry.setLastAccessed(System.currentTimeMillis());
		} else {
			entry = new Friend();
			if (!GameDB.loadFriend(characterID, entry.getFriends())) {
				Logger.logError("loadFriend in FriendMan::LoadFriend failed");
				return null;
			}
			entry.setCharacterID(characterID);
			entry.setFriendMax(friendMax);

			friends.put(characterID, entry);

			for (FriendInfo info : entry.getFriends()) {
				if (info.getFlag() != FriendStatus.Normal) {
					continue;
				}
				info.setChannelID(getChannel(info.getFriendID()));
			}
		}
		if (send && user != null) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.LoadFriend_Done, entry));
		}
		return entry;
	}

	/**
	 * @param channelID -1 if offline
	 */
	public void notify(int characterID, int channelID) {
		Friend friend = loadFriend(characterID, null, false);
		if (friend == null) {
			return;
		}
		friend.setLogined(channelID >= 0);
		friend.setLastAccessed(System.currentTimeMillis());
		for (FriendInfo friendInfo : friend.getFriends()) {
			// Checks if  you have each other added & with status Normal
			if (friendInfo.getFlag() != FriendStatus.Normal) {
				continue;
			}
			Friend friendEntry = friends.get(friendInfo.getFriendID());
			if (friendEntry == null) {
				continue;
			}
			FriendInfo requesterInfo = friendEntry.find(characterID, FriendStatus.Normal);
			if (requesterInfo != null) {
				requesterInfo.setChannelID(channelID);
				User user = GameApp.getInstance().findUser(friendInfo.getFriendID());
				if (user != null) {
					user.sendPacket(FriendPacket.onFriendResult(requesterInfo.getFriendID(), channelID));
				}
			}
		}
	}

	private void onAcceptFriend(User user, InPacket packet) {
		int characterID = packet.decodeInt();
		int friendID = packet.decodeInt();

		if (characterID != user.getCharacterID()) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.AcceptFriend_Unknown));
			return;
		}

		Friend reqFriend = loadFriend(characterID, null, false);
		if (reqFriend == null) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.AcceptFriend_Unknown));
			return;
		}

		FriendInfo friendInfo = reqFriend.find(friendID, -1);
		if (friendInfo == null || friendInfo.getFlag() != FriendStatus.Request) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.AcceptFriend_Unknown));
			return;
		}

		int flag = GameDB.setAcceptFriend(characterID, friendID, user.getCharacterName());
		if (flag < 0) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.AcceptFriend_Unknown));
			Logger.logError("setAcceptFriend in FriendMan::AcceptFriend Failed : %d, %d", characterID, friendID);
			return;
		}

		if (flag == FriendStatus.Normal) {
			Friend friend = loadFriend(friendID, null, false);
			if (friend == null) {
				return;
			}
			FriendInfo reqFriendInfo = friend.find(characterID, -1);
			if (reqFriendInfo != null) {
				reqFriendInfo.setFriendName(user.getCharacterName());
				reqFriendInfo.setFlag(FriendStatus.Normal);
				reqFriendInfo.setChannelID(getChannel(characterID));
				User friendUser = GameApp.getInstance().findUser(friendID);
				if (friendUser != null) {
					friendUser.sendPacket(FriendPacket.onFriendResult(FriendResCode.LoadFriend_Done, friend));
				}
			}
		}
		friendInfo.setFlag(flag);
		friendInfo.setChannelID(getChannel(friendID));
		user.sendPacket(FriendPacket.onFriendResult(FriendResCode.LoadFriend_Done, reqFriend));
	}

	private void onDeleteFriend(User user, InPacket packet) {
		int characterID = packet.decodeInt();
		int friendID = packet.decodeInt();

		if (characterID != user.getCharacterID()) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.DeleteFriend_Unknown));
			return;
		}

		Friend reqFriend = loadFriend(characterID, null, false);
		if (reqFriend == null) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.DeleteFriend_Unknown));
			return;
		}
		FriendInfo friendInfo = reqFriend.find(friendID, -1);
		if (friendInfo == null) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.DeleteFriend_Unknown));
			return;
		}

		if (!GameDB.deleteFriend(characterID, friendID)) {
			Logger.logError("deleteFriend in FriendMan::DeleteFriend Failed");
			return;
		}

		reqFriend.remove(friendID);
		user.sendPacket(FriendPacket.onFriendResult(FriendResCode.DeleteFriend_Done, reqFriend));

		Friend friend = loadFriend(friendID, null, false);
		if (friend == null) {
			return;
		}
		FriendInfo reqFriendInfo = friend.find(characterID, -1);
		if (reqFriendInfo == null || reqFriendInfo.getFlag() != FriendStatus.Normal) {
			return;
		}
		//reqFriendInfo.setFlag(FriendStatus.Refused);
		friend.remove(characterID);
		User friendUser = GameApp.getInstance().findUser(friendID);
		if (friendUser != null) {
			friendUser.sendPacket(FriendPacket.onFriendResult(FriendResCode.LoadFriend_Done, friend));
		}
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

		if (characterID != user.getCharacterID()) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_Unknown));
			return;
		}

		if (friendName.length() < 4 || friendName.length() > 12) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_Unknown));
			return;
		}

		if (friendName.equalsIgnoreCase(user.getCharacterName())) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_Unknown));
			return;
		}

		Friend reqFriend = loadFriend(characterID, null, false);
		Friend friend = loadFriend(0, friendName, false);
		if (reqFriend == null) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_Unknown));
			return;
		}

		if (friend == null) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_UnknownUser));
			return;
		}

		if (reqFriend.getFriends().size() >= reqFriend.getFriendMax()) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_FullMe));
			return;
		}

		if (friend.getFriends().size() >= friend.getFriendMax()) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_FullOther));
			return;
		}

		FriendInfo targetInfo = reqFriend.find(friend.getCharacterID(), -1);
		if (targetInfo != null) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_AlreadySet));
			return;
		}

		if (!GameDB.setFriend(characterID, friend.getCharacterID(), user.getCharacterName(), friendName)) {
			user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_Unknown));
			return;
		}
		// Since "Refused" flagged users do not show in the friends list
		// We've opted to completely delete them on both sides.
		// Some functionality of it still exists commented out but important parts were deleted
		// Would need to fix setFriend, deleteFriend and the respective DB methods.

		FriendInfo info = new FriendInfo();
		info.setFriendID(friend.getCharacterID());
		info.setFriendName(friendName);
		info.setFlag(FriendStatus.Normal);
		info.setChannelID(getChannel(friend.getCharacterID()));
		reqFriend.getFriends().add(info);
		user.sendPacket(FriendPacket.onFriendResult(FriendResCode.SetFriend_Done, reqFriend));

		info = friend.find(characterID, -1); // Grab the FriendInfo from the person you're adding.
		if (info == null) { // If they don't already have you, request it & notify.
			info = new FriendInfo();
			info.setFriendID(characterID);
			info.setFriendName(user.getCharacterName());
			info.setFlag(FriendStatus.Request);
			info.setChannelID(getChannel(characterID));
			friend.getFriends().add(info);
			User friendUser = GameApp.getInstance().findUser(friend.getCharacterID());
			if (friendUser != null) { // Notify of request
				friendUser.sendPacket(FriendPacket.onFriendResult(user.getCharacterID(), user.getCharacterName(), info));
			}
		} else if (info.getFlag() == FriendStatus.Refused) {// You're still on their friendlist since you deleted them, instantly accept.
			info.setFlag(FriendStatus.Normal);
			info.setChannelID(getChannel(characterID));
			User friendUser = GameApp.getInstance().findUser(friend.getCharacterID());
			if (friendUser != null) {
				friendUser.sendPacket(FriendPacket.onFriendResult(FriendResCode.LoadFriend_Done, friend));
			}
		}
	}
}
