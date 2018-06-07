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
package game.user.command;

import common.user.CharacterStat;
import game.field.Field;
import game.field.FieldSplit;
import game.field.drop.Drop;
import game.field.drop.DropPool;
import game.field.life.LifePool;
import game.field.life.npc.Npc;
import game.user.User;
import game.user.WvsContext.Request;
import java.util.Iterator;

/**
 *
 * @author Eric
 */
public class DeveloperCommands {
    
    public static String whereami(User user, CharacterStat stat, String[] args) {
        user.sendSystemMessage("You are currently on field " + stat.getPosMap());
        return null;
    }
    
    public static String pos(User user, Field field, String[] args) {
        int x = user.getCurrentPosition().x;
        int y = user.getCurrentPosition().y;
        int fh = field.getSpace2D().getFootholdUnderneath(x, y).getSN();
        
        user.sendSystemMessage("Your character's current position is (" + x + "," + y + ") on foothold (" + fh + ")");
        return null;
    }
    
    public static String spawn(User user, LifePool lifePool, String[] args) {
        if (args.length > 0) {
            int templateID = Integer.parseInt(args[0]);
            int count = 1;
            if (args.length > 1) {
                count = Math.min(100, Math.max(1, Integer.parseInt(args[1])));
            }
            for (int i = 0; i < count; i++) {
                lifePool.createMob(lifePool.createMob(templateID), user.getCurrentPosition());
            }
            return null;
        }
        return "!spawn <mobid> [count] - Spawns a mob, or optionally a specific amount of mobs";
    }
    
    public static String npc(User user, LifePool lifePool, String[] args) {
        if (args.length > 0) {
            Npc npc = lifePool.getNpc(Integer.parseInt(args[0]));
            if (npc != null) {
                int x = user.getCurrentPosition().x;
                int y = user.getCurrentPosition().y;
                
                lifePool.createNpc(null, npc.getTemplateID(), x, y);
            } else {
                user.sendSystemMessage("The Npc you have entered does not have an existing template.");
            }
            return null;
        }
        return "!npc <id> - Spawns the specified NPC at your location";
    }
    
    public static String itemvac(User user, Field field, DropPool dropPool, String[] args) {
        for (Iterator<Drop> it = dropPool.getDrops().values().iterator(); it.hasNext();) {
            Drop drop = it.next();
            if (user.sendDropPickUpResultPacket(drop, Request.None)) {
                it.remove();
                for (FieldSplit split : drop.getSplits()) {
                    field.splitUnregisterFieldObj(split, 4, drop, drop.makeLeaveFieldPacket(Drop.PickedUpByUser, user.getCharacterID()));
                }
            }
        }
        return null;
    }
}
