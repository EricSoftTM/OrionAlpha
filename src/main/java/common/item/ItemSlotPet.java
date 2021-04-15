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
package common.item;

import network.packet.OutPacket;
import util.FileTime;

/**
 *
 * @author Eric
 */
public class ItemSlotPet extends ItemSlotBase {
	private String name;
	private byte level;
	private short tameness;
	private byte repleteness;
	private FileTime dateDead;
	
	public ItemSlotPet(int itemID) {
		super(itemID);
		this.dateDead = FileTime.END.makeClone();
		this.level = 1;
		this.repleteness = 100;
		this.name = "";
	}
	
	public FileTime getDateDead() {
		return dateDead;
	}
	
	@Override
	public short getItemNumber() {
		return 1;
	}
	
	public byte getLevel() {
		return level;
	}
	
	public String getPetName() {
		return name;
	}
	
	public byte getRepleteness() {
		return repleteness;
	}
	
	@Override
	public long getSN() {
		return getCashItemSN();
	}
	
	public short getTameness() {
		return tameness;
	}
	
	@Override
	public int getType() {
		return ItemSlotType.Pet;
	}
	
	@Override
	public ItemSlotBase makeClone() {
		ItemSlotPet item = (ItemSlotPet) createItem(ItemSlotType.Pet);
		item.setItemID(this.getItemID());
		item.setCashItemSN(this.getCashItemSN());
		item.setDateExpire(this.getDateExpire());
		item.setPetName(this.getPetName());
		item.setLevel(this.getLevel());
		item.setTameness(this.getTameness());
		item.setRepleteness(this.getRepleteness());
		item.setDateDead(this.getDateDead());
		return item;
	}
	
	@Override
	public void rawEncode(OutPacket packet) {
		super.rawEncode(packet);
		packet.encodeString(name, 13);
		packet.encodeByte(level);
		packet.encodeShort(tameness);
		packet.encodeByte(repleteness);
		packet.encodeFileTime(dateDead);
	}
	
	public void setDateDead(FileTime dateDead) {
		this.dateDead = dateDead;
	}
	
	@Override
	public void setItemNumber(int number) {
	
	}
	
	public void setLevel(int level) {
		this.level = (byte) level;
	}
	
	public void setPetName(String name) {
		this.name = name;
	}
	
	public void setRepleteness(int repleteness) {
		this.repleteness = (byte) repleteness;
	}
	
	public void setTameness(int tameness) {
		this.tameness = (short) tameness;
	}
}
