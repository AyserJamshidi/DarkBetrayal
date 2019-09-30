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
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Rolandas
 */
public class MoveToObject extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length != 1) {
            admin.sendMsg("Syntax : //movetoobj <object id>");
            return;
        }

        int objectId = 0;

        try {
            objectId = Integer.valueOf(params[0]);
        } catch (NumberFormatException e) {
            admin.sendMsg("Only numbers please!!!");
        }

        VisibleObject object = World.getInstance().findVisibleObject(objectId);
        if (object == null) {
            admin.sendMsg("Cannot find object for spawn #" + objectId);
            return;
        }

        VisibleObject spawn = object;

        TeleportService.teleportTo(admin, spawn.getWorldId(), spawn.getSpawn().getX(), spawn.getSpawn().getY(), spawn.getSpawn().getZ());
        admin.getController().stopProtectionActiveTask();
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax : //movetoobj <object id>");
    }

}
