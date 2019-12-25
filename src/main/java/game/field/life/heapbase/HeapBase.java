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
package game.field.life.heapbase;

import game.field.life.Controller;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public abstract class HeapBase {
    private final int increment;
    private int count;
    private final List<Controller> heap;
    
    public HeapBase(int increment) {
        this.increment = increment;
        this.count = 0;
        this.heap = new ArrayList<>();
    }
    
    public int getIncrement() {
        return increment;
    }
    
    public int getCount() {
        return count;
    }
    
    public List<Controller> getHeap() {
        return heap;
    }
    
    public int decCount() {
        return count--;
    }
    
    public int incCount() {
        return count++;
    }
    
    public void removeAll() {
        heap.clear();
        count = 0;
    }
    
    public abstract int adjust(int parentCnt);
    public abstract void adjustUpward();
    public abstract int insert(Controller t);
    public abstract void removeAt(int index);
    public abstract void swap(int index1, int index2);
    public abstract void updateAt(int index);
}
