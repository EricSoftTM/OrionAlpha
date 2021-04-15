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
package game.field.portal;

import game.field.GameObject;
import game.field.GameObject.GameObjectBase;
import game.field.GameObjectType;
import java.awt.Point;

/**
 *
 * @author Eric
 */
public class Portal implements GameObject {
    // baseclass_0
    private final int id;
    
    private String name;
    private String tname;
    private String script;
    private int fieldID;
    private byte idx;
    private boolean enable;
    private int type;
    public Point pos; 
    private int tmap;
    
    public Portal() {
        super();
        
        this.name = "";
        this.tname = "";
        this.script = "";
        this.pos = new Point();
        
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
    
    public int getFieldID() {
        return fieldID;
    }
    
    @Override
    public int getGameObjectID() {
        return id;
    }
    
    @Override
    public int getGameObjectTypeID() {
        return GameObjectType.Portal;
    }
    
    public byte getPortalIdx() {
        return idx;
    }
    
    public String getPortalName() {
        return name;
    }
    
    public Point getPortalPos() {
        return pos;
    }
    
    public String getPortalScriptName() {
        return script;
    }
    
    public int getPortalType() {
        return type;
    }
    
    public int getTargetPortalMap() {
        return tmap;
    }
    
    public String getTargetPortalName() {
        return tname;
    }
    
    @Override
    public int getTemplateID() {
        return 0;
    }
    
    public boolean isEnabled() {
        return enable;
    }
    
    /**
     * Determines if the Portal is one who contains a script type
     * and may execute a script request.
     * 
     * @return If the portal type is of type 'Script'
     */
    public boolean isScriptPortal() {
        return type == PortalType.Script || type == PortalType.Script_Invisible
                || type == PortalType.Collision_Script || type == PortalType.Script_Hidden;
    }
    
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    
    public void setPortalName(String name) {
        this.name = name;
    }
    
    public void setTargetName(String name) {
        this.tname = name;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public void setFieldID(int fieldID) {
        this.fieldID = fieldID;
    }
    
    public void setPortalIdx(int idx) {
        this.idx = (byte) idx;
    }
    
    public void setPortalType(int type) {
        this.type = type;
    }
    
    public void setTargetMap(int map) {
        this.tmap = map;
    }
}
