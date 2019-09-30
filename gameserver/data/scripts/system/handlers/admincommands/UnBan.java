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
import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Watson
 */
public class UnBan extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("Syntax: //unban <player> [account|ip|full]");
            return;
        }

        // Banned player must be offline, so get his account ID from database
        String name = params[0];
        int accountId = GDB.get(PlayerDAO.class).getAccountIdByName(name);
        if (accountId == 0) {
            admin.sendMsg("Player " + name + " was not found!");
            admin.sendMsg("Syntax: //unban <player> [account|ip|full]");
            return;
        }

        byte type = 3; // Default: full
        if (params.length > 1) {
            // Smart Matching
            String stype = params[1].toLowerCase();
            if ("account".startsWith(stype)) {
                type = 1;
            } else if ("ip".startsWith(stype)) {
                type = 2;
            } else if ("full".startsWith(stype)) {
                type = 3;
            } else {
                admin.sendMsg("Syntax: //unban <player> [account|ip|full]");
                return;
            }
        }

        // Sends time -1 to unban
        LoginServer.getInstance().sendBanPacket(type, accountId, "", -1, admin.getObjectId());
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //unban <player> [account|ip|full]");
    }
}
