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
import game.field.Field;
import game.field.FieldPacket;
import game.field.life.LifePool;
import game.field.life.mob.Mob;
import game.field.life.mob.MobStatOption;
import game.party.PartyData;
import game.party.PartyPacket;
import game.user.User;

/**
 * @author Arnah
*/
public class UserCommands {

    public static String fixme(User user, String[] args) {
        if (user.getScriptVM() != null) {
            user.setScriptVM(null);
        }
        user.sendCharacterStat(Request.Excl, 0);
        return null;
    }
    
    public static String say(User user, Field field, String[] args) {
        if (args.length > 0) {
            String text = args[0];
            
            user.getChannel().broadcast(FieldPacket.onGroupMessage(user.getCharacterName(), text));
            return null;
        }
        return "!say <message> - Broadcasts your message";
    }
    
    public static String party(User user, String[] args) {
        if (args.length > 0) {
            byte type = Byte.parseByte(args[0]);
            user.sendPacket(PartyPacket.onPartyResult(type));
        } else {
            PartyData pd = new PartyData();
            pd.getParty().getCharacterID().set(0, 1);
            pd.getParty().getCharacterName().set(0, "Eric");
            user.sendPacket(PartyPacket.onPartyResult(1, pd));
        }
        return null;
    }
    
    public static String packet(User user, LifePool pool, String[] args) {
        if (args.length > 0) {
            int flag = Integer.parseInt(args[0]);
            int skillID = 0;
            if (args.length > 1) {
                skillID = Integer.parseInt(args[1]);
            }
            int mobID = 100100;
            if (args.length > 2) {
                mobID = Integer.parseInt(args[2]);
            }
            
            MobStatOption opt = new MobStatOption();
            opt.setOption(1);
            opt.setReason(skillID);
            opt.setDuration(System.currentTimeMillis() + 1000 * 60);
            
            Mob mob = pool.getMobByTemplateID(mobID);
            if (mob != null) {
                mob.getMobStat().setStat(flag, opt);
                mob.sendMobTemporaryStatSet(flag, 0);
            } else {
                return "Unable to find mob.";
            }
        }
        return null;
    }
    
    public static String home(User user, String[] args) {
        user.postTransferField(100000000, "", false);
        return null;
    }

}
