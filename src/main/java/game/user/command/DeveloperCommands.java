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

import common.Request;
import common.user.CharacterStat;
import common.user.CharacterStat.CharacterStatType;
import common.user.DBChar;
import game.field.Field;
import game.field.FieldSplit;
import game.field.drop.Drop;
import game.field.drop.DropPool;
import game.field.life.LifePool;
import game.field.life.npc.Npc;
import game.user.User;

/**
 *
 * @author Eric
 */
public class DeveloperCommands {
    
    @CommandDesc("Set the value of any specific character stat")
    public static String stat(User user, CharacterStat stat, String[] args) {
        int flag = 0;
        if (args.length == 2) {
            short val = Short.parseShort(args[1]);
            if (val > 0) {
                switch (args[0].toLowerCase()) {
                    case "str":
                        stat.setSTR(val); flag |= CharacterStatType.STR;
                        break;
                    case "dex":
                        stat.setDEX(val); flag |= CharacterStatType.DEX;
                        break;
                    case "int":
                        stat.setINT(val); flag |= CharacterStatType.INT;
                        break;
                    case "luk":
                        stat.setLUK(val); flag |= CharacterStatType.LUK;
                        break;
                    case "mhp":
                        stat.setMHP(val); flag |= CharacterStatType.MHP;
                        break;
                    case "mmp":
                        stat.setMMP(val); flag |= CharacterStatType.MMP;
                        break;
                    case "hp":
                        stat.setHP(Math.min(val, stat.getMHP())); flag |= CharacterStatType.HP;
                        break;
                    case "mp":
                        stat.setMP(Math.min(val, stat.getMMP())); flag |= CharacterStatType.MP;
                        break;
                }
            }
            if (flag != 0) {
                user.addCharacterDataMod(DBChar.Character);
                user.sendCharacterStat(Request.Excl, flag);
                user.validateStat(false);
                user.sendSystemMessage(String.format("Your %s has been updated.", args[0].toUpperCase()));
                return null;
            }
        }
        return "!stat <str/dex/int/luk/hp/mp/mhp/mmp> <value>";
    }
    
    @CommandDesc("Displays the map ID that you are currently on")
    public static String whereAmI(User user, CharacterStat stat, String[] args) {
        user.sendSystemMessage("You are currently on field " + stat.getPosMap());
        return null;
    }
    
    @CommandDesc("Displays your current position in the map")
    public static String pos(User user, Field field, String[] args) {
        int x = user.getCurrentPosition().x;
        int y = user.getCurrentPosition().y;
        int fh = field.getSpace2D().getFootholdUnderneath(x, y).getSN();
        user.sendSystemMessage("Your character's current position is (" + x + "," + y + ") on foothold (" + fh + ")");
        return null;
    }
    
    @CommandDesc("Spawn mob(s) at your current location")
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
        return "!spawn <mobID> [count]";
    }
    
    @CommandDesc("Spawn an NPC at your current location")
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
        return "!npc <id>";
    }
    
    @CommandDesc("Item Vac. 'nuff said")
    public static String itemVac(User user, Field field, DropPool dropPool, String[] args) {
        dropPool.getDrops().values().removeIf(drop -> {
            if (user.sendDropPickUpResultPacket(drop, Request.None)) {
                for (FieldSplit split : drop.getSplits()) {
                    field.splitUnregisterFieldObj(split, FieldSplit.Drop, drop, drop.makeLeaveFieldPacket(Drop.PickedUpByUser, user.getCharacterID()));
                }
                return true;
            }
            return false;
        });
        return null;
    }
}
