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

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Eric
 */
public class MobGen {
    public int templateID;
    public short x;
    public short y;
    public short fh;
    public int regenInterval;
    public long regenAfter;
    public final AtomicInteger mobCount;
    public byte f;
    
    public MobGen() {
        this.mobCount = new AtomicInteger(0);
    }
}
