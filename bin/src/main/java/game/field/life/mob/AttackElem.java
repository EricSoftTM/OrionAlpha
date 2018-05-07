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
package game.field.life.mob;

import util.Logger;

/**
 *
 * @author Eric
 */
public class AttackElem {
    public static final int
            Physical    = 0,
            Ice         = 1,
            Fire        = 2,
            Light       = 3,
            Poison      = 4,
            Holy        = 5,
            Count       = 6
    ;
    
    public static final int getElementAttribute(char elemAttr) {
        switch (Character.toUpperCase(elemAttr)) {
            case '\0'://(null)
            case 'P'://80 & 112
                return Physical;
            case 'I'://73 & 105
                return Ice;
            case 'F'://70 & 102
                return Fire;
            case 'L'://76 & 108
                return Light;
            case 'S'://83 & 115
                return Poison;
            case 'H'://72 & 104
                return Holy;
            default: {
                Logger.logError("Undefined element found: '%c'", elemAttr);
                
                return Physical;
            }
        }
    }
}
