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
package game.field.life.npc;

import common.item.ItemAccessor;
import game.user.User;
import game.user.item.BundleItem;
import game.user.item.ItemInfo;
import game.user.skill.SkillInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.packet.OutPacket;
import util.Logger;
import util.wz.WzFileSystem;
import util.wz.WzNodeType;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzSAXProperty;
import util.wz.WzUtil;
import util.wz.WzXML;

/**
 *
 * @author Eric
 */
public class NpcTemplate implements WzXML {
    private static final WzPackage npcDir = new WzFileSystem().init("Npc").getPackage();
    private static final Map<Integer, NpcTemplate> templates = new HashMap<>();
    private static final Lock lockNpc = new ReentrantLock();
    private int templateID;
    private String name;
    private String quest;
    private boolean move;
    private boolean isShop;
    private final List<ShopItem> shopItem;
    
    public NpcTemplate() {
        this.name = "";
        this.quest = "";
        this.shopItem = new ArrayList<>();
    }
    
    public static NpcTemplate getNpcTemplate(int templateID) {
        lockNpc.lock();
        try {
            return templates.get(templateID);
        } finally {
            lockNpc.unlock();
        }
    }
    
    public static void load() {
        for (WzProperty npcData : npcDir.getEntries().values()) {
            registerNpc(Integer.parseInt(npcData.getNodeName().replaceAll(".img", "")), npcData);
        }
    }
    
    private static void registerNpc(int templateID, WzProperty prop) {
        WzProperty info = prop.getNode("info");
        if (info == null) {
            return;
        }
        NpcTemplate template = new NpcTemplate();
        template.templateID = templateID;
        template.name = WzUtil.getString(info.getNode("name"), "NULL");
        template.quest = WzUtil.getString(info.getNode("quest"), null);
        template.move = prop.getNode("move") != null;
        
        WzProperty shop = info.getNode("shop");
        if (shop != null) {
            for (WzProperty items : shop.getChildNodes()) {
                int itemID = Integer.parseInt(items.getNodeName());
                int price = WzUtil.getInt32(items.getNode("price"), 0);
                int stock = WzUtil.getInt32(items.getNode("stock"), 0);
                int period = WzUtil.getInt32(items.getNode("period"), 24);
                double unitPrice = WzUtil.getDouble(items.getNode("unitPrice"), 0.0d);
                
                if (!ItemAccessor.isRechargeableItem(itemID) && price == 0) {
                    Logger.logError("No Price Information in Shop List (NPC ID : %d, Item ID : %d)", templateID, itemID);
                    continue;
                }
                
                template.shopItem.add(new ShopItem(itemID, price, stock, period, unitPrice));
            }
        }
        
        templates.put(templateID, template);
    }
    
    private static void registerNpc(int templateID, WzSAXProperty prop) {
        NpcTemplate template = new NpcTemplate();
        template.templateID = templateID;
        
        prop.addEntity(template);
        prop.parse();
        
        templates.put(templateID, template);
    }
    
    public static void unload() {
        templates.clear();
    }
    
    public void encodeShop(User user, OutPacket packet) {
        packet.encodeInt(templateID);
        packet.encodeShort(shopItem.size());
        for (ShopItem item : shopItem) {
            packet.encodeInt(item.itemID);
            packet.encodeInt(item.price);
            int maxPerSlot;
            if (ItemAccessor.isRechargeableItem(item.itemID)) {
                packet.encodeDouble(item.unitPrice);
                maxPerSlot = SkillInfo.getInstance().getBundleItemMaxPerSlot(item.itemID, user.getCharacter());
            } else {
                BundleItem info = ItemInfo.getBundleItem(item.itemID);
                if (info == null) {
                    maxPerSlot = 1;
                } else {
                    maxPerSlot = info.getSlotMax();
                }
            }
            packet.encodeShort(maxPerSlot);
        }
    }
    
    public int getTemplateID() {
        return templateID;
    }
    
    public String getName() {
        return name;
    }
    
    public String getQuest() {
        return quest;
    }
    
    public List<ShopItem> getShopItem() {
        return shopItem;
    }
    
    public boolean isMove() {
        return move;
    }
    
    @Override
    public void parse(String root, String name, String value, WzNodeType type) {
        if (type.equals(WzNodeType.STRING)) {
            switch (name) {
                case "name":
                    this.name = value;
                    if (this.name == null) {
                        this.name = "NULL";
                    }
                    break;
                case "quest":
                    this.quest = value;
                    break;
            }
        } else if (type.equals(WzNodeType.IMGDIR)) {
            switch (name) {
                case "move":
                    this.move = true;
                    break;
                case "shop":
                    this.isShop = true;
                    break;
                default: {
                    if (this.isShop) {
                        if (name.matches("[0-9]+") && name.length() >= 7) {
                            this.shopItem.add(new ShopItem(Integer.parseInt(name)));
                        }
                    }
                }
            }
        } else if (type.equals(WzNodeType.INT)) {
            if (!this.shopItem.isEmpty()) {
                ShopItem item = this.shopItem.get(this.shopItem.size() - 1);
                switch (name) {
                    case "price":
                        item.price = WzUtil.getInt32(value, 0);
                        if (!ItemAccessor.isRechargeableItem(item.itemID) && item.price == 0) {
                            Logger.logError("No Price Information in Shop List (NPC ID : %d, Item ID : %d)", this.templateID, item.itemID);
                        }
                        break;
                    case "stock":
                        item.stock = WzUtil.getInt32(value, 0);
                        break;
                    case "period":
                        item.period = WzUtil.getInt32(value, 24);
                        break;
                    case "unitPrice":
                        item.unitPrice = WzUtil.getDouble(value, 0.0d);
                        break;
                }
            }
        }
    }
}
