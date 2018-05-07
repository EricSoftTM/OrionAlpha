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

/**
 *
 * @author Eric
 */
public class ShopItem {
    public int itemID;
    public int price;
    public int stock;
    public int stockMax;
    public long lastFullStock;
    public int period;
    public int quantity;
    public double unitPrice;
    
    public ShopItem(int itemID, int price, int stock, int period, double unitPrice) {
        this.itemID = itemID;
        this.price = price;
        this.stockMax = stock;
        this.period = period;
        this.unitPrice = unitPrice;
        this.stock = 0;
        this.lastFullStock = 0;
    }
}
