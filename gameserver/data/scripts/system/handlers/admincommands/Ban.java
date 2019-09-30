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
import com.ne.gs.world.World;

/**
 * @author Watson
 */
public class Ban extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("Syntax: //ban <player> [account|ip|full] [time in minutes]");
            return;
        }

        // We need to get player's account ID
        String name = params[0];
        int accountId = 0;
        String accountIp = "";

        // First, try to find player in the World
        Player player = World.getInstance().findPlayer(name);
        if (player != null) {
            accountId = player.getClientConnection().getAccount().getId();
            accountIp = player.getClientConnection().getIP();
        }

        // Second, try to get account ID of offline player from database
        if (accountId == 0) {
            accountId = GDB.get(PlayerDAO.class).getAccountIdByName(name);
        }

        // Third, fail
        if (accountId == 0) {
            admin.sendMsg("Player " + name + " was not found!");
            admin.sendMsg("Syntax: //ban <player> [account|ip|full] [time in minutes]");
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
                admin.sendMsg("Syntax: //ban <player> [account|ip|full] [time in minutes]");
                return;
            }
        }

        int time = 0; // Default: infinity
        if (params.length > 2) {
            try {
                time = Integer.parseInt(params[2]);
            } catch (NumberFormatException e) {
                admin.sendMsg("Syntax: //ban <player> [account|ip|full] [time in minutes]");
                return;
            }
        }

        LoginServer.getInstance().sendBanPacket(type, accountId, accountIp, time, admin.getObjectId());
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //ban <player> [account|ip|full] [time in minutes]");
    }
}
