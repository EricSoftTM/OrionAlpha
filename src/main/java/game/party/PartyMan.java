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

import common.JobCategory;
import game.GameApp;
import game.user.User;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Eric
 */
public class PartyMan {
    private static final PartyMan instance = new PartyMan();
    
    private final Lock lock;
    private final AtomicInteger partyIDCounter;
    private final Map<Integer, Integer> charIDToPartyID;
    private final Map<Integer, PartyData> party;
    
    private PartyMan() {
    	this.lock = new ReentrantLock();
    	this.partyIDCounter = new AtomicInteger(0);
    	this.charIDToPartyID = new HashMap<>();
    	this.party = new HashMap<>();
    }
    
    public static PartyMan getInstance() {
    	return instance;
    }
    
    public void broadcast(OutPacket packet, int partyID) {
    	PartyData pd = party.get(partyID);
    	if (pd != null) {
    		broadcast(packet, pd.getParty().getCharacterID(), 0);
	    }
    }
    
    public void broadcast(OutPacket packet, List<Integer> memberID, int plusOne) {
    	lock.lock();
    	try {
    		if (plusOne != 0) {
    			User user = GameApp.getInstance().findUser(plusOne);
    			if (user != null) {
    				user.sendPacket(packet);
			    }
		    }
		    for (int characterID : memberID) {
		    	if (characterID != 0) {
		    		User user = GameApp.getInstance().findUser(characterID);
		    		if (user != null) {
		    			user.sendPacket(packet);
				    }
			    }
		    }
	    } finally {
    		lock.unlock();
	    }
    }
    
    public PartyData getParty(int partyID) {
    	lock.lock();
    	try {
    		return party.get(partyID);
	    } finally {
    		lock.unlock();
	    }
    }
	
	public int getPartyID(int characterID) {
		lock.lock();
		try {
			return charIDToPartyID.getOrDefault(characterID, 0);
		} finally {
			lock.unlock();
		}
	}
	
	public boolean isPartyBoss(int partyID, int characterID) {
    	lock.lock();
    	try {
    		PartyData pd = party.get(partyID);
    		if (pd != null) {
    			return pd.getParty().getPartyBoss() == characterID;
		    }
    		return false;
	    } finally {
    		lock.unlock();
	    }
	}
	
	public boolean isPartyBoss(int characterID) {
    	int partyID = getPartyID(characterID);
    	if (partyID != 0) {
    		return isPartyBoss(partyID, characterID);
	    }
    	return false;
	}
	
	public boolean isPartyMember(int partyID, int characterID) {
    	lock.lock();
    	try {
    		PartyData pd = party.get(partyID);
    		if (pd != null) {
    			return pd.getParty().getCharacterID().contains(characterID);
		    }
    		return false;
	    } finally {
    		lock.unlock();
	    }
	}
	
	public void loadParty(User user) {
    	int partyID = getPartyID(user.getCharacterID());
    	if (partyID != 0) {
    		PartyData pd = party.get(partyID);
    		if (pd != null) {
    			user.sendPacket(PartyPacket.onPartyResult(partyID, pd));
		    }
	    }
	}
	
