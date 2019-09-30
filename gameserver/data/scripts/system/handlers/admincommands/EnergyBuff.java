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
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_STATS_INFO;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Source
 */
public class EnergyBuff extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        VisibleObject target = player.getTarget();
        if (target == null) {
            player.sendMsg("No target selected");
            return;
        }

        Creature creature = (Creature) target;
        if (params.length < 1) {
            onError(player, null);
        } else if (target instanceof Player) {
            if (params[0].equals("repose")) {
                Player targetPlayer = (Player) creature;
                if (params[1].equals("info")) {
                    player.sendMsg("Current EoR: " + targetPlayer.getCommonData().getCurrentReposteEnergy() + "\n Max EoR: "
                        + targetPlayer.getCommonData().getMaxReposteEnergy());
                } else if (params[1].equals("add")) {
                    targetPlayer.getCommonData().addReposteEnergy(Long.parseLong(params[2]));
                } else if (params[1].equals("reset")) {
                    targetPlayer.getCommonData().setCurrentReposteEnergy(0);
                }
            } else if (params[0].equals("salvation")) {
                Player targetPlayer = (Player) creature;
                if (params[1].equals("info")) {
                    player.sendMsg("Current EoS: " + targetPlayer.getCommonData().getCurrentSalvationPercent());
                } else if (params[1].equals("add")) {
                    targetPlayer.getCommonData().addSalvationPoints(Long.parseLong(params[2]));
                } else if (params[1].equals("reset")) {
                    targetPlayer.getCommonData().resetSalvationPoints();
                }
            } else if (params[0].equals("refresh")) {
                Player targetPlayer = (Player) creature;
                targetPlayer.sendPck(new SM_STATS_INFO(targetPlayer));
            }
        } else {
            player.sendMsg("This is not player");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        String syntax = "//energy repose|salvation|refresh info|reset|add [points]";
        player.sendMsg(syntax);
    }

}
