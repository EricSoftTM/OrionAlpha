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
package game.miniroom.shop;

import common.item.ItemSlotBase;

/**
 *
 * @author Eric
 */
public class Item {
	private byte ti;
	private short pos, number, set;
	private int price;
	private ItemSlotBase item;
	
	public Item(byte ti, short pos, short count, short set, int price, ItemSlotBase item) {
		this.ti = ti;
		this.pos = pos;
		this.number = count;
		this.set = set;
		this.price = price;
		this.item = item;
	}
	
	public byte getTI() {
		return ti;
	}
	
	public short getPos() {
		return pos;
	}
	
	public short getNumber() {
		return number;
	}
	
	public short getSet() {
		return set;
	}
	
	public int getPrice() {
		return price;
	}
	
	public ItemSlotBase getItemSlot() {
		return item;
	}
	
	public void setNumber(int count) {
		this.number = (short) count;
	}
}
