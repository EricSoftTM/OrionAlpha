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
package game.user.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import util.Logger;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 * @author Eric
 * @author Arnah
 */
public class ItemInfo {

	private static final WzPackage characterDir = new WzFileSystem().init("Character").getPackage();
	private static final WzPackage itemDir = new WzFileSystem().init("Item").getPackage();
	private static final WzPackage fieldDir = new WzFileSystem().init("Map/Map").getPackage();
	protected static Map<Integer, BundleItem> bundleItem = new HashMap<>();
	protected static Map<Integer, EquipItem> equipItem = new HashMap<>();
	protected static Map<Integer, StateChangeItem> statChangeItem = new HashMap<>();
	protected static Map<Integer, PortalScrollItem> portalScrollItem = new HashMap<>();
	protected static Map<Integer, UpgradeItem> upgradeItem = new HashMap<>();
	//
	protected static Map<Integer, String> mapString = new HashMap<>();
	protected static Map<Integer, String> itemString = new HashMap<>();

	public static BundleItem getBundleItem(int itemID){
		return bundleItem.get(itemID);
	}

	public static EquipItem getEquipItem(int itemID){
		return equipItem.get(itemID);
	}

	public static StateChangeItem getStateChangeItem(int itemID){
		return statChangeItem.get(itemID);
	}

	public static PortalScrollItem getPortalScrollItem(int itemID){
		return portalScrollItem.get(itemID);
	}

	public static UpgradeItem getUpgradeItem(int itemID){
		return upgradeItem.get(itemID);
	}

	public static String getItemName(int itemID){
		return itemString.get(itemID);
	}

	public static String getMapName(int mapID){
		return mapString.get(mapID);
	}

	public static int getBulletPAD(int itemID){
		BundleItem item = getBundleItem(itemID);
		if(item == null) return 0;
		else return item.incPAD;
	}

	public static boolean isCashItem(int itemID){
		if(itemID / 1000000 == 5) return true;
		BundleItem item = getBundleItem(itemID);
		if(item != null){
			return item.cash;
		}else return false;
	}

	public static void load(){
		Logger.logReport("Loading Equip Info");
		for(Entry<String, WzPackage> category : characterDir.getChildren().entrySet()){
			if(category.getKey().equals("Afterimage")) continue;
			// int size = equipItem.size();
			for(WzProperty itemData : category.getValue().getEntries().values()){
				registerEquipItemInfo(itemData);
			}
			// Logger.logReport("%d Item(s) loaded successfully from [%S]", equipItem.size() - size, category.getKey());
		}

		Logger.logReport("Loading Bundle Info");
		iterateBundleItem();
		iterateMapString();
	}

