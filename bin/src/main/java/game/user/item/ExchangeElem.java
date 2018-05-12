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
import common.item.ItemType;

/**
 *
 * @author Eric
 * @author sunnyboy
 */
public class ExchangeElem {

    public boolean add;
    public Add a = new Add();
    public Remove r = new Remove();

    public class Add {

        public int itemID;
        public int count;
        public ItemSlotBase item;
    }

    public class Remove {

        public int itemID;
        public int count;
        public int ti;
        public int pos;
    }

    public boolean initAdd(int nItemID, int nCount, ItemSlotBase pItem) {
        this.add = true;
        this.a.itemID = nItemID;
        this.a.count = nCount;
        this.a.item = pItem;
        if (pItem != null) {
            nItemID = pItem.getItemID();
            nCount = pItem.getItemNumber();
        }
        int nTI = nItemID / 1000000;
        if (nTI == ItemType.Consume || nTI == ItemType.Install || nTI == ItemType.Etc) {
            if (nItemID / 10000 == 207) {
                if (nCount > 2000) {
                    return false;
                }
            } else {
                // should be private, verifty w/ eric to call getter and not var
                if (ItemInfo.getBundleItem(nItemID).slotMax < nCount) {
                    return false;
                }
            }
        }
        return true;
    }
}
