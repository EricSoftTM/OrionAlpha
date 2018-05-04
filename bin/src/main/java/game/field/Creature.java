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

import network.packet.OutPacket;

/**
 * CCreature
 * 
 * In official terms, Creature is truly CCreature : CGameObject.
 * 
 * To keep the inheritance the same as what Nexon is doing
 * and not causing any inheritance clashing, we will extend
 * FieldObj in addition to implementing GameObject.
 * 
 * @author Eric
 */
public class Creature extends FieldObj implements GameObject {
    // baseclass_0
    private final int id;
    
    public Creature() {
        super();
        
        GameObjectBase obj = construct();
        
        this.id = obj.getID();
    }
    
    /**
     * Uses the GameObjectBase to register this as a new GameObject. 
     * 
     * Due to multiple inheritance constraints, we use this to
     * call a super() constructor of GameObject in a way.
     */
    private GameObjectBase construct() {
        return GameObjectBase.registerGameObject(this);
    }
    
    @Override
    public int getGameObjectID() {
        return id;
    }
    
    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.Creature;
    }
    
    @Override
    public int getTemplateID() {
        return 0;
    }
    
    @Override
    public OutPacket makeEnterFieldPacket() {
        return null;
    }
    
    @Override
    public OutPacket makeLeaveFieldPacket() {
        return null;
    }
}
