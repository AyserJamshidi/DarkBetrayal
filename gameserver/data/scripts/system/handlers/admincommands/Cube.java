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
import com.ne.gs.services.CubeExpandService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Kamui
 */
public class Cube extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        if (player.getNpcExpands() >= 9) {
            // FIXME what a f*** ^D
            player.sendMsg("Aucune extension n'est disponible pour votre inventaire.");
            return;
        }
        while (player.getNpcExpands() < 9) {
            CubeExpandService.expand(player, true);
        }
        // FIXME what a f*** ^D
        player.sendMsg("Vous venez de recevoir toutes les extensions de votre inventaire.");
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: .cube");
    }
}
