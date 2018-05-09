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

import java.util.LinkedList;

/**
 *
 * @author Eric
 */
public class MobDamageLog {
    private static final int LOG_SIZE = 32;
    
    public int fieldID;
    public int initHP;
    public int vainDamage;
    private final LinkedList<Info> log;
    
    public MobDamageLog() {
        this.log = new LinkedList<>();
    }
    
    public void addLog(int characterID, int damage, long time) {
        Info info = findPosition(characterID);
        if (info != null) {
            log.remove(info);
            
            info.damage = Math.min(Math.max(0, damage + info.damage), Math.max(Integer.MAX_VALUE, initHP));
            info.time = time;
            log.addLast(info);
        } else {
            if (log.size() >= LOG_SIZE) {
                info = log.getFirst();
                
                vainDamage = Math.min(Math.max(0, info.damage + vainDamage), Math.max(Integer.MAX_VALUE, initHP));
                
                log.removeFirst();
            }
            info = new Info();
            info.damage = damage;
            info.characterID = characterID;
            info.time = time;
            log.addLast(info);
        }
    }
    
    public Info findPosition(int characterID) {
        for (Info info : log) {
            if (info.characterID == characterID) {
                return info;
            }
        }
        return null;
    }
    
    public LinkedList<Info> getLog() {
        return log;
    }
    
    public class Info {
        public int characterID;
        public int damage;
        public long time;
    }
}
