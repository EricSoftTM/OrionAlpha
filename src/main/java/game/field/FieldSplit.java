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
import java.util.LinkedList;

/**
 *
 * @author Eric
 */
public class FieldSplit {
    // Focus Points
    public static final int
            User            = 0,
            Mob             = 1,
            Npc             = 2,
            Drop            = 3,
            NO              = 4
    ;
    private int row;
    private int col;
    private int index;
    private final LinkedList<User> users;
    private final LinkedList<FieldObj>[] fieldObj;
    
    public FieldSplit(int row, int col, int index) {
        this.row = row;
        this.col = col;
        this.index = index;
        this.users = new LinkedList<>();
        this.fieldObj = new LinkedList[GameObjectType.NO];
        for (int i = 0; i < GameObjectType.NO; i++) {
            this.fieldObj[i] = new LinkedList<>();
        }
    }
    
    public LinkedList<User> getUser() {
        return users;
    }
    
    public LinkedList<FieldObj> getFieldObj(int foc) {
        return fieldObj[foc];
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
