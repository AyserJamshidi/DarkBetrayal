/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.ingameshop.InGameShopEn;
import com.ne.gs.services.abyss.AbyssPointsService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Tiger
 */
public class cmd_toll extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {
        if (params.length < 2) {
            player.sendMsg(".toll <ap | kinah> <value>" + "\nAp 1,000:1 : Kinah 10,000:1");
            return;
        }
        int toll;
        try {
            toll = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            return;
        }
        if (toll > 1000000) {
            player.sendMsg("Too large.");
            return;
        }
        if (params[0].equals("ap") && toll > 0) {
            int PlayerAbyssPoints = player.getAbyssRank().getAp();
            int pointsLost = toll * 1000;
            if (PlayerAbyssPoints < pointsLost) {
                player.sendMsg("You don't have enough Ap.");
                return;
            }
            AbyssPointsService.addAp(player, -pointsLost);
            addtoll(player, toll);
        } else if (params[0].equals("kinah") && toll > 0) {
            int pointsLost = toll * 10000;
            if (player.getInventory().getKinah() < pointsLost) {
                player.sendMsg("You don't have enough Kinah.");
                return;
            }
            player.getInventory().decreaseKinah(pointsLost);
            addtoll(player, toll);
        } else {
            player.sendMsg("value is incorrect.");
            return;
        }
    }

    private void addtoll(Player player, int toll) {
        InGameShopEn.getInstance().addToll(player, toll);
    }
}
