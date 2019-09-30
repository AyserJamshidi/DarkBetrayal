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
import com.ne.gs.world.World;

/**
 * @author xavier
 */
public class AddTitle extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1 || params.length > 2) {
            onError(player, null);
            return;
        }

        int titleId = Integer.parseInt(params[0]);
        if (titleId > 161 || titleId < 1) {
            player.sendMsg("title id " + titleId + " is invalid (must be between 1 and 161)");
            return;
        }

        Player target = null;
        if (params.length == 2) {
            target = World.getInstance().findPlayer(params[1]);
            if (target == null) {
                player.sendMsg("player " + params[1] + " was not found");
                return;
            }
        } else {
            VisibleObject creature = player.getTarget();
            if (player.getTarget() instanceof Player) {
                target = (Player) creature;
            }

            if (target == null) {
                target = player;
            }
        }

        if (titleId < 162) {
            titleId = target.getRace().getRaceId() * 161 + titleId;
        }

        if (!target.getTitleList().addTitle(titleId, false, 0)) {
            String msg = "you can't add title #" + titleId + " to " + (target.equals(player) ? "yourself" : target.getName());
            player.sendMsg(msg);
        } else if (target.equals(player)) {
            player.sendMsg("you added to yourself title #" + titleId);
        } else {
            player.sendMsg("you added to " + target.getName() + " title #" + titleId);
            target.sendMsg(player.getName() + " gave you title #" + titleId);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //addtitle title_id [playerName]");
    }
}
