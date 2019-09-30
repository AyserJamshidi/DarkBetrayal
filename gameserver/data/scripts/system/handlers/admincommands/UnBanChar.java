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
import com.ne.gs.services.PunishmentService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author nrg
 */
public class UnBanChar extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("Syntax: //unbanchar <player>");
            return;
        }

        // Banned player must be offline
        String name = params[0];
        int playerId = GDB.get(PlayerDAO.class).getPlayerIdByName(name);
        if (playerId == 0) {
            admin.sendMsg("Player " + name + " was not found!");
            admin.sendMsg("Syntax: //unbanchar <player>");
            return;
        }

        admin.sendMsg("Character " + name + " is not longer banned!");

        PunishmentService.unbanChar(playerId);
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //unban <player> [account|ip|full]");
    }
}
