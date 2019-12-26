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

/**
 *
 * @author Eric
 */
public class RewardInfo {
    private byte type;
    private final int money;
    private final int prob;
    private int itemId;
    private int min;
    private int max;
    private int maxCount;
    private int period;
    private final boolean premiumMap;
    
    public RewardInfo(int money, int itemId, int prob, int min, int max, boolean premium) {
        this.money = money;
        this.prob = prob;
        if (this.money == 0) {
            this.itemId = itemId;
            this.min = min;
            this.max = max;
        }
        this.premiumMap = premium;
    }

    public byte getType() {
        return type;
    }

    public int getMoney() {
        return money;
    }

    public int getItemId() {
        return itemId;
    }

    public int getProb() {
        return prob;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public int getPeriod() {
        return period;
    }

    public boolean isPremiumMap() {
        return premiumMap;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
