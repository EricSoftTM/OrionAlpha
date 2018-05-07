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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import util.Logger;
import util.wz.WzFileSystem;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzUtil;

/**
 *
 * @author Eric
 */
public class NpcTemplate {
    private static final WzPackage npcDir = new WzFileSystem().init("Npc").getPackage();
    private static final Map<Integer, NpcTemplate> templates = new HashMap<>();
    private static final Lock lockNpc = new ReentrantLock();
    private int templateID;
    private String name;
    private String quest;
    private boolean move;
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
        template.quest = WzUtil.getString(info.getNode("quest"), "NULL");
        template.move = prop.getNode("move") != null;
        
        WzProperty shop = info.getNode("shop");
        if (shop != null) {
            for (WzProperty items : shop.getChildNodes()) {
                int itemID = Integer.parseInt(items.getNodeName());
                int price = WzUtil.getInt32(items.getNode("price"), 0);
                int stock = WzUtil.getInt32(items.getNode("stock"), 0);
                int period = WzUtil.getInt32(items.getNode("period"), 24);
                double unitPrice = WzUtil.getDouble(items.getNode("unitPrice"), 0.0d);
                
                if (itemID / 10000 != 207 && price == 0) {
                    Logger.logError("No Price Information in Shop List (NPC ID : %d, Item ID : %d)", templateID, itemID);
                    continue;
                }
                
                template.shopItem.add(new ShopItem(itemID, price, stock, period, unitPrice));
            }
        }
        
        templates.put(templateID, template);
    }
    
    public static void unload() {
        templates.clear();
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
}
