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
import com.ne.gs.configs.administration.AdminConfig;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Imaginary
 */
public class AdminChat extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.commons.utils.AbstractCommand#runImpl(java.lang.Object, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
        if (!admin.isGM()) {
            admin.sendMsg("Vous devez etre au moins rang " + AdminConfig.GM_LEVEL + " pour utiliser cette commande");
            return;
        }
        if (admin.isGagged()) {
            admin.sendMsg("Vous avez ete reduit au silence ...");
            return;
        }

        StringBuilder sbMessage = new StringBuilder("[Admin] " + admin.getName() + " : ");

        for (String p : params) {
            sbMessage.append(p).append(" ");
        }
        String message = sbMessage.toString().trim();
        for (Player a : World.getInstance().getAllPlayers()) {
            if (a.isGM()) {
                PacketSendUtility.sendWhiteMessageOnCenter(a, message);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ne.commons.utils.AbstractCommand#onError(java.lang.Object, java.lang.Exception)
     */
    @Override
    public void onError(Player admin, Exception e) {
        admin.sendMsg("Syntax: //s <message>");
    }
}
