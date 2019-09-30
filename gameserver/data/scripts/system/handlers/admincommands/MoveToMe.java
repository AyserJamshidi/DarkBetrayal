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
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * Admin movetome command.
 *
 * @author Cyrakuse
 */
public class MoveToMe extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            player.sendMsg("syntax //movetome <characterName>");
            return;
        }

        Player playerToMove = World.getInstance().findPlayer(params[0]);
        if (playerToMove == null) {
            player.sendMsg("The specified player is not online.");
            return;
        }

        if (playerToMove == player) {
            player.sendMsg("Cannot use this command on yourself.");
            return;
        }

        TeleportService.teleportBeam(playerToMove, player.getWorldId(), player.getInstanceId(), player.getX(), player.getY(), player.getZ());

        player.sendMsg("Teleported player " + playerToMove.getName() + " to your location.");
        playerToMove.sendMsg("You have been teleported by " + player.getName() + ".");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //movetome <characterName>");
    }
}
