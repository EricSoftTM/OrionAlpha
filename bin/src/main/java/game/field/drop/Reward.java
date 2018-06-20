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
import game.user.item.ItemInfo;
import game.user.item.ItemVariationOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import network.database.GameDB;
import util.Rand32;

/**
 *
 * @author Eric
 */
public class Reward {
    private byte type;
    private ItemSlotBase item;
    private int money;
    private int period;
    private RewardInfo info;
    
    public Reward() {
        this(RewardType.MONEY, null, 0, 0);
    }
    
    public Reward(byte type, ItemSlotBase item, int money, int period) {
        this.type = type;
        this.item = item;
        this.money = money;
        this.period = period;
    }
    
    public byte getType() {
        return type;
    }
    
    public ItemSlotBase getItem() {
        return item;
    }
    
    public int getMoney() {
        return money;
    }
    
    public int getPeriod() {
        return period;
    }
    
    public RewardInfo getInfo() {
        return info;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setItem(ItemSlotBase item) {
        this.item = item;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void setInfo(RewardInfo info) {
        this.info = info;
    }
    
    public static List<Reward> create(List<RewardInfo> rewardInfo, boolean premiumMap, int ownerDropRate, int ownerDropRate_Ticket) {
        final float INC_DROP_RATE = 1.0f; //fIncDropRate, default 1
        final float REWARD_RATE = 1.0f; //dRewardRate, always 1 (used for monster carnival which doesn't exist)
        
        List<Reward> rewards = new ArrayList<>();
        for (RewardInfo info : rewardInfo) {
            int itemRate = 1;
            int moneyRate = 1;
            if (info.getType() == RewardType.MONEY) {
                moneyRate = ownerDropRate;
            } else {
                if (info.getType() == RewardType.ITEM)
                    itemRate = ownerDropRate_Ticket;
            }
            int minProb = (int) (long) (1000000000.0d / INC_DROP_RATE / (double) ownerDropRate / (double) itemRate / REWARD_RATE);
            int maxProb = 1000000000;
            if (minProb > 0) {
                maxProb = (int) (Rand32.getInstance().random() % minProb);
            }
            if (maxProb < info.getProb()) {
                Reward reward = new Reward();
                if (!info.isPremiumMap() || premiumMap) {
                    reward.setType(info.getType());
                    reward.setInfo(info);
                    if (info.getType() == RewardType.MONEY) {
                        int min = 2 * info.getMoney() / 5 + 1;
                        int max = 4 * info.getMoney() / 5;
                        int rand = 1;
                        if (min > 0) {
                            rand = Math.max(1, max + (int) (Rand32.getInstance().random() % min));
                        }
                        reward.setMoney(moneyRate * rand);
                        rewards.add(reward);
                    } else {
                        if (reward.getType() == RewardType.ITEM) {
                            ItemSlotBase item = ItemInfo.getItemSlot(info.getItemId(), ItemVariationOption.Normal);
                            if (item != null) {
                                if (info.getPeriod() > 0) {
                                    //item.setDateExpire(ItemAccessor.getDateExpireFromPeriod(info.getPeriod()));
                                } else {
                                    //item.setDateExpire(info.getDateExpire());
                                }
                                if (ItemAccessor.isBundleTypeIndex(ItemAccessor.getItemTypeIndexFromID(info.getItemId()))) {
                                    int rand = Math.min(info.getMax() - info.getMin() + 1, 1);
                                    if (rand > 1)
                                        rand = info.getMin() + (int) (Rand32.getInstance().random() % rand);
                                    item.setItemNumber(rand);
                                }
                                reward.setItem(item);
                                rewards.add(reward);
                            }
                        }
                    }
                }
            }
        }
        Collections.shuffle(rewards);
        return rewards;
    }
    
    public static void loadReward(int templateID, List<RewardInfo> rewardInfo) {
        rewardInfo.clear();
        
        int count = GameDB.rawLoadReward(templateID, rewardInfo);
        for (int i = 0; i < count; i++) {
            RewardInfo reward = rewardInfo.get(i);
            if (reward.getMoney() > 0) {
                reward.setType(RewardType.MONEY);
            } else {
                reward.setType(RewardType.ITEM);
                reward.setPeriod(0);
                
                // Nexon additionally returns errors for the below conditions..
                if (reward.getMin() <= 0 || reward.getMax() > 1) {
                    
                }
                if (ItemInfo.isCashItem(reward.getItemId())) {
                    
                }
                if (ItemInfo.getItemSlot(reward.getItemId(), ItemVariationOption.None) == null) {
                    
                }
                if (reward.getPeriod() != 0) {
                    // if FileTime.CompareFileTime(ftDateExpire, FileTime.END) < 0
                }
            }
        }
    }
}
