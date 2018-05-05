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
package game.field.drop;

import game.field.Field;
import game.field.FieldSplit;
import game.field.StaticFoothold;
import game.user.User;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import network.packet.InPacket;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;
import util.Logger;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class DropPool {
    private final AtomicInteger dropIdCounter;
    private final Field field;
    private long lastExpire;
    private final Map<Integer, Drop> drops;
    
    public DropPool(Field field) {
        this.field = field;
        this.dropIdCounter = new AtomicInteger(30000);
        this.drops = new HashMap<>();
        this.lastExpire = System.currentTimeMillis();
    }
    
    public void create(Reward reward, int ownerID, int sourceID, int x1, int y1, int x2, int y2, int delay, boolean admin, int pos) {
        if (field.lock(1100)) {
            try {
                Pointer<Integer> py2 = new Pointer<>(y2);
                StaticFoothold pfh = field.getSpace2D().getFootholdUnderneath(x2, y1 - 100, py2);
                y2 = py2.get();
                FieldSplit centerSplit = field.splitFromPoint(x2, y2);
                if (centerSplit == null || pfh == null || !field.getSpace2D().isPointInMBR(x2, y2, true)) {
                    Pointer<Integer> px2 = new Pointer<>(x2);
                    pfh = field.getSpace2D().getFootholdClosest(field, x2, y1, px2, py2, x1);
                    x2 = px2.get();
                    y2 = py2.get();
                    centerSplit = field.splitFromPoint(x2, y2);
                }
                if (centerSplit != null && pfh != null) {
                    int id = dropIdCounter.incrementAndGet();
                    Drop drop = new Drop(id, reward, ownerID, sourceID, x1, y1, x2, y2);
                    drop.setField(field);
                    drop.setCreateTime(System.currentTimeMillis());
                    drop.setPos(pos);
                    drop.setEverlasting(false);
                    field.getEncloseSplit(centerSplit, drop.getSplits());
                    drops.put(id, drop);
                    for (FieldSplit split : drop.getSplits()) {
                        field.splitRegisterFieldObj(split, 4, drop, drop.makeEnterFieldPacket(Drop.Create, delay));
                    }
                    Logger.logReport("Dropping mesos");
                    field.splitNotifyFieldObj(centerSplit, drop.makeEnterFieldPacket(Drop.JustShowing, delay), drop);
                } else {
                    Logger.logError("Cannot Create Drop [ ptHitx : %d, ptHity : %d, cx : %d, FieldID: %d ]", x1, y1, x2, field.getFieldID());
                }
            } finally {
                field.unlock();
            }
        }
    }
    
    public Drop get(int dropID) {
        if (field.lock()) {
            try {
                return drops.get(dropID);
            } finally {
                field.unlock();
            }
        }
        return null;
    }
    
    public boolean onEnter(User user) {
        return true;
    }
    
    public void onLeave(User user) {
        
    }
    
    public void onPacket(User user, byte type, InPacket packet) {
        /*if (type == ClientPacket.DropPickUpRequest) {
            onPickUpRequest(user, packet);
        }*/
    }
    
    public void remove(int id, int delay) {
        if (field.lock()) {
            try {
                if (drops.containsKey(id)) {
                    Drop drop = drops.get(id);
                    if (drop != null) {
                        int leaveType = Drop.ByTimeOut;
                        int option = 0;
                        if (delay != 0) {
                            option = delay;
                            //leaveType = Drop.Explode;
                        }
                        for (FieldSplit split : drop.getSplits()) {
                            field.splitUnregisterFieldObj(split, 4, drop, drop.makeLeaveFieldPacket(leaveType, option));
                        }
                        drops.remove(id);
                    }
                }
            } finally {
                field.unlock();
            }
        }
    }
    
    public void tryExpire(boolean removeAll) {
        long time = System.currentTimeMillis();
        if (removeAll || (time - lastExpire) >= 10000) {
            if (field.lock()) {
                try {
                    for (Iterator<Map.Entry<Integer, Drop>> it = drops.entrySet().iterator(); it.hasNext();) {
                        Drop drop = it.next().getValue();
                        if (!drop.isEverlasting()&& (removeAll || time - drop.getCreateTime() >= 180000)) {
                            it.remove();
                            for (FieldSplit split : drop.getSplits()) {
                                field.splitUnregisterFieldObj(split, 4, drop, drop.makeLeaveFieldPacket(Drop.ByTimeOut, 0));
                            }
                        }
                    }
                } finally {
                    field.unlock();
                }
            }
            lastExpire = time;
        }
    }
    
    public void onPickUpRequest(User user, InPacket packet) {
        // soonTM
    }
    
    public static OutPacket onDropEnterField(Drop drop, int enterType, int delay) {
        OutPacket packet = new OutPacket(LoopbackPacket.DropEnterField);
        packet.encodeByte(enterType);
        packet.encodeInt(drop.getDropID());
        packet.encodeBool(drop.isMoney());
        packet.encodeInt(drop.getDropInfo());
        packet.encodeShort(drop.getPt2().x);
        packet.encodeShort(drop.getPt2().y);
        if (enterType == Drop.JustShowing || enterType == Drop.Create || enterType == Drop.FadingOut) {
            packet.encodeInt(drop.getOwnType());//actually might be dwSourceID..
            packet.encodeShort(drop.getPt1().x);
            packet.encodeShort(drop.getPt1().y);
            packet.encodeShort(delay);
        }
        return packet;
    }
    
    public static OutPacket onDropLeaveField(int dropID, int leaveType, int option) {
        OutPacket packet = new OutPacket(LoopbackPacket.DropLeaveField);
        packet.encodeByte(leaveType);
        packet.encodeInt(dropID);
        if (leaveType == Drop.PickedUpByUser) {
            packet.encodeInt(option);//dwPickupID
        }
        return packet;
    }
}
