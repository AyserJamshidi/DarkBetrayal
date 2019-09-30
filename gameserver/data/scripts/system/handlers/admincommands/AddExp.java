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
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Wakizashi
 */
public class AddExp extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length != 1) {
            onError(player, null);
            return;
        }

        Player target = null;
        VisibleObject creature = player.getTarget();

        if (player.getTarget() instanceof Player) {
            target = (Player) creature;
        }

        String paramValue = params[0];
        long exp;
        try {
            exp = Long.parseLong(paramValue);
        } catch (NumberFormatException e) {
            player.sendMsg("<exp> must be an Integer");
            return;
        }

        exp += target.getCommonData().getExp();
        target.getCommonData().setExp(exp);
        player.sendMsg("You added " + params[0] + " exp points to the target.");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //addexp <exp>");
    }
}
