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
package game.field.portal;

/**
 *
 * @author Eric
 */
public class PortalType {
    public static final int
            StartPoint              = 0,
            Invisible               = 1,
            Visible                 = 2,
            Collision               = 3,
            Changable               = 4,
            Changable_Invisible     = 5,
            TownPortal_Point        = 6,
            Script                  = 7,
            Script_Invisible        = 8,
            Collision_Script        = 9,
            Hidden                  = 10,
            Script_Hidden           = 11,
            Collision_VerticalJump  = 12,
            Collision_CustomImpact  = 13
    ;
    
    /*
        FFFFFFFF PORTALTYPE_STARTPOINT  = 0
        FFFFFFFF PORTALTYPE_INVISIBLE  = 1
        FFFFFFFF PORTALTYPE_VISIBLE  = 2
        FFFFFFFF PORTALTYPE_COLLISION  = 3
        FFFFFFFF PORTALTYPE_CHANGABLE  = 4
        FFFFFFFF PORTALTYPE_CHANGABLE_INVISIBLE  = 5
        FFFFFFFF PORTALTYPE_TOWNPORTAL_POINT  = 6
        FFFFFFFF PORTALTYPE_SCRIPT  = 7
        FFFFFFFF PORTALTYPE_SCRIPT_INVISIBLE  = 8
        FFFFFFFF PORTALTYPE_COLLISION_SCRIPT  = 9
        FFFFFFFF PORTALTYPE_HIDDEN  = 0Ah
        FFFFFFFF PORTALTYPE_SCRIPT_HIDDEN  = 0Bh
        FFFFFFFF PORTALTYPE_COLLISION_VERTICAL_JUMP  = 0Ch
        FFFFFFFF PORTALTYPE_COLLISION_CUSTOM_IMPACT  = 0Dh
    */
}
