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
import com.ne.gs.services.CubeExpandService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Kamui
 */
public class AddCube extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length != 1) {
            admin.sendMsg("Syntax: //addcube <player name>");
            return;
        }

        Player receiver;

        receiver = World.getInstance().findPlayer(params[0]);

        if (receiver == null) {
            admin.sendMsg("The player " + params[0] + " is not online.");
            return;
        }

        if (receiver.getNpcExpands() < 12) {
            CubeExpandService.expand(receiver, true);
            admin.sendMsg("9 cube slots successfully added to player " + receiver.getName() + "!");
            receiver.sendMsg("Admin " + admin.getName() + " gave you a cube expansion!");
        } else {
            admin.sendMsg("Cube expansion cannot be added to " + receiver.getName()
                    + "!\nReason: player cube already fully expanded.");
        }
    }

    @Override
    public void onError(Player admin, Exception e) {
        admin.sendMsg("Syntax: //addcube <player name>");
    }
}
