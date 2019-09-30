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
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Nemiroff Date: 11.01.2010
 */
public class Unstuck extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (player.getLifeStats().isAlreadyDead()) {
            player.sendMsg("You dont have execute this command. You die");
            return;
        }
        if (player.isInPrison()) {
            player.sendMsg("You can't use the unstuck command when you are in Prison");
            return;
        }

        TeleportService.moveToBindLocation(player, true, CustomConfig.UNSTUCK_DELAY);
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
