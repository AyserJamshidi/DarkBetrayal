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
import com.ne.gs.world.WorldMapType;

/**
 * Admin moveto command
 *
 * @author KID
 */
public class MoveTo extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 4) {
            admin.sendMsg("syntax //moveto worldId X Y Z");
            return;
        }

        int worldId;
        float x, y, z;

        try {
            worldId = Integer.parseInt(params[0]);
            x = Float.parseFloat(params[1]);
            y = Float.parseFloat(params[2]);
            z = Float.parseFloat(params[3]);
        } catch (NumberFormatException e) {
            admin.sendMsg("All the parameters should be numbers");
            return;
        }

        if (WorldMapType.of(worldId) == null) {
            admin.sendMsg("Illegal WorldId %d " + worldId);
        } else {
            TeleportService.teleportTo(admin, worldId, x, y, z);
            admin.sendMsg("Teleported to " + x + " " + y + " " + z + " [" + worldId + "]");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //moveto worldId X Y Z");
    }
}
