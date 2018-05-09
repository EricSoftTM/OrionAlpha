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
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eric
 */
public class AttackInfo {
    public int mobID;
    public int templateID;
    public Mob deadMob;
    public byte hitAction;
    public byte left;
    public Point hit;
    public short delay;
    public byte attackCount;
    public final List<Short> damageCli;
    
    public AttackInfo() {
        this.hit = new Point(0, 0);
        this.damageCli = new ArrayList<>(15);
    }
}
