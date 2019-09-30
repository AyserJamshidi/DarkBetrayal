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
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * Admin moveplayertoplayer command.
 *
 * @author Tanelorn
 */
public class MovePlayerToPlayer extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 2) {
            admin.sendMsg("syntax //moveplayertoplayer <characterNameToMove> <characterNameDestination>");
            return;
        }

        Player playerToMove = World.getInstance().findPlayer(params[0]);
        if (playerToMove == null) {
            admin.sendMsg("The specified player is not online.");
            return;
        }

        Player playerDestination = World.getInstance().findPlayer(params[1]);
        if (playerDestination == null) {
            admin.sendMsg("The destination player is not online.");
            return;
        }

        if (playerToMove.getObjectId().equals(playerDestination.getObjectId())) {
            admin.sendMsg("Cannot move the specified player to their own position.");
            return;
        }

        TeleportService.teleportTo(playerToMove, playerDestination.getWorldId(), playerDestination.getX(), playerDestination.getY(), playerDestination.getZ(),
            playerDestination.getHeading(), TeleportAnimation.BEAM_ANIMATION);

        admin.sendMsg("Teleported player " + playerToMove.getName() + " to the location of player " + playerDestination.getName() + ".");
        playerToMove.sendMsg("You have been teleported by an administrator.");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //moveplayertoplayer <characterNameToMove> <characterNameDestination>");
    }
}
