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
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * Admin promote command.
 *
 * @author Cyrakuse
 * @modified By Aionchs-Wylovech
 */
public class Promote extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length != 3) {
            admin.sendMsg("syntax //promote <characterName> <accesslevel | membership> <mask> ");
            return;
        }

        int mask;
        try {
            mask = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            admin.sendMsg("Only number!");
            return;
        }

        int type;
        if (params[1].toLowerCase().equals("accesslevel")) {
            type = 1;
            if (mask > 3 || mask < 0) {
                admin.sendMsg("accesslevel can be 0 - 3");
                return;
            }
        } else if (params[1].toLowerCase().equals("membership")) {
            type = 2;
            if (mask > 10 || mask < 0) {
                admin.sendMsg("membership can be 0 - 10");
                return;
            }
        } else {
            admin.sendMsg("syntax //promote <characterName> <accesslevel | membership> <mask>");
            return;
        }

        Player player = World.getInstance().findPlayer(params[0]);
        if (player == null) {
            admin.sendMsg("The specified player is not online.");
            return;
        }
        LoginServer.getInstance().sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), mask, type);

    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //promote <characterName> <accesslevel | membership> <mask> ");
    }
}
