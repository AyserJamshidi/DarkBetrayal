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
 * @author lord_rex Command: //rprison <player> This command is removing player from prison.
 */
public class RPrison extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length == 0 || params.length > 2) {
            admin.sendMsg("syntax //rprison <player>");
            return;
        }

        try {
            String playerName = params[0];
            Player playerFromPrison = World.getInstance().findPlayer(playerName);

            if (playerFromPrison != null) {
                PunishmentService.setIsInPrison(playerFromPrison, false, 0, "");
            } else {
                Integer playerObjectId = GDB.get(PlayerDAO.class).getPlayerObjectIdByName(playerName);
                if (playerObjectId == null) {
                    admin.sendMsg("Player " + playerName + " not found on server.");
                    return;
                }
                PunishmentService.setIsInPrisonOffline(playerObjectId, false, 0, "");
            }
            admin.sendMsg("Player " + playerName + " removed from prison.");
        } catch (NoSuchElementException nsee) {
            admin.sendMsg("Usage: //rprison <player>");
        } catch (Exception e) {
            admin.sendMsg("Usage: //rprison <player>");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //rprison <player>");
    }
}
