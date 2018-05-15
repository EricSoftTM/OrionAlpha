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

import util.Rand32;

/**
 *
 * @author Eric
 */
public class ItemVariationOption {
    public static final int
            // ITEMVARIATION_
            None        = 0,
            Better      = 1,
            Normal      = 2,
            Great       = 3,
            
            Count       = 4
    ;
    
    /**
     * Based on the item's variation option provided, this method will
     * randomize and apply the new variation of stats.
     * 
     * The item variations are defined as follows:
     *  -> None = No variation, return the default value given
     *  -> Normal = 50% chance to either increase or decrease by variation
     *  -> Better = 70% chance to increase variation
     *  -> Great = 90% chance to increase variation
     * 
     * @param v The default base stats value
     * @param option The type of item variation as defined above
     * @return The new variation if provided
     */
    public static Integer getVariation(int v, int option) {//ITEMVARIATIONOPTION enOption
        if (v != 0) {
            int randRange = Math.min(v / 10 + 1, 5);
            int randomBit = Rand32.getRand(1 << (randRange + 2), 0).intValue();
            int delta = 0;
            for (int i = 0; i < randRange + 2; i++) {
                delta += randomBit & 1;
                randomBit >>= 1;
            }
            delta -= 2;
            
            int variation = delta + v;
            switch (option) {
                case Normal:
                    if (Rand32.getRand(2, 0) == 0)
                        variation = Math.max(v - delta, 0);
                    break;
                case None:
                case Better:
                case Great:
                default: {
                    if ((option == Better && Rand32.getRand(10, 0) >= 3) || (option == Great && Rand32.getRand(10, 0) >= 1))
                        break;
                    variation = v;
                }
            }
            
            return variation;
        }
        return v;
    }
}
