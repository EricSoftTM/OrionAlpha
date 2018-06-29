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

import common.item.ItemAccessor;
import common.item.ItemSlotBase;
import common.item.ItemType;
import game.field.FieldObj;
import game.user.User;
import game.user.item.Inventory;
import game.user.item.ItemInfo;
import java.awt.Point;
import network.packet.OutPacket;
import util.Pointer;

/**
 *
 * @author Eric
 */
public class Drop extends FieldObj {
    // EnterType
    public static final int 
            JustShowing     = 0,
            Create          = 1,
            OnTheFoothold   = 2,
            FadingOut       = 3
    ;
    // LeaveType
    public static final int 
            ByTimeOut       = 0,
            ByScreenScroll  = 1,
            PickedUpByUser  = 2
    ;
    private int dropID;
    private int sourceID;
    private int ownerID;
    private int ownType;
    private int money;
    private int period;
    private int showMax;
    private int pos;
    private long createTime;
    private boolean isMoney;
    private boolean everlasting;
    private Point pt1;
    private Point pt2;
    private ItemSlotBase item;
    
    public Drop(int dropID, Reward reward, int ownerID, int sourceID, int x1, int y1,int x2, int y2) {
        super();
        this.dropID = dropID;
        this.ownerID = ownerID;
        this.sourceID = sourceID;
        this.pt1 = new Point(x1, y1);
        this.pt2 = new Point(x2, y2);
        this.isMoney = reward.getType() == RewardType.MONEY;
        this.money = reward.getMoney();
        this.showMax = 0;
        this.period = reward.getPeriod();
        if (reward.getInfo() != null) {
            this.showMax = reward.getInfo().getMaxCount();
        }
        this.item = reward.getItem();
    }

    public int getDropID() {
        return dropID;
    }

    public int getSourceID() {
        return sourceID;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public int getOwnType() {
        return ownType;
    }

    public int getMoney() {
        return money;
    }

    public int getPeriod() {
        return period;
    }

    public boolean isMoney() {
        return isMoney;
    }
    
    public boolean isEverlasting() {
        return everlasting;
    }

    public Point getPt1() {
        return pt1;
    }

    public Point getPt2() {
        return pt2;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public int getDropInfo() {
        int info = money;
        if (info == 0) {
            if (item != null) {
                info = item.getItemID();
            } else {
                info = 0;
            }
        }
        return info;
    }
    
    public ItemSlotBase getItem() {
        return item;
    }
    
    @Override
    public OutPacket makeEnterFieldPacket() {
        return DropPool.onDropEnterField(this, Drop.OnTheFoothold, 0);
    }
    
    public OutPacket makeEnterFieldPacket(int enterType, int delay) {
        return DropPool.onDropEnterField(this, enterType, delay);
    }
    
    @Override
    public OutPacket makeLeaveFieldPacket() {
        return DropPool.onDropLeaveField(this.dropID, Drop.ByScreenScroll, 0);
    }
    
    public OutPacket makeLeaveFieldPacket(int leaveType, int option) {
        return DropPool.onDropLeaveField(this.dropID, leaveType, option);
    }
    
    @Override
    public boolean isShowTo(User user) {
        if (user == null || user.getHP() == 0 || user.getField() == null) {
            return false;
        }
        if (showMax > 0) {
            if (user.lock()) {
                try {
                    int count = Inventory.getItemCount(user, item.getItemID());
                    if (ItemInfo.getEquipItem(item.getItemID()) != null) {
                        Pointer<Integer> bodyPart = new Pointer<>(0);
                        ItemAccessor.getBodyPartFromItem(item.getItemID(), user.getCharacter().getCharacterStat().getGender(), bodyPart, false);
                        ItemSlotBase itemSlot = user.getCharacter().getItem(ItemType.Equip, -bodyPart.get());
                        if (itemSlot != null) {
                            if (item.getItemID() == itemSlot.getItemID()) {
                                ++count;
                            }
                        }
                    }
                    if (count >= showMax) {
                        return false;
                    }
                } finally {
                    user.unlock();
                }
            }
        }
        return true;
    }
    
    public void setCreateTime(long time) {
        this.createTime = time;
    }
    
    public void setPos(int pos) {
        this.pos = pos;
    }
    
    public void setEverlasting(boolean everlasting) {
        this.everlasting = everlasting;
    }
}
