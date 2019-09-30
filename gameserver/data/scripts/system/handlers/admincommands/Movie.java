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
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author d3v1an
 */
public class Movie extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            onError(player, null);
        } else {
            player.sendPck(new SM_PLAY_MOVIE(Integer.parseInt(params[0]), Integer.parseInt(params[1])));
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("//movie <type> <id>");
    }
}
