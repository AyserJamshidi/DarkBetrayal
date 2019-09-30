/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.Iterator;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * Admin notice command
 *
 * @author Jenose Updated By Darkwolf
 */
public class Notice extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {

        String message = "";

        try {
            for (String param : params) {
                message += " " + param;
            }
        } catch (NumberFormatException e) {
            player.sendMsg("Parameters should be text or number !");
            return;
        }
        Iterator<Player> iter = World.getInstance().getPlayersIterator();

        while (iter.hasNext()) {
            PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), "Information: " + message);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //notice <message>");
    }
}
