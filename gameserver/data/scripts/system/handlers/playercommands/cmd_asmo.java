/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import org.apache.commons.lang3.StringUtils;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.player.PlayerChatService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

public class cmd_asmo extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String command, @NotNull String... params) {
        Storage sender = player.getInventory();

        if (!CustomConfig.FACTION_CMD_CHANNEL) {
            player.sendMsg("Команда отключена.");
            return;
        }

        if (params == null || params.length < 1) {
            player.sendMsg("Синтаксис .asmo Сообщение");
            return;
        }

        if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
            player.sendMsg("Вы не можете разговаривать в тюрьме.");
            return;
        } else if (player.isGagged()) {
            player.sendMsg("Ваш чат Заблокирован. Вы не можете говорить.");
            return;
        } else if (player.getLevel() < CustomConfig.FACTION_LEVEL) {
            player.sendMsg("Ваш уровень меньше " + CustomConfig.FACTION_LEVEL + ", вы не можете говорить.");
            return;
        }

        if (player.getRace() != Race.ASMODIANS && player.getAccessLevel() < 1) {
            player.sendMsg("Вы не можете разговаривать с другой расой.");
            return;
        }

        if (!CustomConfig.FACTION_FREE_USE) {
            if (sender.getKinah() > CustomConfig.FACTION_USE_PRICE) {
                sender.decreaseKinah(CustomConfig.FACTION_USE_PRICE);
            } else {
                player.sendPck(SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
                return;
            }
        }

        String message = StringUtils.join(params, " ");

        if (!PlayerChatService.isFlooding(player)) {
            message = "Asmodians | " + player.getName() + ": " + message;

            for (Player a : World.getInstance().getAllPlayers()) {
                if (a.getAccessLevel() > 0) {
                    a.sendMsg(message);
                } else if (a.getRace() == Race.ASMODIANS) {
                    a.sendMsg(message);
                }
            }
        }
    }
}
