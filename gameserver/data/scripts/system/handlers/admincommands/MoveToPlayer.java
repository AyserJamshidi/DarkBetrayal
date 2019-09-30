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
 * Admin movetoplayer command.
 *
 * @author Tanelorn
 */
public class MoveToPlayer extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("syntax //movetoplayer characterName");
            return;
        }

        Player player = World.getInstance().findPlayer(params[0]);
        if (player == null) {
            admin.sendMsg("The specified player is not online.");
            return;
        }

        if (player == admin) {
            admin.sendMsg("Cannot use this command on yourself.");
            return;
        }

        TeleportService.teleportTo(admin, player.getWorldId(), player.getInstanceId(), player.getX(), player.getY(), player.getZ(), player.getHeading(),
            TeleportAnimation.BEAM_ANIMATION);
        admin.sendMsg("Teleported to player " + player.getName() + ".");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //movetoplayer characterName");
    }

}
