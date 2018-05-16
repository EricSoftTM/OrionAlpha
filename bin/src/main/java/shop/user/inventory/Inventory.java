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
package shop.user.inventory;

import common.item.ItemSlotBase;
import common.item.ItemSlotBundle;
import common.item.ItemSlotEquip;
import common.item.ItemType;
import game.user.WvsContext;
import shop.ShopApp;
import shop.user.User;

/**
 *
 * @author sunnyboy
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
            item.setCashItemSN(ShopApp.getInstance().getNextCashSN());
            return item.getCashItemSN();
        } else {
            if (item instanceof ItemSlotBundle) {
                ((ItemSlotBundle) item).setItemSN(ShopApp.getInstance().getNextSN());
            } else if (item instanceof ItemSlotEquip) {
                ((ItemSlotEquip) item).setItemSN(ShopApp.getInstance().getNextSN());
            } else if (item instanceof ItemSlotBase) {
                return ShopApp.getInstance().getNextSN();
            }
            return item.getSN();
        }
    }

    public static boolean incItemSlotCount(User user, byte ti, byte inc) {
        if (ti <= ItemType.NotDefine || ti > ItemType.NO) {
            return false;
        }
        if (user.lock()) {
            try {
                int slotCount = user.getCharacter().getItemSlotCount(ti);
                int slotInc = slotCount + inc;
                if (slotInc >= 80) {
                    slotInc = 80;
                }
                if (slotCount >= slotInc) {
                    return false;
                }
                for (int i = 0; i < inc; i++) {
                    user.getCharacter().getItemSlot(ti).add(null);
                    user.getCharacter().getItemTrading().get(ti).add(0);
                }
            //    user.sendPacket(WvsContext.onInventoryGrow(ti, slotInc));
                return true;
            } finally {
                user.unlock();
            }
        }
        return false;
    }
}
