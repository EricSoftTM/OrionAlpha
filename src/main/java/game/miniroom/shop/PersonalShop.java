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

import common.Request;
import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import common.user.CharacterStat.CharacterStatType;
import game.field.Field;
import game.miniroom.CloseType;
import game.miniroom.MiniRoomBase;
import game.miniroom.MiniRoomBaseDlg;
import game.miniroom.MiniRoomEnter;
import game.miniroom.MiniRoomLeave;
import game.miniroom.MiniRoomPacket;
import game.miniroom.MiniRoomType;
import game.user.User;
import game.user.item.ChangeLog;
import game.user.item.ExchangeElem;
import game.user.item.Inventory;
import network.packet.InPacket;
import network.packet.OutPacket;
import util.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalShop extends MiniRoomBase {
	private static final byte SlotCount = 16;
	
	private final List<Item> items;
	
	public PersonalShop() {
		super(4);
		
		this.items = new ArrayList<>();
	}
	
	private void checkNoMoreItem() {
		for (Item item : items) {
			if (item.getNumber() != 0) {
				return;
			}
		}
		closeRequest(null, MiniRoomLeave.NoMoreItem, MiniRoomLeave.UserRequest);
	}
	
	private void closeShop(Field field) {
		field.removeBalloon(getBalloonSN());
	}
	
	private byte doTransaction(User user, byte slot, Item item, short number) {
		if (user.lock()) {
			try {
				User owner = getUsers().get(0);
				byte ti = item.getTI();
				short pos = item.getPos();
				ItemSlotBase itemSlot = owner.getCharacter().getItem(ti, pos);
				if (itemSlot == null) {
					return PersonalShopBuy.Unknown;//DeniedUser
				}
				long meso = number * item.getPrice();
				long money = (owner.getCharacter().getCharacterStat().getMoney() - owner.getCharacter().getMoneyTrading()) + meso;
				if ((money >> 32) != 0 || (int)money < 0) {
					return PersonalShopBuy.HostTooMuchMoney;
				}
				if ((meso >> 32) != 0 || (int)meso < 0) {
					return PersonalShopBuy.OverPrice;
				}
				if (user.getCharacter().getCharacterStat().getMoney() - user.getCharacter().getMoneyTrading() < (int)meso) {
					return PersonalShopBuy.NoMoney;
				}
				
				List<List<List<ItemSlotBase>>> backupItem = new ArrayList<>();
				List<List<List<Integer>>> backupTrading = new ArrayList<>();
				for (int i = 0; i < 2; i++) {
					backupItem.add(i, new ArrayList<>());
					backupTrading.add(i, new ArrayList<>());
				}
				owner.getCharacter().backupItemSlot(backupItem.get(0), backupTrading.get(0));
				user.getCharacter().backupItemSlot(backupItem.get(1), backupTrading.get(1));
				
				List<List<ExchangeElem>> exchanges = new ArrayList<>();
				for (int i = 0; i < 2; i++) {
					exchanges.add(i, new ArrayList<>());
				}
				
				ExchangeElem elem = new ExchangeElem();
				elem.add = false;
				elem.r.itemID = 0;
				elem.r.ti = ti;
				elem.r.pos = pos;
				int trading = owner.getCharacter().getItemTrading().get(ti).get(pos);
				if (ItemAccessor.isTreatSingly(itemSlot)) {
					elem.r.count = 1;
					exchanges.get(0).add(elem);
					elem = new ExchangeElem();
					elem.initAdd(0, (short) 0, itemSlot);
					exchanges.get(1).add(elem);
					--trading;
				} else {
					elem.r.count = (short) (number * item.getSet());
					exchanges.get(0).add(elem);
					elem = new ExchangeElem();
					elem.initAdd(itemSlot.getItemID(), (short) (number * item.getSet()), null);
					exchanges.get(1).add(elem);
					trading -= number * item.getSet();
				}
				owner.getCharacter().getItemTrading().get(ti).set(pos, trading);
				
				List<ChangeLog> logAdd = new ArrayList<>();
				List<ChangeLog> changeLog = new ArrayList<>();
				boolean exchange = true;
				if (trading < 0) {
					exchange = false;
				}
				if (exchange) {
					if (!Inventory.exchange(owner, 0, exchanges.get(0), logAdd, null) || !Inventory.exchange(user, 0, exchanges.get(1), changeLog, null)) {
						exchange = false;
					}
				}
				if (exchange) {
					user.flushCharacterData(0, true);
					owner.flushCharacterData(0, true);
				}
				if (exchange) {
					int price = (int)meso;
					int tax = getTax(price);
					owner.incMoney(price - tax, true, false);
					user.incMoney(-price, true, false);
					owner.sendCharacterStat(Request.None, CharacterStatType.Money);
					user.sendCharacterStat(Request.None, CharacterStatType.Money);
					Inventory.sendInventoryOperation(user, Request.Excl, changeLog);
				} else {
					owner.getCharacter().restoreItemSlot(backupItem.get(0), backupTrading.get(0));
					user.getCharacter().restoreItemSlot(backupItem.get(1), backupTrading.get(1));
				}
				
				// extensive cleanup here
				logAdd.clear();
				changeLog.clear();
				for (int i = 0; i < 2; i++) {
					exchanges.get(i).clear();
					for (List<ItemSlotBase> backup : backupItem.get(i)) {
						backup.clear();
					}
					backupItem.get(i).clear();
					for (List<Integer> backup : backupTrading.get(i)) {
						backup.clear();
					}
					backupTrading.get(i).clear();
				}
				backupItem.clear();
				backupTrading.clear();
				return exchange ? PersonalShopBuy.Success : PersonalShopBuy.NoSlot;
			} finally {
				user.unlock();
			}
		}
		return PersonalShopBuy.Unknown;
	}
	
	@Override
	public void encodeEnterResult(User user, OutPacket packet) {
		packet.encodeString(getTitle());
		encodeItemList(packet);
	}
	
	public void encodeItemList(OutPacket packet) {
		packet.encodeByte(items.size());
		for (Item item : items) {
			packet.encodeShort(item.getNumber());
			packet.encodeShort(item.getSet());
			packet.encodeInt(item.getPrice());
			packet.encodeByte(item.getItemSlot().getType());
			item.getItemSlot().encode(packet);
		}
	}
	
	@Override
	public int getCloseType() {
		return CloseType.Host;
	}
	
	@Override
	public int getTypeNumber() {
		return MiniRoomType.PersonalShop;
	}
	
	@Override
	public int isAdmitted(User user, InPacket packet, boolean onCreate) {
		int result = super.isAdmitted(user, packet, onCreate);
		if (result == MiniRoomEnter.Success) {
			if (onCreate) {
				short pos = packet.decodeShort();
				int itemID = packet.decodeInt();
				ItemSlotBase item = user.getCharacter().getItem(ItemType.Etc, pos);
				if (item == null || item.getItemID() != itemID) {
					return MiniRoomEnter.Etc;
				}
				Point host = new Point(packet.decodeInt(), packet.decodeInt());
				if (!user.getField().checkBalloonAvailable(host)) {
					return MiniRoomEnter.ExistMiniRoom;
				}
				setBalloonSN(user.getField().setBalloon(host));
			}
			if (!user.getCharacter().setTrading(true)) {
				return MiniRoomEnter.NoTrading;
			}
		}
		return result;
	}
	
	private void moveItemToShop(ItemSlotBase item, User user, byte ti, short pos, short number, int itemID) {
		Inventory.moveItemToTemp(user, ti, pos, number);
	}
	
	private void onBuyItem(User user, InPacket packet) {
		byte pos = packet.decodeByte();
		short number = packet.decodeShort();
		byte slot = findUserSlot(user);
		byte result;
		if (getCurUsers() == 0 || !isOpened() || slot <= 0 || pos < 0 || pos >= SlotCount) {
			result = PersonalShopBuy.Unknown;
		} else if (items.get(pos).getNumber() < number || number <= 0) {
			result = PersonalShopBuy.NoStock;
		} else {
			result = doTransaction(user, slot, items.get(pos), number);
		}
		if (result != PersonalShopBuy.Success) {
			user.sendCharacterStat(Request.Excl, 0);
			user.sendPacket(MiniRoomBaseDlg.onBuyResult(result));
			return;
		}
		registerSoldItem(user, pos, number, items.get(pos).getSet());
		broadCast(MiniRoomBaseDlg.onRefresh(this), null);
		if (user.getLevel() <= 15) {
			user.setTradeMoneyLimit(user.getTradeMoneyLimit() + user.getTempTradeMoney());
		}
		checkNoMoreItem();
	}
	
	@Override
    public void onLeave(User user, int leaveType) {
		if (findUserSlot(user) == 0) {
			Inventory.restoreFromTemp(user);
			closeShop(user.getField());
		}
		user.getCharacter().setTrading(false);
    }
	
	@Override
	public void onPacket(int type, User user, InPacket packet) {
		switch (type) {
			case MiniRoomPacket.PutItem_PS:
				onPutItem(user, packet);
				break;
			case MiniRoomPacket.BuyItem:
				onBuyItem(user, packet);
				break;
		}
	}
	
	private void onPutItem(User user, InPacket packet) {
		byte ti = packet.decodeByte();
		short pos = packet.decodeShort();
		short count = packet.decodeShort();
		short set = packet.decodeShort();
		int price = packet.decodeInt();
		if (getCurUsers() == 0 || set == 0 || (isOpened() || findUserSlot(user) > 0) || items.size() == SlotCount) {
			user.sendCharacterStat(Request.Excl, 0);
			return;
		}
		ItemSlotBase item = user.getCharacter().getItem(ti, pos);
		if (item == null || ItemAccessor.isTreatSingly(item) && (count != 1 || set != 1)) {
			user.sendCharacterStat(Request.Excl, 0);
			return;
		}
		if (ItemAccessor.isBundleTypeIndex(ti) && count > item.getItemNumber()) {
			Logger.logError("PersonalShop Invalid Number:[%d][%d][%d]", item.getItemID(), count, item.getItemNumber());
			return;
		}
		moveItemToShop(item, user, ti, pos, (short) (count * set), item.getItemID());
		items.add(new Item(ti, pos, count, set, price, item.makeClone()));
		user.sendPacket(MiniRoomBaseDlg.onRefresh(this));
	}
	
	private void registerSoldItem(User user, byte idx, short number, short set) {
		Item item = items.get(idx);
		item.setNumber(item.getNumber() - number);
		//getUsers().get(0).sendPacket(MiniRoomBaseDlg.onSoldItemResult(idx, number, user.getCharacterName()));
	}
	
	// Not sure if this even exists yet tbh.
	private static int getTax(int money) {
		return 0;
	}
}
