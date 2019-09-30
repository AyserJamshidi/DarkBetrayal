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
import com.ne.gs.network.aion.serverpackets.SM_RESURRECT;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Sarynth
 */
public class Res extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        VisibleObject target = admin.getTarget();
        if (target == null) {
            admin.sendMsg("No target selected.");
            return;
        }

        if (!(target instanceof Player)) {
            admin.sendMsg("You can only resurrect other players.");
            return;
        }

        Player player = (Player) target;
        if (!player.getLifeStats().isAlreadyDead()) {
            admin.sendMsg("That player is already alive.");
            return;
        }

        // Default action is to prompt for resurrect.
        if (params.length == 0 || "prompt".startsWith(params[0])) {
            player.setPlayerResActivate(true);
            player.sendPck(new SM_RESURRECT(admin));
            return;
        }

        if ("instant".startsWith(params[0])) {
            PlayerReviveService.skillRevive(player);
            return;
        }

        admin.sendMsg("[Resurrect] Usage: target player and use //res <instant|prompt>");
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
