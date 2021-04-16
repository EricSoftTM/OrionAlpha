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
package game.user.pet;

import common.ExpAccessor;
import common.item.ItemSlotBase;
import common.item.ItemSlotPet;
import common.item.ItemType;
import game.field.Field;
import game.field.MovePath;
import game.field.StaticFoothold;
import game.user.User;
import game.user.item.Inventory;
import network.packet.ClientPacket;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Rand32;

import java.awt.*;

/**
 *
 * @author Eric
 */
public class Pet {
	private PetTemplate template;
	private long petLockerSN;
	private User owner;
	private Field field;
	private String name;
	private short itemSlotPos;
	private long remainHungriness;
	private long lastUpdated;
	private long lastInteraction;
	private int overeat;
	private Point curPos;
	private byte moveAction;
	private short footholdSN;
	
	public Pet() {
		this.name = "";
		this.remainHungriness = 36000;
		this.curPos = new Point(0, 0);
		
		long curTime = System.currentTimeMillis();
		this.lastUpdated = curTime;
		this.lastInteraction = curTime;
	}
	
	public void encodeEnterPacket(OutPacket packet) {
		ItemSlotPet item = getItemSlot();
		packet.encodeInt(getTemplateID());
		packet.encodeString(item.getPetName());
		packet.encodeByte(item.getLevel());
		packet.encodeShort(item.getTameness());
		packet.encodeByte(item.getRepleteness());
		packet.encodeShort(curPos.x);
		packet.encodeShort(curPos.y);
		packet.encodeByte(getMoveAction());
		packet.encodeShort(getFootholdSN());
	}
	
	public String getBasicName() {
		return template.getName();
	}
	
	public Field getField() {
		return field;
	}
	
	public short getFootholdSN() {
		return footholdSN;
	}
	
	public ItemSlotPet getItemSlot() {
		return (ItemSlotPet) owner.getCharacter().getItem(ItemType.Cash, getItemSlotPos());
	}
	
	public short getItemSlotPos() {
		ItemSlotBase item = owner.getCharacter().getItem(ItemType.Cash, itemSlotPos);
		if (item == null || item.getCashItemSN() != petLockerSN) {
			itemSlotPos = (short) owner.getCharacter().findCashItemSlotPosition(ItemType.Cash, petLockerSN);
		}
		return itemSlotPos;
	}
	
	public byte getLevel() {
		final byte maxLevel = (byte) ExpAccessor.EXP_PET.length;
		byte level = 0;
		
		ItemSlotPet item = getItemSlot();
		if (item != null) {
			short tameness = item.getTameness();
			for (int exp : ExpAccessor.EXP_PET) {
				if (tameness >= exp) {
					++level;
					if (level >= maxLevel)
						return maxLevel;
				}
			}
		} else {
			return -1;
		}
		return ++level;
	}
	
	public byte getMoveAction() {
		return moveAction;
	}
	
