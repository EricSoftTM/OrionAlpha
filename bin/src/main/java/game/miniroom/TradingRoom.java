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
package game.miniroom;

import game.user.User;
import game.user.item.BundleItem;
import game.user.item.EquipItem;
import game.user.item.ItemInfo;
import java.util.ArrayList;
import java.util.List;
import network.packet.InPacket;

/**
 *
 * @author sunnyboy
 */
public class TradingRoom extends MiniRoomBase {

    public List<Boolean> boolLock = new ArrayList<>(2);
    public List<List<Item>> item = new ArrayList<>(2);

    public TradingRoom(int maxUsers) {
        super(maxUsers);
        for (int i = 0; i < 2; i++) {
            this.item.add(new ArrayList<>());
            for (int j = 0; j < 10; j++) {
                this.item.get(i).add(j, new Item());
            }
        }
        for (int i = 0; i < 2; i++) {
            this.boolLock.add(i, Boolean.FALSE);
        }
    }

    public int DoTrade() {
        return 0;
    }

    private void onTrade(User user, InPacket packet) {
        
    }

    @Override
    public int getCloseType() {
        return 2;// verify
    }

    public int getItemPrice(int itemID) {
        EquipItem equip;
        BundleItem bundle;
        if ((equip = ItemInfo.getEquipItem(itemID)) != null) {
            return equip.getSellPrice();
        } else if ((bundle = ItemInfo.getBundleItem(itemID)) != null) {
            return bundle.getSellPrice();
        }
        return 0;
    }

    public double getTax(int meso) {
        double tax;
        if (meso >= 10000000) {
            tax = meso * 0.04;
            return tax;
        }
        if (meso >= 5000000) {
            tax = meso * 0.03;
            return tax;
        }
        if (meso >= 1000000) {
            tax = meso * 0.02;
            return tax;
        }
        if (meso >= 100000) {
            tax = meso * 0.01;
            return tax;
        }
        if (meso >= 50000) {
            tax = meso * 0.005;
            return tax;
        }
        return 0;
    }

    @Override
    public int getTypeNumber() {
        return 3;//verify
    }
    
    @Override
    public int isAdmitted(User user, InPacket packet, boolean onCreate) {
        int admitted = super.isAdmitted(user, packet, onCreate);
        if (admitted == 0) {
            
        }
        return admitted;
    }

    @Override
    public void onLeave(User user, int leaveType) {

    }

    @Override
    public void onPacket(byte type, User user, InPacket packet) {

    }

    private void onPutItem(User user, InPacket packet) {

    }

    private void onPutMoney(User user, InPacket packet) {

    }

    public class Item {

        public int ti = 0;
        public int pos = 0;
        public int number = -1;

    }
}