	public void notifyTransferField(int characterID, int fieldID) {
		lock.lock();
		try {
			int partyID = getPartyID(characterID);
			if (partyID != 0) {
				PartyData pd = party.get(partyID);
				if (pd != null) {
					int idx = pd.getParty().getCharacterID().indexOf(characterID);
					if (idx >= 0) {
						pd.getParty().getFieldID().set(idx, fieldID);
						broadcast(PartyPacket.onPartyResult(partyID, pd), partyID);
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}
	
	private void onCreateNewParty(User user) {
    	if (getPartyID(user.getCharacterID()) != 0) {
		    user.sendPacket(PartyPacket.onPartyResult(PartyResCode.CreateNewParty_AlreadyJoined));
	    } else if (user.getCharacter().getCharacterStat().getJob() == JobCategory.None) {
		    user.sendPacket(PartyPacket.onPartyResult(PartyResCode.CreateNewParty_Beginner));
	    } else {
    		int partyID = partyIDCounter.incrementAndGet();
    		PartyData pd = new PartyData();
    		pd.getParty().getCharacterID().set(0, user.getCharacterID());
    		pd.getParty().getCharacterName().set(0, user.getCharacterName());
    		pd.getParty().getFieldID().set(0, user.getField().getFieldID());
    		pd.getParty().setPartyBoss(user.getCharacterID());
    		party.put(partyID, pd);
    		charIDToPartyID.put(user.getCharacterID(), partyID);
    		user.sendPacket(PartyPacket.onPartyResult(partyID));
	    }
	}
	
	private void onInviteParty(User user, InPacket packet) {
		int partyID = getPartyID(user.getCharacterID());
		if (partyID == 0 || isPartyBoss(partyID, user.getCharacterID())) {
			User invited = user.getChannel().findUser(packet.decodeInt());
			if (invited != null && (!invited.isGM() || user.isGM())) {
				if (getPartyID(invited.getCharacterID()) != 0) {
					user.sendPacket(PartyPacket.onPartyResult(PartyResCode.CreateNewParty_AlreadyJoined));
				} else {
					invited.sendPacket(PartyPacket.onPartyResult(user.getCharacterID(), user.getCharacterName()));
					user.addPartyInvitedCharacter(invited.getCharacterID());
				}
			}
		}
	}
	
	private void onJoinParty(User user, InPacket packet) {
		User inviter = user.getChannel().findUser(packet.decodeInt());
		if (inviter != null) {
			if (!inviter.isPartyInvitedCharacter(user.getCharacterID())) {
				Logger.logError("Uninvited User Tried to join party.");
				return;
			}
			inviter.removePartyInvitedCharacter(user.getCharacterID());
			int partyID = getPartyID(inviter.getCharacterID());
			if (partyID != 0) {
				PartyData pd = party.get(partyID);
				if (pd != null) {
					int idx = pd.getParty().getCharacterID().indexOf(0);
					if (idx >= 0) {
						if (getPartyID(user.getCharacterID()) != 0) {
							user.sendPacket(PartyPacket.onPartyResult(PartyResCode.JoinParty_AlreadyJoined));
						} else {
							charIDToPartyID.put(user.getCharacterID(), partyID);
							pd.getParty().getCharacterID().set(idx, user.getCharacterID());
							pd.getParty().getCharacterName().set(idx, user.getCharacterName());
							pd.getParty().getFieldID().set(idx, user.getField().getFieldID());
							broadcast(PartyPacket.onPartyResult(partyID, user.getCharacterName(), pd), partyID);
						}
					} else {
						user.sendPacket(PartyPacket.onPartyResult(PartyResCode.JoinParty_OverDesiredSize));
					}
				} else {
					user.sendPacket(PartyPacket.onPartyResult(PartyResCode.JoinParty_Unknown));
				}
			} else {
				user.sendPacket(PartyPacket.onPartyResult(PartyResCode.JoinParty_UnknownUser));
			}
		}
	}
	
	private void onKickParty(User user, InPacket packet) {
		int memberID = packet.decodeInt();
		int partyID = getPartyID(user.getCharacterID());
		if (partyID != 0 && isPartyBoss(user.getCharacterID()) && isPartyMember(partyID, memberID)) {
			User member = GameApp.getInstance().findUser(memberID);
			if (member != null) {
				onWithdrawParty(member, true);
			}
		}
	}
	
	public void onPacket(User user, byte type, InPacket packet) {
    	if (type == ClientPacket.PartyRequest) {
    		byte retCode = packet.decodeByte();
		    switch (retCode) {
			    case PartyResCode.CreateNewParty:
				    onCreateNewParty(user);
				    break;
			    case PartyResCode.InviteParty:
				    onInviteParty(user, packet);
				    break;
			    case PartyResCode.WithdrawParty:
				    onWithdrawParty(user, packet.decodeBool());
				    break;
			    case PartyResCode.JoinParty:
				    onJoinParty(user, packet);
				    break;
			    case PartyResCode.KickParty:
				    onKickParty(user, packet);
				    break;
		    }
	    } else if (type == ClientPacket.PartyResult) {
    		onPartyResult(user, packet);
	    }
	}
	
	private void onPartyResult(User user, InPacket packet) {
		byte retCode = packet.decodeByte();
		if (retCode == PartyResCode.InviteParty_Rejected) {
			User inviter = user.getChannel().findUserByName(packet.decodeString(), true);
			if (inviter != null) {
				String invited = packet.decodeString();
				if (user.getCharacterName().equals(invited)) {
					inviter.sendPacket(PartyPacket.onPartyResult(packet.decodeBool(), invited));
					inviter.removePartyInvitedCharacter(user.getCharacterID());
				}
			}
		}
	}
	
	private void onWithdrawParty(User user, boolean kicked) {
		int partyID = getPartyID(user.getCharacterID());
		if (partyID != 0) {
			PartyData pd = party.get(partyID);
			if (pd != null) {
				int memberIdx = pd.getParty().getCharacterID().indexOf(user.getCharacterID());
				if (memberIdx >= 0) {
					if (user.getCharacterID() == pd.getParty().getPartyBoss()) {
						party.remove(partyID);
						for (int characterID : pd.getParty().getCharacterID()) {
							charIDToPartyID.remove(characterID);
						}
						broadcast(PartyPacket.onPartyResult(partyID, user.getCharacterID(), false, false, user.getCharacterName(), pd), pd.getParty().getCharacterID(), 0);
					} else {
						charIDToPartyID.remove(user.getCharacterID());
						pd.getParty().getCharacterID().set(memberIdx, 0);
						pd.getParty().getCharacterName().set(memberIdx, "");
						pd.getParty().getFieldID().set(memberIdx, -1);
						broadcast(PartyPacket.onPartyResult(partyID, user.getCharacterID(), true, kicked, user.getCharacterName(), pd), pd.getParty().getCharacterID(), user.getCharacterID());
					}
				} else {
					user.sendPacket(PartyPacket.onPartyResult(PartyResCode.WithdrawParty_NotJoined));
				}
			} else {
				user.sendPacket(PartyPacket.onPartyResult(PartyResCode.WithdrawParty_NotJoined));
			}
		} else {
			user.sendPacket(PartyPacket.onPartyResult(PartyResCode.WithdrawParty_NotJoined));
		}
	}
}
