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
 * @author Source
 */
public class GroupToMe extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            onError(admin, null);
            return;
        }

        Player groupToMove = World.getInstance().findPlayer(params[0]);
        if (groupToMove == null) {
            admin.sendMsg("The player is not online.");
            return;
        }

        if (!groupToMove.isInGroup2()) {
            admin.sendMsg(groupToMove.getName() + " is not in group.");
            return;
        }

        for (Player target : groupToMove.getPlayerGroup2().getMembers()) {
            if (target != admin) {
                TeleportService.teleportTo(target, admin.getWorldId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(),
                    TeleportAnimation.BEAM_ANIMATION);
                target.sendMsg("You have been summoned by " + admin.getName() + ".");
                admin.sendMsg("You summon " + target.getName() + ".");
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //grouptome <player>");
    }
}