	public String getName() {
		return name;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public long getPetLockerSN() {
		return petLockerSN;
	}
	
	public PetTemplate getTemplate() {
		return template;
	}
	
	public int getTemplateID() {
		return getTemplate().getTemplateID();
	}
	
	public int incTameness(ItemSlotPet item, int inc) {
		int tameness = item.getTameness();
		if (tameness + inc >= 0) {
			if (tameness + inc > 30000) {
				inc = 30000 - tameness;
			}
		} else {
			inc = -tameness;
		}
		if (inc != 0) {
			item.setTameness(item.getTameness() + inc);
			byte level = getLevel();
			if (level > item.getLevel()) {
				item.setLevel(level);
				
				getField().splitSendPacket(getOwner().getSplit(), PetPacket.onPetEffect(getOwner().getCharacterID(), PetEffect.LevelUp), null);
			}
		}
		return inc;
	}
	
	public boolean init(User user, short pos) {
		ItemSlotPet item = (ItemSlotPet) user.getCharacter().getItem(ItemType.Cash, pos);
		if (item != null) {
			this.owner = user;
			this.petLockerSN = item.getCashItemSN();
			this.template = PetTemplate.getPetTemplate(item.getItemID());
			this.curPos.setLocation(user.getCurrentPosition());
			this.curPos.y -= 20;
			this.name = item.getPetName();
			onEnterField(user.getField());
			return !isDead();
		}
		return false;
	}
	
	public boolean isDead() {
		ItemSlotPet item = getItemSlot();
		if (item != null) {
			return item.isDead();
		}
		return false;
	}
	
	public boolean isNamedPet() {
		return !getBasicName().equalsIgnoreCase(getName());
	}
	
	public void onEnterField(Field field) {
		this.field = field;
		this.curPos.setLocation(owner.getCurrentPosition());
		
		short sn = 0;
		StaticFoothold fh = field.getSpace2D().getFootholdUnderneath(curPos.x, curPos.y, null);
		if (fh != null) {
			sn = (short) fh.getSN();
		}
		
		this.moveAction = 0;
		this.footholdSN = sn;
	}
	
	private void onAction(InPacket packet) {
		byte action = packet.decodeByte();
		String chat = packet.decodeString();
		if (template.isValidAction(action)) {
			getField().splitSendPacket(getOwner().getSplit(), PetPacket.onAction(getOwner().getCharacterID(), action, chat), getOwner());
		}
	}
	
	public void onEatFood(int incRepleteness) {
		ItemSlotPet item = getItemSlot();
		if (item != null) {
			int repleteness = item.getRepleteness();
			int inc = incRepleteness;
			if (repleteness + incRepleteness > 100) {
				inc = 100 - repleteness;
			}
			item.setRepleteness(inc + repleteness);
			boolean update = inc > 0;
			remainHungriness = 1000 * (Rand32.getInstance().random() % 10 + overeat * overeat + 10);
			if (10 * inc / incRepleteness <= Rand32.getInstance().random() % 12 || item.getRepleteness() / 10 <= Rand32.getInstance().random() % 12) {
				if (inc == 0) {
					long rand;
					if (overeat == 10) {
						rand = Rand32.getInstance().random() % overeat;// = 0;
					} else {
						rand = Rand32.getInstance().random() % (10 - overeat);
					}
					if (rand != 0) {
						++overeat;
					} else {
						item.setTameness(Math.max(0, item.getTameness() - 1));
						overeat = 0;
						update = true;
					}
				}
			} else {
				incTameness(item, 1);
			}
			if (update) {
				Inventory.updatePetItem(getOwner(), getItemSlotPos());
			}
			getOwner().sendPacket(PetPacket.onActionCommand(getOwner().getCharacterID(), 0, true, (short) inc));
		}
	}
	
	private void onInteraction(InPacket packet) {
		boolean isNamedPet = true;
		if (!packet.decodeBool() || !isNamedPet()) {
			isNamedPet = false;
		}
		boolean inc = false;
		short tameness = 0;
		int interactionIdx = packet.decodeByte();
		if (interactionIdx >= 0 && !template.getInteractions().isEmpty() && interactionIdx <= template.getInteractions().size()) {
			PetInteraction interaction = template.getInteractions().get(interactionIdx);
			if (interaction != null) {
				ItemSlotPet item = getItemSlot();
				if (item != null) {
					long time = System.currentTimeMillis();
					long delay = time - lastInteraction;
					if (item.getLevel() - interaction.getLevelRange().getLow() >= 0 && interaction.getLevelRange().getHigh() > item.getLevel() && delay > 15000) {
						double rate = 1.0;
						if (isNamedPet) {
							rate = 1.5;
						}
						lastInteraction = time;
						double prob = ((double)((delay - 15000) / 10000) * 0.01 + 1.0) * rate;
						if (Rand32.getInstance().random() % 100 < (int)((double)interaction.getProb() * prob) && item.getRepleteness() > 50) {
							tameness = (short) incTameness(item, interaction.getFriendnessInc());
							if (Inventory.updatePetItem(getOwner(), getItemSlotPos())) {
								inc = true;
							} else {
								incTameness(item, -tameness);
							}
						}
					}
				}
			}
		}
		getOwner().sendPacket(PetPacket.onActionCommand(getOwner().getCharacterID(), interactionIdx, inc, tameness));
	}
	
	private void onMove(InPacket packet) {
		MovePath mp = new MovePath();
		mp.decode(packet);
		if (!mp.getElem().isEmpty()) {
			MovePath.Elem tail = mp.getElem().getLast();
			if (curPos.x != tail.getX() || curPos.y != tail.getY()) {
				remainHungriness -= 500;
			}
			setMovePosition(tail.getX(), tail.getY(), tail.getMoveAction(), tail.getFh());
		}
		getField().splitSendPacket(getOwner().getSplit(), PetPacket.onMove(getOwner().getCharacterID(), mp), getOwner());
		mp.getElem().clear();
	}
	
	public void onPacket(byte type, InPacket packet) {
		switch (type) {
			case ClientPacket.PetMove:
				onMove(packet);
				break;
			case ClientPacket.PetActionRequest:
				onAction(packet);
				break;
			case ClientPacket.PetInteractionRequest:
				onInteraction(packet);
				break;
		}
	}
	
	public void setMovePosition(int x, int y, byte moveAction, short sn) {
		this.curPos.setLocation(x, y);
		this.moveAction = moveAction;
		this.footholdSN = sn;
	}
	
	public boolean update(long time) {
		this.remainHungriness -= time - lastUpdated;
		this.lastUpdated = time;
		
		boolean remove;
		boolean update = false;
		ItemSlotPet item = getItemSlot();
		if (this.remainHungriness < 0) {
			int hunger = 6 * template.getHungry();
			long rand;
			if (hunger == 36) {
				rand = Rand32.getInstance().random() % hunger;
			} else {
				rand = Rand32.getInstance().random() % (36 - hunger) + 60;
			}
			this.remainHungriness = 1000 * rand;
			item.setRepleteness(item.getRepleteness() - 1);
			update = true;
		}
		if (item.getRepleteness() != 0) {
			remove = false;
		} else {
			item.setTameness(Math.max(0, item.getTameness() - 1));
			item.setRepleteness(5);
			update = true;
			remove = true;
		}
		if (update) {
			Inventory.updatePetItem(getOwner(), getItemSlotPos());
		}
		return remove;
	}
}
