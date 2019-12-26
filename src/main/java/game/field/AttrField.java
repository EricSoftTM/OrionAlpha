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

/**
 *
 * @author Eric
 */
public class AttrField {
    private double walk;
    private double drag;
    private double fly;
    private double g;

    public AttrField() {
        this.walk = 0.0d;
        this.drag = 0.0d;
        this.fly = 0.0d;
        this.g = 0.0d;
    }

    public double getWalk() {
        return walk;
    }

    public double getDrag() {
        return drag;
    }

    public double getFly() {
        return fly;
    }
    
    public double getGravity() {
        return g;
    }

    public void setWalk(double walk) {
        this.walk = walk;
    }

    public void setDrag(double drag) {
        this.drag = drag;
    }

    public void setFly(double fly) {
        this.fly = fly;
    }
    
    public void setGravity(double g) {
        this.g = g;
    }
}