	private static void registerEquipItemInfo(WzProperty itemData){
		EquipItem item = new EquipItem();
		item.itemID = Integer.parseInt(itemData.getNodeName().replaceAll(".img", ""));
		//
		WzProperty info = itemData.getNode("info");
		if(info != null){
			item.itemName = WzUtil.getString(info.getNode("name"), "NULL");
			itemString.put(item.itemID, item.itemName);
			item.reqSTR = WzUtil.getInt32(info.getNode("reqSTR"), 0);
			item.reqDEX = WzUtil.getInt32(info.getNode("reqDEX"), 0);
			item.reqINT = WzUtil.getInt32(info.getNode("reqINT"), 0);
			item.reqLUK = WzUtil.getInt32(info.getNode("reqLUK"), 0);

			item.sellPrice = WzUtil.getInt32(info.getNode("price"), 0);
			item.cash = WzUtil.getBoolean(info.getNode("cash"), false);

			item.incSTR = WzUtil.getShort(info.getNode("incSTR"), (short)0);
			item.incDEX = WzUtil.getShort(info.getNode("incDEX"), (short)0);
			item.incINT = WzUtil.getShort(info.getNode("incINT"), (short)0);
			item.incLUK = WzUtil.getShort(info.getNode("incLUK"), (short)0);
			item.incMaxHP = WzUtil.getShort(info.getNode("incMHP"), (short)0);
			item.incMaxMP = WzUtil.getShort(info.getNode("incMMP"), (short)0);

			item.incPAD = WzUtil.getShort(info.getNode("incPAD"), (short)0);
			item.incMAD = WzUtil.getShort(info.getNode("incMAD"), (short)0);
			item.incPDD = WzUtil.getShort(info.getNode("incPDD"), (short)0);
			item.incMDD = WzUtil.getShort(info.getNode("incMDD"), (short)0);

			item.incACC = WzUtil.getShort(info.getNode("incACC"), (short)0);
			item.incEVA = WzUtil.getShort(info.getNode("incEVA"), (short)0);
			item.incCraft = WzUtil.getShort(info.getNode("incCraft"), (short)0);
			item.incSpeed = WzUtil.getShort(info.getNode("incSpeed"), (short)0);
			item.incJump = WzUtil.getShort(info.getNode("incJump"), (short)0);
			item.incSwim = WzUtil.getShort(info.getNode("incSwim"), (short)0);

			item.knockback = WzUtil.getInt32(info.getNode("knockback"), 0);
			item.attackSpeed = WzUtil.getInt32(info.getNode("attackSpeed"), 0);
			item.tuc = WzUtil.getInt32(info.getNode("tuc"), 0);
			// vslot, iconRaw, tuc, sfx, incMDD, icon, reqLUK, reqLevel, knockback, reqDEX, incJump, price, attack, incINT, islot, incSTR, incPDD, stand, cash, incMHP, reqPOP, afterImage, incACC, incLUK, nameTag, incMMD, incDEX, reqJob, chatBalloon, incSpeed, attackSpeed, name, incEVA, incMMP, incMAD, incPAD, reqINT, walk, reqSTR, desc

		}
		//
		equipItem.put(item.itemID, item);
	}

	private static void iterateBundleItem(){
		String[] category = {"Consume", "Etc"};
		for(String cat : category){
			WzPackage pack = itemDir.getChildren().get(cat);
			for(WzProperty itemSection : pack.getEntries().values()){
				for(WzProperty itemData : itemSection.getChildNodes()){
					loadBundleItem(itemData);
				}
			}
		}
	}

	private static void loadBundleItem(WzProperty itemData){
		BundleItem item = new BundleItem();
		item.itemID = Integer.parseInt(itemData.getNodeName().replaceAll(".img", ""));
		WzProperty info = itemData.getNode("info");
		if(info != null){
			item.itemName = WzUtil.getString(info.getNode("name"), "NULL");
			itemString.put(item.itemID, item.itemName);
			item.incPAD = WzUtil.getShort(info.getNode("incPAD"), (short) 0);
			item.sellPrice = WzUtil.getInt32(info.getNode("price"), 0);
			item.unitPrice = WzUtil.getDouble(info.getNode("unitPrice"), 0);
			item.cash = WzUtil.getBoolean(info.getNode("cash"), false);
			item.slotMax = WzUtil.getInt32(info.getNode("slotMax"), 0);

			// unitPrice, iconRaw, incMDD, icon, incACC, slotMax, incLUK, incDEX, incJump, price, success, incSpeed, name, incINT, incSTR, incPDD, incMAD, incEVA, incPAD, cash, incMHP, desc
		}
		int type = item.itemID / 10000;

		switch (type){
			case 200:
			case 201:
			case 202:
			case 205:
				registerStateChangeItem(item.itemID, itemData);
				break;
			case 203:
				registerPortalScrollItem(item.itemID, itemData);
				break;
			case 204:
				registerUpgradeItem(item.itemID, itemData);
				break;
			// All this shit is useless.
			case 206:// arrows
			case 207:// throwing star
			case 208:// mega phones
			case 209:// weather effect? idk
				break;
			case 400:
			case 401:
			case 402:
			case 403:
			case 404:
			case 405:
				break;
		}
		bundleItem.put(item.itemID, item);
	}

