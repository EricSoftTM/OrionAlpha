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

import common.item.ItemSlotBase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Eric
 */
public class Reward {
    private int type;
    private ItemSlotBase item;
    private int money;
    private int period;
    private RewardInfo info;
    
    public Reward() {
        this.type = RewardType.Money;
        this.item = null;
        this.money = 0;
        this.period = 0;
    }
    
    public Reward(int type, ItemSlotBase item, int money, int period) {
        this.type = type;
        this.item = item;
        this.money = money;
        this.period = period;
    }
    
    public int getType() {
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

    public void setType(int type) {
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
    
    public static List<Reward> create(List<RewardInfo> rewardInfo, int ownerDropRate, int ownerDropRate_Ticket) {
        List<Reward> reward = new ArrayList<>();
        for (RewardInfo info : rewardInfo) {
            // TODO: Calculations and reward formulas
        }
        Collections.shuffle(reward);
        return reward;
    }
    
    public static void loadReward(int templateID, List<RewardInfo> rewardInfo) {
        rewardInfo.clear();
        // TODO: DB Loading of rewards.
        
        int count = rewardInfo.size();
        for (int i = 0; i < count; i++) {
            RewardInfo reward = rewardInfo.get(i);
            if (reward.money > 0) {
                reward.type = RewardType.Money;
            } else {
                reward.type = RewardType.Item;
                reward.period = 0;
            }
        }
    }
}
