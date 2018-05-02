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
package login.avatar;

import common.item.BodyPart;
import common.user.CharacterStat;
import java.util.ArrayList;
import java.util.List;

/**
 * AvatarLook
 * 
 * @author Eric
 */
public class AvatarLook {
    public static final int
            Look    = 1,
            Speed   = 2
    ;
    static final int BODY_PART_COUNT = BodyPart.BP_Count;
    private byte gender;
    private byte skin;
    private byte face;
    private final List<Integer> anHairEquip;
    private final List<Integer> anUnseenEquip;
    
    public AvatarLook() {
        this.gender = 0;
        this.skin = 0;
        this.face = 0;
        this.anHairEquip = new ArrayList<>();
        this.anUnseenEquip = new ArrayList<>();
        
        for (int i = 0; i < BODY_PART_COUNT + 1; i++) {
            this.anHairEquip.add(i, 0);
            this.anUnseenEquip.add(i, 0);
        }
    }
    
    public void load(CharacterStat cs, List<Integer> equipped, List<Integer> equipped2) {
        
    }
}
