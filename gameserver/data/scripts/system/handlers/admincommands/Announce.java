/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.Iterator;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Ben, Ritsu Smart Matching Enabled //announce anon This will work. as well as //announce a
 *         This will work. Both will match the "a" or "anon" to the "anonymous" flag.
 */
public class Announce extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        String message;

        if ("anonymous".startsWith(params[0].toLowerCase())) {
            message = "Announce: ";
        } else if ("name".startsWith(params[0].toLowerCase())) {
            message = player.getName() + ": ";
        } else {
            player.sendMsg("Syntax: //announce <anonymous|name> <message>");
            return;
        }

        // Add with space
        for (int i = 1; i < params.length - 1; i++) {
            message += params[i] + " ";
        }

        // Add the last without the end space
        message += params[params.length - 1];

        Iterator<Player> iter = World.getInstance().getPlayersIterator();

        while (iter.hasNext()) {
            PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), message);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //announce <anonymous|name> <message>");
    }
}
