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
import common.user.CharacterData;
import java.util.Collections;
import java.util.List;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class InventoryManipulator {
    
    public static void InsertChangeLog(List<ChangeLog> a, byte change, byte ti, short pos, ItemSlotBase pi, short pos2, short number) {
        if (pi != null)
            pi = pi.makeClone();
        ChangeLog cl = new ChangeLog();
        cl.setChange(change);
        cl.setTI(ti);
        cl.setPOS(pos);
        cl.setItem(pi);
        cl.setPOS2(pos2);
        cl.setNumber(number);
        if (cl.getItem() != null && number > 0 && (ti == ItemType.Consume || ti == ItemType.Install || ti == ItemType.Etc))
            cl.getItem().setItemNumber(number);
        a.add(cl);
    }
    
    public static OutPacket makeInventoryOperation(byte onExclResult, List<ChangeLog> changeLog) {
        if (changeLog == null) {
            changeLog = Collections.EMPTY_LIST;
        }
        OutPacket packet = new OutPacket(LoopbackPacket.InventoryOperation);
        packet.encodeByte(onExclResult);
        packet.encodeByte(changeLog.size());
        for (ChangeLog change : changeLog) {
            packet.encodeByte(change.getChange());
            packet.encodeByte(change.getTI());
            packet.encodeShort(change.getPOS());
            switch (change.getChange()) {
                case ChangeLog.NewItem:
                    change.getItem().encode(packet);
                    break;
                case ChangeLog.ItemNumber:
                    packet.encodeShort(change.getNumber());
                    break;
                case ChangeLog.Position:
                    packet.encodeShort(change.getPOS2());
                    break;
                case ChangeLog.DelItem:
                    break;
            }
        }
        return packet;
    }
    
    public static boolean rawIncMoney(CharacterData cd, int inc, boolean onlyFull) {
        int money = inc + cd.getCharacterStat().getMoney();
        if (inc <= 0) {
            if (money < Math.max(cd.getMoneyTrading(), 0)) {
                if (onlyFull)
                    return false;
                money = Math.max(cd.getMoneyTrading(), 0);
            }
        } else {
            if (money < 0) {
                if (onlyFull)
                    return false;
                money = ((cd.getCharacterStat().getMoney() < 0 ? 1 : 0) - 1) & 0x7FFFFFFF;
            }
            if (money < cd.getMoneyTrading())
                money = cd.getMoneyTrading();
        }
        cd.getCharacterStat().setMoney(money);
        return true;
    }
}
