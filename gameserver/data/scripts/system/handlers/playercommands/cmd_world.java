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
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.ingameshop.InGameShopEn;
import com.ne.gs.network.aion.serverpackets.SM_TOLL_INFO;
import com.ne.gs.network.loginserver.LoginServer;
import com.ne.gs.network.loginserver.serverpackets.SM_ACCOUNT_TOLL_INFO;
import com.ne.gs.services.player.PlayerChatService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

public class cmd_world extends ChatCommand {

    @Override
    public void runImpl(@NotNull final Player player, @NotNull String command, @NotNull final String... params) {
        if (!CustomConfig.WORLD_CMD_CHANNEL) {
            player.sendMsg("Команда отключена.");
            return;
        }

        if (params.length < 1) {
            player.sendMsg("синтаксис .world ВашеСообщение");
            return;
        }

        if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
            player.sendMsg("Вы не можете разговаривать в Тюрьме.");
            return;
        } else if (player.isGagged()) {
            player.sendMsg("Ваш чат Заблокирован. Вы не можете говорить.");
            return;
        } else if (player.getLevel() < CustomConfig.FACTION_LEVEL) {
            player.sendMsg("Ваш уровень ниже " + CustomConfig.FACTION_LEVEL + ", вы не можете говорить.");
            return;
        }

        InGameShopEn.getInstance().querryToll(player, new InGameShopEn.TollQuerry() {
            @Override
            public Object onEvent(@NotNull InGameShopEn.TollQuerryResult env) {
                long playerToll = env.toll;
                if (playerToll >= CustomConfig.WORLD_CHAT_USE_COST) {
                    long toll = playerToll - CustomConfig.WORLD_CHAT_USE_COST;
                    if (LoginServer.getInstance()
                                   .sendPacket(new SM_ACCOUNT_TOLL_INFO(toll, player.getAcountName()))) {
                        player.sendPck(new SM_TOLL_INFO(toll));
                    }
                } else {
                    player.sendMsg("У вас недостаточно донат монет для использования команды.");
                    return null;
                }

                String message = StringUtils.join(params, " ");

                if (!PlayerChatService.isFlooding(player)) {
                    message = "World | " + player.getName() + ": " + message;

                    for (Player a : World.getInstance().getAllPlayers()) {
                        PacketSendUtility.sendBrightYellowMessage(a, message);
                    }
                }

                return null;
            }
        });
    }
}
