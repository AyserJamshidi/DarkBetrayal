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
import com.ne.gs.world.World;

/**
 * @author nrg
 */
public class BanChar extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 3) {
            sendInfo(admin, true);
            return;
        }

        int playerId = 0;
        String playerName = params[0];

        // First, try to find player in the World
        Player player = World.getInstance().findPlayer(playerName);
        if (player != null) {
            playerId = player.getObjectId();
        }

        // Second, try to get player Id from offline player from database
        if (playerId == 0) {
            playerId = GDB.get(PlayerDAO.class).getPlayerIdByName(playerName);
        }

        // Third, fail
        if (playerId == 0) {
            admin.sendMsg("Player " + playerName + " was not found!");
            sendInfo(admin, true);
            return;
        }

        int dayCount;
        try {
            dayCount = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            admin.sendMsg("Second parameter is not an int");
            sendInfo(admin, true);
            return;
        }

        if (dayCount < 0) {
            admin.sendMsg("Second parameter has to be a positive daycount or 0 for infinity");
            sendInfo(admin, true);
            return;
        }

        String reason = params[2];
        for (int itr = 3; itr < params.length; itr++) {
            reason += " " + params[itr];
        }

        admin.sendMsg("Char " + playerName + " is now banned for the next " + dayCount + " days!");

        PunishmentService.banChar(playerId, dayCount, reason);
    }

    @Override
    public void onError(Player player, Exception e) {
        sendInfo(player, false);
    }

    private void sendInfo(Player player, boolean withNote) {
        player.sendMsg("Syntax: //banChar <playername> <days>/0 (for permanent) <reason>");
        if (withNote) {
            player.sendMsg("Note: the current day is defined as a whole day even if it has just a few hours left!");
        }
    }
}
