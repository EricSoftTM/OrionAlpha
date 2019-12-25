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

import game.user.User;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Arnah
 */
public class CommandHandler {

    /**
     * @param input Command input with a prefix.
     */
    public static void handle(User user, String input) {
        String[] split = input.split(" ");
        String command = split[0].substring(1);//input is expected to have some type of command prefix.
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        for (UserGradeCode role : UserGradeCode.values()) {
            if (user.getGradeCode() < role.getGrade()) {
                break;
            }
            Class<?> clazz;
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
            try {
                for (Method method : clazz.getMethods()) {
                    String usedAlias = null;
                    CommandAlias alias = method.getAnnotation(CommandAlias.class);
                    if (alias != null) {
                        for (String s : alias.value()) {
                            if (s.equalsIgnoreCase(command)) {
                                usedAlias = s;
                                break;
                            }
                        }
                    }
                    if (usedAlias != null || method.getName().equalsIgnoreCase(command)) {
                        commandMethod = method;
                        // Only do the field lookup if we actually have to.
                        // We automatically fill in any User or String[] paremeters with the sender &
                        // args.

                        if (method.getParameters().length == 0) {//No parameters, just pass it.
                            break;
                        }
                        int index = -1;
                        methodArgs = new Object[method.getParameters().length];
                        boolean filledAlias = alias == null;
                        for (Parameter param : method.getParameters()) {
                            index++;
                            if (param.getType().isAssignableFrom(User.class)) {// Automatically fill in User parameter
                                methodArgs[index] = user;
                                continue;
                            }
                            if (param.getType().isAssignableFrom(String[].class)) {// Automatically fill in String[] parameter.
                                methodArgs[index] = args;
                                continue;
                            }
                            //Assumes if we are using an Alias annotation that the first String is used
                            //for that alias.
                            if (!filledAlias && param.getType().isAssignableFrom(String.class)) {
                                if (usedAlias != null) {
                                    methodArgs[index] = usedAlias;
                                } else {//Used method name not alias.
                                    methodArgs[index] = method.getName();
                                }
                                filledAlias = true;
                                continue;
                            }
                            List<Field> checked = new ArrayList<>();
                            Object value = findFieldFor(user, checked, param.getType());
                            if (value == null) {
                                // System.out.println("Failed to find " + param.getType().getSimpleName());
                                checked.clear();
                                continue;
                            }
                            methodArgs[index] = value;
                            checked.clear();
                        }
                        // If the current method has all args filled in
                        // we break so we don't attempt a 2nd method with same name.
                        int valid = 0;
                        for (Object obj : methodArgs) {
                            if(obj != null) {
                                valid++;
                            }
                        }
                        if (valid == method.getParameters().length) {
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                continue;
            }
            if (commandMethod != null) {
                try {
                    Object ret = null;
                    int validArgs = 0;
                    if(methodArgs != null) {
                        for (Object obj : methodArgs) {
                            if (obj == null) {
                                continue;
                            }
                            validArgs++;
                        }
                    }
                    if (validArgs == commandMethod.getParameterCount()) {
                        ret = commandMethod.invoke(null, methodArgs);//Does it matter if methodArgs is null?
                    } else {
                        user.sendSystemMessage("Failed to execute the command.");
                    }

                    if (ret instanceof String) {
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
