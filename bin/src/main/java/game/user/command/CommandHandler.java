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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.user.User;

/**
 * @author Arnah
 */
public class CommandHandler {

    public static void handle(User user, String input) {
        String[] args = new String[input.split(" ").length];
        String val = "";
        int index = 0;
        for (String s : input.split(" ")) {
            // Combines any strings inbetween quotes into one string in the array.
            if (!val.isEmpty() || s.contains("\"")) {
                boolean end = !val.isEmpty() && s.contains("\"");
                if (!val.isEmpty()) {
                    val += " ";// Add a space inbetween the text in the quote.
                }
                val += s.replace("\"", "");
                if (end || hasDoubleQuote(s)) {// Had a start and end quote so we are good.
                    args[index++] = val;
                    val = "";
                }
            } else {
                args[index++] = s;
            }
        }
        // If for some reason the val still has data we append it.
        // Can happen if someone doesn't include an ending quote.
        if (!val.isEmpty())
            args[index++] = val;
        String command = args[0].replace("!", "").replace("@", "");
        args = Arrays.copyOfRange(args, 1, index);// Cuts off the nulls created if quotes were used.
        for (UserGradeCode role : UserGradeCode.values()) {
            if (user.getGradeCode() < role.getGrade()) {
                break;
            }
            Class<?> clazz = null;
            try {
                // Since in java we aren't able to grab a list of classes under a package
                // I just do a list of 'grades' to check for specific class commands.
                clazz = Class.forName("game.user.command." + role.name() + "Commands");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace(System.err);
                continue;
            }

            Method commandMethod = null;
            Object[] methodArgs = null;
            boolean unknownParameters = false;
            try {
                // Allow the method to accept any values in user.
                // method = clazz.getMethod(command, User.class, String[].class);
                for (Method method : clazz.getMethods()) {
                    if (method.getName().equals(command)) {
                        commandMethod = method;
                        // Only do the field lookup if we actually have to.
                        // We automatically fill in any User or String[] paremeters with the sender &
                        // args.

                        for (Class<?> c : method.getParameterTypes()) {
                            if (!c.equals(User.class) && !c.equals(String[].class)) {
                                unknownParameters = true;
                                break;
                            }
                        }
                        if (!unknownParameters && method.getParameterCount() == 2)
                            break;
                        index = -1;
                        methodArgs = new Object[method.getParameters().length];
                        for (Parameter param : method.getParameters()) {
                            index++;
                            if (param.getType().isAssignableFrom(User.class)) {// Automatically fill in User parameter
                                methodArgs[index] = user;
                                continue;
                            }
                            if (param.getType().isAssignableFrom(String[].class)) {// Automatically fill in String[]
                                                                                   // parameter.
                                methodArgs[index] = args;
                                continue;
                            }
                            if (unknownParameters) {
                                List<Field> checked = new ArrayList<>();
                                Object value = findFieldFor(user, checked, param.getType());
                                if (value == null) {
                                    System.out.println("Failed to find " + param.getType().getSimpleName());
                                    checked.clear();
                                    continue;
                                }
                                methodArgs[index] = value;
                                checked.clear();
                            }
                        }
                    }
                }
            } catch (SecurityException ex) {
                ex.printStackTrace(System.err);
                continue;
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                continue;
            }
            if (commandMethod != null) {
                try {
                    Object ret = null;
                    if (methodArgs == null || !unknownParameters) {
                        ret = commandMethod.invoke(null, user, args);
                    } else {
                        int validArgs = 0;
                        for (Object obj : methodArgs) {
                            if (obj == null) {
                                continue;
                            }
                            validArgs++;
                        }
                        if (validArgs == commandMethod.getParameterCount()) {
                            ret = commandMethod.invoke(null, methodArgs);
                        } else {
                            user.sendSystemMessage("Failed to execute the command.");
                        }
                    }
                    if (ret != null && ret instanceof String) {
                        user.sendSystemMessage(ret.toString());
                    }
                    methodArgs = null;
                    return;
                } catch (Exception ex) {
                    user.sendSystemMessage("Failed to execute the command.");
                    ex.printStackTrace(System.err);
                }
                break;
            }
        }
    }

    private static Object findFieldFor(Object parent, List<Field> checked, Class<?> target) throws Exception {
        // I couldn't find a clean way to prevent looping without using a List of
        // checked fields.
        List<Field> fields = new ArrayList<>();
        if (parent.getClass().getSuperclass() != null) {
            // Tried multiple ways of getting fields from super class related to casting and
            // this is the only way I could find that worked.
            Class<?> par = parent.getClass();
            while ((par = par.getSuperclass()) != null) {
                if (par.equals(Object.class)) {
                    break;
                }
                if (par.equals(Class.class)) {
                    break;
                }
                for (Field f : par.getDeclaredFields()) {
                    fields.add(f);
                }
            }
        }
        for (Field f : parent.getClass().getDeclaredFields()) {
            fields.add(f);
        }
        for (Field f : fields) {
            String typeName = f.getType().getTypeName();
            // Some checks to prevent wasting time looking at values we don't accept as a
            // parameter.
            if (!typeName.contains(".")) {
                continue;
            }
            if (typeName.contains("java")) {
                continue;
            }
            if (typeName.contains("sun")) {
                continue;
            }
            if (checked.contains(f)) {
                continue;
            }
            checked.add(f);
            f.setAccessible(true);
            if (f.getType().isAssignableFrom(target)) {// If the field is what we want grab it and return.
                fields.clear();
                return f.get(parent);
            }
            Object val = f.get(parent);
            if (val != null) {// Do some recursive of values inside fields so we can go from User > DropPool
                // of the users current field.
                if (val.getClass().equals(Class.class)) {
                    continue;
                }
                if (parent.getClass().equals(val.getClass())) {
                    continue;
                }
                Object found = findFieldFor(val, checked, target);
                if (found != null) {
                    fields.clear();
                    return found;
                }
            }
        }
        fields.clear();
        return null;
    }

    private static boolean hasDoubleQuote(String input) {
        int index = input.indexOf("\"");
        if (index == -1)
            return false;
        return input.indexOf("\"", index + 1) != -1;
    }
}
