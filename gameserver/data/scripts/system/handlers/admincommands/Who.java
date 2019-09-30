/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

public class Who extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        admin.sendMsg("List players :");

        for (Player player : World.getInstance().getAllPlayers()) {
            if (params.length > 0) {
                String cmd = params[0].toLowerCase();

                if ("ely".startsWith(cmd)) {
                    if (player.getRace() == Race.ASMODIANS) {
                        continue;
                    }
                }

                if ("asmo".startsWith(cmd)) {
                    if (player.getRace() == Race.ELYOS) {
                        continue;
                    }
                }

                if ("member".startsWith(cmd) || "premium".startsWith(cmd)) {
                    if (player.getPlayerAccount().getMembership() == 0) {
                        continue;
                    }
                }

                if ("gm".startsWith(cmd) || "admin".startsWith(cmd)) {
                    if (player.getPlayerAccount().getAccessLevel() == 0) {
                        continue;
                    }
                }
            }

            admin.sendMsg("Char: " + player.getName() + " - Race: " + player.getRace().name() + " - Acc: " + player.getAcountName());
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //who [ely|asmo|premium|gm]");
    }

}
