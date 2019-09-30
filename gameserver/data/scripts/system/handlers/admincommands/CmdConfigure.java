/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ne.commons.annotations.NotNull;
import com.ne.commons.configuration.Property;
import com.ne.commons.utils.ClassUtils;
import com.ne.gs.GameServer;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author hex1r0
 */
public class CmdConfigure extends ChatCommand {

    private static final String SYNTAX_FORMAT = "Syntax: //%s <get|set> <property> [<newvalue>]";

    private static List<Field> _fields = null;

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        String cmd = getCommand(params);
        if (cmd == null) {
            sendSyntax(player, alias);
            return;
        }

        String property = params[1];

        if (_fields == null) {
            buildFieldCache();
        }

        Field field = getField(property);

        if (field == null) {
            player.sendMsg("Error: Undefined property.");
        }

        if (cmd.startsWith("g")) {
            try {
                player.sendMsg(property + " = " + field.get(null));
            } catch (Exception e) {
                player.sendMsg("Error: Undefined value.");
            }
        } else if (cmd.startsWith("s")) {
            String newValue = params[2];
            try {
                Class<?> classType = field.getType();
                if (classType == String.class) {
                    field.set(null, newValue);
                } else if (classType == int.class || classType == Integer.class) {
                    field.set(null, Integer.parseInt(newValue));
                } else if (classType == Boolean.class || classType == boolean.class) {
                    field.set(null, Boolean.valueOf(newValue));
                } else if (classType == byte.class || classType == Byte.class) {
                    field.set(null, Byte.valueOf(newValue));
                } else if (classType == float.class || classType == Float.class) {
                    field.set(null, Float.valueOf(newValue));
                }

                player.sendMsg(property + " = " + field.get(null));
            } catch (Exception e) {
                player.sendMsg("Error: Undefined value.");
            }
        }
    }

    private String getCommand(String... params) {
        if (params.length == 2) { // get
            if (!params[0].startsWith("g")) {
                return null;
            }
        } else if (params.length == 3) { // set
            if (!params[0].startsWith("s")) {
                return null;
            }
        } else {
            return null;
        }
        return params[0];
    }

    private Field getField(String property) {
        for (Field f : _fields) {
            Property p = f.getAnnotation(Property.class);
            if (p.key().equalsIgnoreCase(property)) {
                return f;
            } else if (f.getName().equalsIgnoreCase(property)) {
                return f;
            }
        }
        return null;
    }

    private void buildFieldCache() {
        _fields = new ArrayList<>();
        Map<String, Class<?>> classes = ClassUtils.loadClasses(GameServer.class, "configs");
        for (Class<?> clazz : classes.values()) {
            for (Field f : clazz.getDeclaredFields()) {
                Property p = f.getAnnotation(Property.class);

                if (p == null) {
                    continue;
                }

                _fields.add(f);
            }
        }
    }

    public void sendSyntax(Player player, String alias) {
        player.sendMsg(String.format(SYNTAX_FORMAT, alias));
    }
}
