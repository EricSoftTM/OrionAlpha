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

import common.item.ItemSlotBase;
import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import game.GameApp;

/**
 *
 * @author Eric
 */
public class Inventory {
    
    /**
     * Global way to update an item's serial number.
     * 
     * @param item The object of the item to update SN
     * @param cash Whether or not to increment CashSN or SN
     * @return The item's new SN assigned
     */
    public static final long getNextSN(ItemSlotBase item, boolean cash) {
        if (cash) {
            item.setCashItemSN(GameApp.getInstance().getNextCashSN());
            return item.getCashItemSN();
        } else {
            if (item instanceof ItemSlotBundle) {
                ((ItemSlotBundle) item).setItemSN(GameApp.getInstance().getNextSN());
            } else if (item instanceof ItemSlotEquip) {
                ((ItemSlotEquip) item).setItemSN(GameApp.getInstance().getNextSN());
            } else if (item instanceof ItemSlotBase) {
                return GameApp.getInstance().getNextSN();
            }
            return item.getSN();
        }
    }
}
