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
package game.field.life;

import game.field.life.mob.Mob;
import game.field.life.npc.Npc;
import game.user.User;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class Controller {
    private User user;
    private final List<Mob> ctrlMob;
    private final List<Npc> ctrlNpc;
    private int posMinHeap;
    private int posMaxHeap;
    
    public Controller(User user) {
        this.user = user;
        this.ctrlMob = new ArrayList<>();
        this.ctrlNpc = new ArrayList<>();
        this.posMinHeap = 0;
        this.posMaxHeap = 0;
    }
    
    public User getUser() {
        return user;
    }
    
    public List<Mob> getCtrlMob() {
        return ctrlMob;
    }
    
    public List<Npc> getCtrlNpc() {
        return ctrlNpc;
    }
    
    public int getPosMinHeap() {
        return posMinHeap;
    }
    
    public int getPosMaxHeap() {
        return posMaxHeap;
    }
    
    public void setPosMinHeap(int pos) {
        this.posMinHeap = pos;
    }
    
    public void setPosMaxHeap(int pos) {
        this.posMaxHeap = pos;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public int getCtrlCount() {
        return ctrlMob.size() + ctrlNpc.size() - (!ctrlMob.isEmpty() ? 1 : 0);
    }
}
