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
package game.field.life.npc;

/**
 *
 * @author Eric
 */
public class ShopResCode {
    public static final int
            // SHOP_REQ
            Buy         = 0,
            Sell        = 1,
            Recharge    = 2,
            Close       = 3,
            // SHOP_RES
            Success     = 0,
            NoStock     = 1,
            Unknown2    = 2,//BuyNoMoney?
            Unknown3    = 3,
            Unknown4    = 4
    ;
}
