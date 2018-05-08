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

import java.util.LinkedList;
import network.packet.InPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class MovePath {
    private short x;
    private short y;
    private final LinkedList<Elem> elements;
    
    public MovePath() {
        this.elements = new LinkedList<>();
    }
    
    public void decode(InPacket packet) {
        this.x = packet.decodeShort();
        this.y = packet.decodeShort();
        
        int count = packet.decodeByte();
        for (int i = 0; i < count; i++) {
            Elem elem = new Elem();
            elem.setAttr(packet.decodeByte());
            elem.setX(packet.decodeShort());
            elem.setY(packet.decodeShort());
            elem.setVx(packet.decodeShort());
            elem.setVy(packet.decodeShort());
            elem.setMoveAction(packet.decodeByte());
            elem.setFh(packet.decodeShort());
            elem.setElapse(packet.decodeShort());
            elements.add(elem);
        }
    }
    
    public void encode(OutPacket packet) {
        packet.encodeShort(getX());
        packet.encodeShort(getY());
        
        packet.encodeByte(elements.size());
        for (Elem elem : elements) {
            packet.encodeByte(elem.getAttr());
            packet.encodeShort(elem.getX());
            packet.encodeShort(elem.getY());
            packet.encodeShort(elem.getVx());
            packet.encodeShort(elem.getVy());
            packet.encodeByte(elem.getMoveAction());
            packet.encodeShort(elem.getFh());
            packet.encodeShort(elem.getElapse());
        }
    }
    
    public LinkedList<Elem> getElem() {
        return elements;
    }
    
    public short getX() {
        return x;
    }
    
    public short getY() {
        return y;
    }
    
    public class Elem {
        private byte attr;
        private short x;
        private short y;
        private short vx;
        private short vy;
        private byte moveAction;
        private short fh;
        private short elapse;
        
        public Elem() {
            // dummy
        }

        public byte getAttr() {
            return attr;
        }

        public short getX() {
            return x;
        }

        public short getY() {
            return y;
        }

        public short getVx() {
            return vx;
        }

        public short getVy() {
            return vy;
        }

        public byte getMoveAction() {
            return moveAction;
        }

        public short getFh() {
            return fh;
        }

        public short getElapse() {
            return elapse;
        }

        public void setAttr(byte attr) {
            this.attr = attr;
        }

        public void setX(short x) {
            this.x = x;
        }

        public void setY(short y) {
            this.y = y;
        }

        public void setVx(short vx) {
            this.vx = vx;
        }

        public void setVy(short vy) {
            this.vy = vy;
        }

        public void setMoveAction(byte moveAction) {
            this.moveAction = moveAction;
        }

        public void setFh(short fh) {
            this.fh = fh;
        }

        public void setElapse(short elapse) {
            this.elapse = elapse;
        }
    }
}
