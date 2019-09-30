/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.NoSuchElementException;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.database.GDB;
import com.ne.gs.database.dao.PlayerDAO;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.PunishmentService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author Watson, Jenelli
 */
public class UnGag extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length == 0 || params.length > 2) {
            sendInfo(admin);
            return;
        }

        try {
            String playerName = params[0];
            Player playerUnGag = World.getInstance().findPlayer(playerName);

            if (playerUnGag != null) {
                PunishmentService.setIsGag(playerUnGag, false, 0, "");
            } else {
                Integer playerObjectId = GDB.get(PlayerDAO.class).getPlayerObjectIdByName(playerName);
                if (playerObjectId == null) {
                    admin.sendMsg("Player " + playerName + " not found on server.");
                    return;
                }
                PunishmentService.setIsGagOffline(playerObjectId, false, 0, "");
            }
            admin.sendMsg("Player " + playerName + " delete chat ban.");
        } catch (NoSuchElementException nsee) {
            sendInfo(admin);
        } catch (Exception e) {
            sendInfo(admin);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        sendInfo(player);
    }

    private void sendInfo(Player player) {
        player.sendMsg("syntax //ungag <player>");
    }

    //Jenelli 06.03.2013 Старая версия более не используется, так как нет сохранения при релоге.
    //	@Override
    //	public void runImpl(@NotNull final Player admin, @NotNull final String alias, @NotNull final String... params) {
    //		if (params == null || params.length < 1) {
    //            admin.sendMsg("Syntax: //ungag <player>");
    //            return;
    //		}
    //
    //		final String name = params[0];
    //		final Player player = World.getInstance().findPlayer(name);
    //		if (player == null) {
    //            admin.sendMsg("Player " + name + " was not found!");
    //            admin.sendMsg("Syntax: //ungag <player>");
    //            return;
    //		}
    //
    //		player.setGagged(false);
    //		final Future<?> task = player.getController().getTask(TaskId.GAG);
    //		if (task != null) {
    //			player.getController().cancelTask(TaskId.GAG);
    //		}
    //        player.sendMsg("You have been ungagged");
    //
    //        admin.sendMsg("Player " + name + " ungagged");
    //    }
    //
    //	@Override
    //	public void onError(final Player player, final Exception e) {
    //        player.sendMsg("Syntax: //ungag <player>");
    //    }
}
