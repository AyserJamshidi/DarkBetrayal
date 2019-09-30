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

import com.ne.commons.Util;
import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.PunishmentService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author lord_rex Command: //sprison <player> <delay>(minutes) This command is sending player to
 *         prison.
 */
public class SPrison extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 2) {
            sendInfo(admin);
            return;
        }
        try {
            String playerName = params[0];
            Player playerToPrison = World.getInstance().findPlayer(playerName);
            int delay = Integer.parseInt(params[1]);

            String reason = Util.convertName(params[2]);
            for (int itr = 3; itr < params.length; itr++) {
                reason += " " + params[itr];
            }

            reason += "\nВыдал тюрьму: " + admin.getCustomTag(true) + admin.getName();

            if (playerToPrison != null) {
                PunishmentService.setIsInPrison(playerToPrison, true, delay, reason);
            } else {
                Integer playerObjectId = GDB.get(PlayerDAO.class).getPlayerObjectIdByName(playerName);
                if (playerObjectId == null) {
                    admin.sendMsg("Player " + playerName + " not found on server.");
                    return;
                }
                PunishmentService.setIsInPrisonOffline(playerObjectId, true, delay, reason);
            }
            String message = "Игроку " + playerName + " выдана тюрьма на " + delay + " мин. по причине: " + reason;
            admin.sendMsg(message);
            Iterator<Player> iter = World.getInstance().getPlayersIterator();
            while (iter.hasNext()) {
                PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), message);
            }
        } catch (Exception e) {
            sendInfo(admin);
        }

    }

    @Override
    public void onError(Player player, Exception e) {
        sendInfo(player);
    }

    private void sendInfo(Player player) {
        player.sendMsg("syntax //sprison <player> <delay> <reason>");
    }
}
