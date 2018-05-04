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
package game.field;

import game.user.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class FieldSplit {
    // Focus Points
    public static final int
            User        = 0,
            Mob         = 1,
            Npc         = 2,
            Pet         = 3,
            Summon      = 4,
            TownPortal  = 5,
            Employee    = 6,
            Grenade     = 7,
            Dragon      = 8
    ;
    private int row;
    private int col;
    private int index;
    private final List<User> users;
    private final List<FieldObj> fieldObj;
    
    public FieldSplit(int row, int col, int index) {
        this.row = row;
        this.col = col;
        this.index = index;
        this.users = new ArrayList<>();
        this.fieldObj = new ArrayList<>(GameObjectType.NO);
        for (int i = 0; i < GameObjectType.NO; i++) {
            this.fieldObj.add(i, null);
        }
    }
    
    public List<User> getUser() {
        return users;
    }
    
    public List<FieldObj> getFieldObj() {
        return fieldObj;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public int getIndex() {
        return index;
    }
}
