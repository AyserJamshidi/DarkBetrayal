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
 * @author Sarynth, (edited by Pan)
 */
public class Neutral extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        String help = "Syntax: //neutral < players | npcs | all | cancel >\n" + "Players - You're neutral to Players of both factions.\n"
            + "Npcs - You're neutral to all Npcs and Monsters.\n" + "All - You're neutral to Players of both factions and all Npcs.\n"
            + "Cancel - Cancel all. Players and Npcs have default enmity to you.";

        if (params.length != 1) {
            onError(admin, null);
            return;
        }

        String output = "You now appear neutral to " + params[0] + ".";

        int enemyType = admin.getAdminEnmity();

        if (params[0].equals("all")) {
            admin.setAdminNeutral(3);
            admin.setAdminEnmity(0);
        } else if (params[0].equals("players")) {
            admin.setAdminNeutral(2);
            if (enemyType > 1) {
                admin.setAdminEnmity(0);
            }
        } else if (params[0].equals("npcs")) {
            admin.setAdminNeutral(1);
            if (enemyType == 1 || enemyType == 3) {
                admin.setAdminEnmity(0);
            }
        } else if (params[0].equals("cancel")) {
            admin.setAdminNeutral(0);
            output = "You appear regular to both Players and Npcs.";
        } else if (params[0].equals("help")) {
            admin.sendMsg(help);
            return;
        } else {
            onError(admin, null);
            return;
        }

        admin.sendMsg(output);

        admin.clearKnownlist();
        admin.sendPck(new SM_PLAYER_INFO(admin, false));
        admin.sendPck(new SM_MOTION(admin.getObjectId(), admin.getMotions().getActiveMotions()));
        admin.updateKnownlist();
    }

    @Override
    public void onError(Player player, Exception e) {
        String syntax = "Syntax: //neutral < players | npcs | all | cancel >\n" + "If you're unsure about what you want to do, type //neutral help";
        player.sendMsg(syntax);
    }

}
