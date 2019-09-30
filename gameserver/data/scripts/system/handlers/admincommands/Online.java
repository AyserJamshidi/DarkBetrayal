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
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author VladimirZ
 */
public class Online extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {

        int playerCount = GDB.get(PlayerDAO.class).getOnlinePlayerCount();

        if (playerCount == 1) {
            admin.sendMsg("There is " + playerCount + " player online !");
        } else {
            admin.sendMsg("There are " + playerCount + " players online !");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //online");
    }
}
