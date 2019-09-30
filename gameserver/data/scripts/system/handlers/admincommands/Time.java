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
import com.ne.gs.network.aion.serverpackets.SM_GAME_TIME;
import com.ne.gs.spawnengine.TemporarySpawnEngine;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.utils.gametime.GameTimeManager;
import com.ne.gs.world.World;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author Pan
 */
public class Time extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
        if (params.length < 1) {
            throw new Exception();
        }

        // Getting current hour and minutes
        int time = GameTimeManager.getGameTime().getHour();
        int min = GameTimeManager.getGameTime().getMinute();
        int hour;

        // If the given param is one of these four, get the correct hour...
        if (params[0].equals("night")) {
            hour = 22;
        } else if (params[0].equals("dusk")) {
            hour = 18;
        } else if (params[0].equals("day")) {
            hour = 9;
        } else if (params[0].equals("dawn")) {
            hour = 4;
        } else {
            // If not, check if the param is a number (hour)...
            try {
                hour = Integer.parseInt(params[0]);
            } catch (NumberFormatException e) {
                throw new Exception();
            }

            // A day have only 24 hours!
            if (hour < 0 || hour > 23) {
                throw new Exception();
            }
        }

        // Calculating new time in minutes...
        time = hour - time;
        time = GameTimeManager.getGameTime().getTime() + 60 * time - min;

        // Reloading the time, restarting the clock...
        GameTimeManager.reloadTime(time);

        // Checking the new daytime
        GameTimeManager.getGameTime().calculateDayTime();

        World.getInstance().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(new SM_GAME_TIME());
            }
        });
        TemporarySpawnEngine.spawnAll();

        admin.sendMsg("You changed the time to " + params[0] + ".");
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        String syntax = "Syntax: //time < dawn | day | dusk | night | desired hour (number) >";
        player.sendMsg(syntax);
    }

}
