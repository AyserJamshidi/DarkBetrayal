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
import com.ne.gs.network.aion.serverpackets.SM_QUIT_RESPONSE;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Elusive
 */
public class Kick extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("syntax //kick <character_name> | <All>");
            return;
        }

        if (params[0] != null && "All".equalsIgnoreCase(params[0])) {
            for (Player player : World.getInstance().getAllPlayers()) {
                if (!player.isGM()) {
                    player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
                    admin.sendMsg("Kicked player : " + player.getName());
                }
            }
        } else {
            Player player = World.getInstance().findPlayer(params[0]);
            if (player == null) {
                admin.sendMsg("The specified player is not online.");
                return;
            }
            player.getClientConnection().close(new SM_QUIT_RESPONSE(), false);
            admin.sendMsg("Kicked player : " + player.getName());
        }

    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //kick <character_name> | <All>");
    }
}
