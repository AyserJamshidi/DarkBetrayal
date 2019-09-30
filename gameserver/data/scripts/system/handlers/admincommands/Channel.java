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

import com.ne.commons.annotations.NotNull;
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author SheppeR
 */
public class Channel extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        Class<?> classToMofify = CustomConfig.class;
        Field someField;
        try {
            someField = classToMofify.getDeclaredField("FACTION_CMD_CHANNEL");
            if (params[0].equalsIgnoreCase("on") && !CustomConfig.FACTION_CMD_CHANNEL) {
                someField.set(null, true);
                player.sendMsg("The command .faction is ON.");
            } else if (params[0].equalsIgnoreCase("off") && CustomConfig.FACTION_CMD_CHANNEL) {
                someField.set(null, false);
                player.sendMsg("The command .faction is OFF.");
            }
        } catch (Exception e) {
            player.sendMsg("Error! Wrong property or value.");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //channel <On | Off>");
    }
}
