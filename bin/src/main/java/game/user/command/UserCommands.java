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

import game.field.Field;
import game.field.FieldPacket;
import game.user.User;
import game.user.WvsContext;
import game.user.WvsContext.Request;

/**
 * @author Arnah
*/
public class UserCommands {

    public static String fixme(User user, String[] args) {
        if (user.getCharacterID() == 1 && !user.isGM()) {
            user.setGradeCode(UserGradeCode.Developer.getGrade());
        }
        if (user.getScriptVM() != null) {
            user.setScriptVM(null);
        }
        user.sendCharacterStat(Request.Excl, 0);
        return null;
    }
    
    public static String say(User user, Field field, String[] args) {
        if (args.length > 0) {
            String text = args[0];
            
            // TODO: Multi-channel broadcasting support.
            field.splitSendPacket(user.getSplit(), FieldPacket.onGroupMessage(user.getCharacterName(), text), null);
            return null;
        }
        return "!say <message> - Broadcasts your message";
    }

    public static String packet(User user, String[] args) {
        user.sendPacket(WvsContext.onIncEXPMessage(90));
        user.sendPacket(FieldPacket.onTransferFieldReqIgnored());
        return null;
    }
    
    public static String home(User user, String[] args) {
        user.postTransferField(100000000, "", false);
        return null;
    }

}
