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
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * Admin announce faction
 *
 * @author Divinity
 */
public class AnnounceFaction extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 2) {
            player.sendMsg("Syntax: //announcefaction <ely | asmo> <message>");
        } else {
            Iterator<Player> iter = World.getInstance().getPlayersIterator();
            if (params[0].equals("ely")) {
            } else {
            }

            // Add with space
            for (int i = 1; i < params.length - 1; i++) {
            }

            while (iter.hasNext()) {
                iter.next();

                // FIXME
                // if (target.getAccessLevel() > CommandsConfig.ANNONCEFACTION || target.getRace() == Race.ELYOS
                // && params[0].equals("ely"))
                // PacketSendUtility.sendBrightYellowMessageOnCenter(target, message);
                // else if (target.getAccessLevel() > CommandsConfig.ANNONCEFACTION
                // || target.getCommonData().getRace() == Race.ASMODIANS && params[0].equals("asmo"))
                // PacketSendUtility.sendBrightYellowMessageOnCenter(target, message);
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //announcefaction <ely | asmo> <message>");
    }
}
