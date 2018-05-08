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

/**
 *
 * @author Eric
 */
public class CompareCtrlMin extends HeapBase {
    
    public CompareCtrlMin(int increment) {
        super(increment);
    }

    @Override
    public int adjust(int parentCnt) {
        int childCnt = 2 * parentCnt + 1;
        if (childCnt < getCount()) {
            Controller child = getHeap().get(childCnt);
            Controller parent = getHeap().get(parentCnt);
            if (parent.getCtrlCount() > child.getCtrlCount()) {
                if (childCnt < getCount() - 1) {
                    child = getHeap().get(2 * parentCnt + 2);
                    parent = getHeap().get(childCnt);
                    if (parent.getCtrlCount() > child.getCtrlCount())
                        childCnt = 2 * parentCnt + 2;
                }
                swap(parentCnt, childCnt);
                return childCnt;
            }
            childCnt = 2 * parentCnt + 2;
            if (childCnt < getCount()) {
                child = getHeap().get(childCnt);
                parent = getHeap().get(parentCnt);
                if (parent.getCtrlCount() > child.getCtrlCount()) {
                    swap(parentCnt, childCnt);
                    return childCnt;
                }
            }
        }
        return 0;
    }

    @Override
    public void adjustUpward() {
        int count = getCount() - 1;
        while (adjust(count) != 0) {
            count = (count - 1) >> 1;
        }
    }

    @Override
    public int insert(Controller t) {
        if (getHeap().size() <= getCount()) {
            int alloc = getIncrement();
            if (alloc == 0 && !getHeap().isEmpty())
                alloc = getHeap().size();
            if (alloc == 0)
                alloc = 4;
            for (int i = 0; i < alloc; i++) {
                getHeap().add(null);
            }
        }
        getHeap().set(incCount(), t);
        adjustUpward();
        return getCount();
    }

    @Override
    public void removeAt(int index) {
        getHeap().set(index, null);
        boolean last = decCount() == 1;
        if (last && index < getCount()) {
            getHeap().set(index, getHeap().get(getCount()));
            for (int i = index;;) {
                i = adjust(i);
                if (i == 0)
                    break;
            }
        }
    }

    @Override
    public void swap(int index1, int index2) {
        Controller t = getHeap().get(index1);

        getHeap().set(index1, getHeap().get(index2));
        getHeap().set(index2, t);
    }

    @Override
    public void updateAt(int index) {
        if (getCount() != 1) {
            decCount();
            if (index < getCount()) {
                getHeap().set(index, getHeap().get(getCount()));
                for (int i = index;;) {
                    i = adjust(i);
                    if (i == 0)
                        break;
                }
                //reassign index back to origin?
            }
            incCount();
            adjustUpward();
        }
    }
}
