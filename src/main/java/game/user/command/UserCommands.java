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
import game.user.User;

import java.lang.reflect.Method;

/**
 * @author Arnah
 */
public class UserCommands {

    @CommandAlias({"dispose", "excl", "check"})
    public static String fixme(User user) {
        if (user.getScriptVM() != null) {
            user.setScriptVM(null);
        }
        user.sendCharacterStat(Request.Excl, 0);
        return null;
    }

    public static String help(User user) {
        for (UserGradeCode role : UserGradeCode.values()) {
            if (user.getGradeCode() < role.getGrade()) {
                continue;
            }
            Class<?> clazz;
            try {
                clazz = Class.forName("game.user.command." + role.name() + "Commands");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace(System.err);
                continue;
            }
            user.sendSystemMessage(role.name());
            for (Method method : clazz.getMethods()) {
            	if (!CommandHandler.isValidCommandMethod(method)) {
            		continue;
	            }
            	
            	StringBuilder sb = new StringBuilder("@");
            	sb.append(method.getName());
            	
                CommandDesc desc = method.getAnnotation(CommandDesc.class);
                if (desc != null) {
                	sb.append(" - ").append(desc.value());
                }
                
                user.sendSystemMessage(sb.toString());
            }
        }
        return null;
    }
}
