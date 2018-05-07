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
package game.field.life.npc;

import game.field.Creature;
import game.field.GameObjectType;
import game.field.life.Controller;
import game.user.User;
import java.awt.Point;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import network.packet.OutPacket;
import util.Range;

/**
 *
 * @author Eric
 */
public class Npc extends Creature {
    private final NpcTemplate npcTemplate;
    private Point curPos;
    private Point initPos;
    private Point originalPos;
    private Range horz;
    private Controller controller;
    private final Lock lock;
    private byte moveAction;
    private short footholdSN;
    
    public Npc(NpcTemplate npcTemplate, int x, int y) {
        super();
        this.curPos = new Point(0, 0);
        this.initPos = new Point(0, 0);
        this.originalPos = new Point(0, 0);
        this.horz = new Range(0, 0);
        this.npcTemplate = npcTemplate;
        this.controller = null;
        this.lock = new ReentrantLock();
        this.footholdSN = 0;
        
        // TODO: Shop items
        
        this.originalPos.x = x;
        this.originalPos.y = y;
    }
    
    public static Npc createNpc(NpcTemplate template, int x, int y) {
        return new Npc(template, x, y);
    }
    
    public void encodeInitData(OutPacket packet) {
        lock.lock();
        try {
            packet.encodeInt(getGameObjectID());
            packet.encodeInt(getTemplateID());
            packet.encodeShort(curPos.x);
            packet.encodeShort(curPos.y);
            packet.encodeByte(moveAction);
            packet.encodeShort(footholdSN);
            packet.encodeShort(horz.low);
            packet.encodeShort(horz.high);
        } finally {
            lock.unlock();
        }
    }
    
    public int getFieldID() {
        return getField().getFieldID();
    }
    
    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.Npc;
    }
    
    @Override
    public int getTemplateID() {
        return npcTemplate.getTemplateID();
    }
    
    @Override
    public OutPacket makeEnterFieldPacket() {
        return NpcPool.onNpcEnterField(this);
    }
    
    @Override
    public OutPacket makeLeaveFieldPacket() {
        return NpcPool.onNpcLeaveField(this.getGameObjectID());
    }
    
    public void sendChangeControllerPacket(User user, boolean ctrl) {
        if (user != null) {
            user.sendPacket(NpcPool.onNpcChangeController(this, ctrl));
        }
    }
    
    public void setMovePosition(int x, int y, byte moveAction, short fh) {
        lock.lock();
        try {
            this.curPos.x = x;
            this.curPos.y = y;
            this.moveAction = moveAction;
            this.footholdSN = fh;
        } finally {
            lock.unlock();
        }
    }
}
