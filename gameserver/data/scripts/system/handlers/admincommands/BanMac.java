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
import com.ne.gs.network.BannedMacManager;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author KID
 */
public class BanMac extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            player.sendMsg("Syntax: //banmac <mac> [time in minutes]");
            return;
        }

        String address = params[0];

        int time = 0; // Default: infinity
        if (params.length > 1) {
            try {
                time = Integer.parseInt(params[1]);
            } catch (NumberFormatException e) {
                player.sendMsg("Syntax: //banmac <mac> [time in minutes]");
                return;
            }
        }

        VisibleObject target = player.getTarget();
        String targetName = "direct_type";
        if (target != null && target instanceof Player) {
            if (target.getObjectId().equals(player.getObjectId())) {
                player.sendMsg("omg, disselect yourself please.");
                return;
            }

            Player targetpl = (Player) target;
            address = targetpl.getClientConnection().getMacAddress();
            targetName = targetpl.getName();
            targetpl.getClientConnection().closeNow();
        }

        BannedMacManager.getInstance().banAddress(address, System.currentTimeMillis() + time * 60000,
            "author=" + player.getName() + ", " + player.getObjectId() + "; target=" + targetName);
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //banmac <mac> [time in minutes]");
    }

}
