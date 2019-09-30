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
 * @author Watson, Jenelli
 */
public class Gag extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 2) {
            sendInfo(admin);
            return;
        }
        try {
            String playerName = params[0];
            Player playerGag = World.getInstance().findPlayer(playerName);
            int delay = Integer.parseInt(params[1]);

            String reason = Util.convertName(params[2]);
            for (int itr = 3; itr < params.length; itr++) {
                reason += " " + params[itr];
            }

            reason += "\nВыдал бан чата: " + admin.getCustomTag(true) + admin.getName();

            if (playerGag != null) {
                PunishmentService.setIsGag(playerGag, true, delay, reason);
            } else {
                Integer playerObjectId = GDB.get(PlayerDAO.class).getPlayerObjectIdByName(playerName);
                if (playerObjectId == null) {
                    admin.sendMsg("Player " + playerName + " not found on server.");
                    return;
                }
                PunishmentService.setIsGagOffline(playerObjectId, true, delay, reason);
            }
            String message = "Игроку " + playerName + " выдан бан чата на " + delay + " мин. по причине: " + reason;
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
        player.sendMsg("syntax //gag <player> <delay in minutes> <reason>");
    }

    //Jenelli 06.03.2013 Старая версия более не используется, так как нет сохранения при релоге.
    //	@Override
    //	public void runImpl(@NotNull final Player admin, @NotNull final String alias, @NotNull final String... params) {
    //		if (params == null || params.length < 1) {
    //            admin.sendMsg("Syntax: //gag <player> [time in minutes]");
    //            return;
    //		}
    //
    //		final String name = params[0];
    //		final Player player = World.getInstance().findPlayer(name);
    //		if (player == null) {
    //            admin.sendMsg("Player " + name + " was not found!");
    //            admin.sendMsg("Syntax: //gag <player> [time in minutes]");
    //            return;
    //		}
    //
    //		int time = 0;
    //		if (params.length > 1) {
    //			try {
    //				time = Integer.parseInt(params[1]);
    //			} catch (final NumberFormatException e) {
    //                admin.sendMsg("Syntax: //gag <player> [time in minutes]");
    //                return;
    //			}
    //		}
    //
    //		player.setGagged(true);
    //		if (time != 0) {
    //			final Future<?> task = player.getController().getTask(TaskId.GAG);
    //			if (task != null) {
    //				player.getController().cancelTask(TaskId.GAG);
    //			}
    //			player.getController().addTask(TaskId.GAG, ThreadPoolManager.getInstance().schedule(new Runnable() {
    //				@Override
    //				public void run() {
    //					player.setGagged(false);
    //                    player.sendMsg("You have been ungagged");
    //                }
    //			}, time * 60000L));
    //		}
    //        final String msg1 = "You have been gagged" + (time != 0 ? " for " + time + " minutes" : "");
    //        player.sendMsg(msg1);
    //
    //        final String msg = "Player " + name + " gagged" + (time != 0 ? " for " + time + " minutes" : "");
    //        admin.sendMsg(msg);
    //    }
    //
    //	@Override
    //	public void onError(final Player player, final Exception e) {
    //        player.sendMsg("Syntax: //gag <player> [time in minutes]");
    //    }
}
