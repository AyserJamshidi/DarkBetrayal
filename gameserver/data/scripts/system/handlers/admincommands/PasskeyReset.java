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
import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.database.dao.PlayerPasskeyDAO;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author cura
 */
public class PasskeyReset extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            player.sendMsg("syntax: //passkeyreset <player> <passkey>");
            return;
        }

        String name = params[0];
        int accountId = GDB.get(PlayerDAO.class).getAccountIdByName(name);
        if (accountId == 0) {
            player.sendMsg("player " + name + " can't find!");
            player.sendMsg("syntax: //passkeyreset <player> <passkey>");
            return;
        }

        try {
            Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            player.sendMsg("parameters should be number!");
            return;
        }

        String newPasskey = params[1];
        if (!(newPasskey.length() > 5 && newPasskey.length() < 9)) {
            player.sendMsg("passkey is 6~8 digits!");
            return;
        }

        GDB.get(PlayerPasskeyDAO.class).updateForcePlayerPasskey(accountId, newPasskey);
        LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", -1, player.getObjectId());
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax: //passkeyreset <player> <passkey>");
    }
}
