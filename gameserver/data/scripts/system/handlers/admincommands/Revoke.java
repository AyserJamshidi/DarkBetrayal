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
 * Admin revoke command.
 *
 * @author Cyrakuse
 * @modified By Aionchs-Wylovech
 */
public class Revoke extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length != 2) {
            admin.sendMsg("syntax //revoke <characterName> <acceslevel | membership>");
            return;
        }

        int type;
        if (params[1].toLowerCase().equals("acceslevel")) {
            type = 1;
        } else if (params[1].toLowerCase().equals("membership")) {
            type = 2;
        } else {
            admin.sendMsg("syntax //revoke <characterName> <acceslevel | membership>");
            return;
        }

        Player player = World.getInstance().findPlayer(params[0]);
        if (player == null) {
            admin.sendMsg("The specified player is not online.");
            return;
        }
        LoginServer.getInstance().sendLsControlPacket(player.getAcountName(), player.getName(), admin.getName(), 0, type);
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //revoke <characterName> <acceslevel | membership>");
    }
}
