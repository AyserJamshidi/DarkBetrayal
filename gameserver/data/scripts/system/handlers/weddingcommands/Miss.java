/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package weddingcommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.DelayedGameAction;

/**
 * @author hex1r0
 */
public class Miss extends AbstractWeddingCommand {

    @Override
    public void runImpl(@NotNull final Player player, @NotNull String command, @NotNull String... params) {
        final Player partner = player.findPartner();

        if (!checkPreconditions(player, partner)) {
            return;
        }

        storeCooldown(player);

        new DelayedGameAction(player, 10000) {
            @Override
            protected void preRun() {
                getPlayer().sendMsg("Перемещение к игроку " + partner.getName() + ".");
            }

            @Override
            protected void postRun() {
                TeleportService.teleportBeam(getPlayer(), partner.getWorldId(), partner.getX(), partner.getY(), partner.getZ());
                getPlayer().sendMsg("Перемещено к игроку " + partner.getName() + ".");
            }
        }.invoke();
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Failed");
    }
}
