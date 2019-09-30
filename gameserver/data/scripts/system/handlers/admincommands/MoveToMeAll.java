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
import com.ne.gs.model.Race;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_SPAWN;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Shepper Helped by @alfa24t
 */
public class MoveToMeAll extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("syntax //movetomeall < all | elyos | asmos >");
            return;
        }

        if (params[0].equals("all")) {
            for (Player p : World.getInstance().getAllPlayers()) {
                if (!p.equals(admin)) {
                    TeleportService.teleportTo(p, admin.getWorldId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(),
                        TeleportAnimation.BEAM_ANIMATION);
                    p.sendPck(new SM_PLAYER_SPAWN(p));

                    admin.sendMsg("Player " + p.getName() + " teleported.");
                    p.sendMsg("Teleportd by " + admin.getName() + ".");
                }
            }
        }

        if (params[0].equals("elyos")) {
            for (Player p : World.getInstance().getAllPlayers()) {
                if (!p.equals(admin)) {
                    if (p.getRace() == Race.ELYOS) {
                        TeleportService.teleportTo(p, admin.getWorldId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(),
                            TeleportAnimation.BEAM_ANIMATION);
                        p.sendPck(new SM_PLAYER_SPAWN(p));

                        admin.sendMsg("Player " + p.getName() + " teleported.");
                        p.sendMsg("Teleportd by " + admin.getName() + ".");
                    }
                }
            }
        }

        if (params[0].equals("asmos")) {
            for (Player p : World.getInstance().getAllPlayers()) {
                if (!p.equals(admin)) {
                    if (p.getRace() == Race.ASMODIANS) {
                        TeleportService.teleportTo(p, admin.getWorldId(), admin.getX(), admin.getY(), admin.getZ(), admin.getHeading(),
                            TeleportAnimation.BEAM_ANIMATION);
                        p.sendPck(new SM_PLAYER_SPAWN(p));

                        admin.sendMsg("Player " + p.getName() + " teleported.");
                        p.sendMsg("Teleportd by " + admin.getName() + ".");
                    }
                }
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //movetomeall < all | elyos | asmos >");
    }
}
