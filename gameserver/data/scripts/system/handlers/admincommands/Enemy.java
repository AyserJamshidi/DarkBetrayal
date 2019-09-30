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
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_MOTION;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_INFO;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Pan
 */
public class Enemy extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        String help = "Syntax: //enemy < players | npcs | all | cancel >\n" + "Players - You're enemy to Players of both factions.\n"
            + "Npcs - You're enemy to all Npcs and Monsters.\n" + "All - You're enemy to Players of both factions and all Npcs.\n"
            + "Cancel - Cancel all. Players and Npcs have default enmity to you.";

        if (params.length != 1) {
            onError(player, null);
            return;
        }

        String output = "You now appear as enemy to " + params[0] + ".";

        int neutralType = player.getAdminNeutral();

        if (params[0].equals("all")) {
            player.setAdminEnmity(3);
            player.setAdminNeutral(0);
        } else if (params[0].equals("players")) {
            player.setAdminEnmity(2);
            if (neutralType > 1) {
                player.setAdminNeutral(0);
            }
        } else if (params[0].equals("npcs")) {
            player.setAdminEnmity(1);
            if (neutralType == 1 || neutralType == 3) {
                player.setAdminNeutral(0);
            }
        } else if (params[0].equals("cancel")) {
            player.setAdminEnmity(0);
            output = "You appear regular to both Players and Npcs.";
        } else if (params[0].equals("help")) {
            player.sendMsg(help);
            return;
        } else {
            onError(player, null);
            return;
        }

        player.sendMsg(output);

        player.clearKnownlist();
        player.sendPck(new SM_PLAYER_INFO(player, false));
        player.sendPck(new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
        player.updateKnownlist();
    }

    @Override
    public void onError(Player player, Exception e) {
        String syntax = "Syntax: //enemy < players | npcs | all | cancel >\nIf you're unsure about what you want to do, type //enemy help";
        player.sendMsg(syntax);
    }
}
