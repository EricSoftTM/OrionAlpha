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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CGameObject
 * 
 * While this class is actually very much a real, abstract class,
 * Java cannot extend and inherit from multiple classes. For this
 * reason, we hold this as an interface and control it from a Base.
 * 
 * Nexon registers and unregisters these objects in addition to
 * assigning each a dwID (GameObjectID), thus we do so in the Base.
 * 
 * All classes used to inherit from GameObject will return the said
 * GameObjectID in their inherited GetGameObjectID() methods.
 * 
 * @author Eric
 */
public interface GameObject {
    public class GameObjectBase {
        private static final Lock lockGameObject;
        private static final AtomicInteger objectCounter;
        private static final Map<Integer, GameObjectBase> gameObject;
        
        private final int id;
        private final GameObject p;
        
        public GameObjectBase(GameObject p) {
            this.id = objectCounter.incrementAndGet();
            this.p = p;
        }
        
        public int getID() {
            return id;
        }
        
        public GameObject getObject() {
            return p;
        }
        
        public static final GameObject getFromGameObjectID(int id) {
            lockGameObject.lock();
            try {
                if (gameObject.containsKey(id)) {
                    GameObjectBase obj = gameObject.get(id);
                    
                    if (obj != null) {
                        return obj.p;
                    }
                }
                return null;
            } finally {
                lockGameObject.unlock();
            }
        }
        
        public static final GameObjectBase registerGameObject(GameObject p) {
            lockGameObject.lock();
            try {
                GameObjectBase obj = new GameObjectBase(p);
                
                gameObject.put(obj.id, obj);
                
                return obj;
            } finally {
                lockGameObject.unlock();
            }
        }
        
        public static final void unregisterGameObject(GameObject p) {
            lockGameObject.lock();
            try {
                int id = p.getGameObjectID();
                if (id != 0) {
                    if (gameObject.containsKey(id)) {
                        GameObjectBase obj = gameObject.get(id);
                        
                        if (obj != null) {
                            gameObject.remove(obj.getID());
                        }
                    }
                }
            } finally {
                lockGameObject.unlock();
            }
        }
        
        static {
            lockGameObject = new ReentrantLock(true);
            objectCounter = new AtomicInteger(0);
            gameObject = new LinkedHashMap<>();
        }
    }
    
    /*
        public GameObject() {
            
        }
    */
    
    public int getGameObjectTypeID();
    /*
        {
            return GameObjectType.GameObject;
        }
    */
    
    public int getTemplateID();
    /*
        {
            return 0;
        }
    */
    
    public int getGameObjectID();
    /*
        {
            return id;
        }
    */
}