	private static void registerStateChangeItem(int itemid, WzProperty itemData){
		StateChangeItem item = new StateChangeItem();
		item.itemID = itemid;
		WzProperty specEx = itemData.getNode("specEx");
		if(specEx == null){
			loadStateChangeInfo(item, itemData);
		}else{
			// Nice joke
		}
		statChangeItem.put(item.itemID, item);
	}

	private static void loadStateChangeInfo(StateChangeItem sci, WzProperty itemData){
		WzProperty spec = itemData.getNode("spec");
		if(spec != null){
			//acc, eva, mad, pdd, pad, mp, hp, hpR, time, pda, mpR, speed
			sci.hp =  WzUtil.getInt32(spec.getNode("hp"), 0);
			sci.mp =  WzUtil.getInt32(spec.getNode("mp"), 0);
			sci.hpR =  WzUtil.getInt32(spec.getNode("hpR"), 0);
			sci.mpR =  WzUtil.getInt32(spec.getNode("mpR"), 0);

			sci.acc =  WzUtil.getInt32(spec.getNode("acc"), 0);
			sci.eva =  WzUtil.getInt32(spec.getNode("eva"), 0);
			sci.mad =  WzUtil.getInt32(spec.getNode("mad"), 0);
			sci.pdd =  WzUtil.getInt32(spec.getNode("pdd"), 0);
			sci.pad =  WzUtil.getInt32(spec.getNode("pad"), 0);

			sci.speed =  WzUtil.getInt32(spec.getNode("speed"), 0);

			sci.time =  WzUtil.getInt32(spec.getNode("time"), 0);
		}
	}

	private static void registerPortalScrollItem(int itemid, WzProperty itemData){
		PortalScrollItem item = new PortalScrollItem();
		item.itemID = itemid;
		WzProperty spec = itemData.getNode("spec");
		if(spec != null){
			item.moveTo = WzUtil.getInt32(spec.getNode("moveTo"), 999999999);
		}
		portalScrollItem.put(item.itemID, item);
	}

	private static void registerUpgradeItem(int itemid, WzProperty itemData){
		UpgradeItem item = new UpgradeItem();
		item.itemID = itemid;
		WzProperty info = itemData.getNode("info");
		if(info != null){
			item.incMaxHP = WzUtil.getShort(info.getNode("incMHP"), (short) 0);

			item.incSTR = WzUtil.getShort(info.getNode("incSTR"), (short) 0);
			item.incDEX = WzUtil.getShort(info.getNode("incDEX"), (short) 0);
			item.incINT = WzUtil.getShort(info.getNode("incINT"), (short) 0);
			item.incLUK = WzUtil.getShort(info.getNode("incLUK"), (short) 0);

			item.incACC = WzUtil.getShort(info.getNode("incACC"), (short) 0);
			item.incEVA = WzUtil.getShort(info.getNode("incEVA"), (short) 0);

			item.incSpeed = WzUtil.getShort(info.getNode("incSpeed"), (short) 0);
			item.incJump = WzUtil.getShort(info.getNode("incJump"), (short) 0);

			item.incPAD = WzUtil.getShort(info.getNode("incPAD"), (short) 0);
			item.incPDD = WzUtil.getShort(info.getNode("incPDD"), (short) 0);
			item.incMAD = WzUtil.getShort(info.getNode("incMAD"), (short) 0);
			item.incMDD = WzUtil.getShort(info.getNode("incMDD"), (short) 0);

			item.success = WzUtil.getByte(info.getNode("success"), (byte) 0);
		}
		upgradeItem.put(item.itemID, item);
	}

	private static void iterateMapString(){
		for(WzProperty map : fieldDir.getEntries().values()){
			int mapid = Integer.parseInt(map.getNodeName().replace(".img", ""));
			WzProperty info = map.getNode("info");
			if(info != null) {
				mapString.put(mapid, WzUtil.getString(info.getNode("mapName"), "NULL"));
			}
		}
	}
}
