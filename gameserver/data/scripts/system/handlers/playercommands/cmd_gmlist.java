/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package playercommands;

import java.util.Collection;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.configs.administration.AdminConfig;
import com.ne.gs.model.gameobjects.player.FriendList;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.audit.GMService;
import com.ne.gs.utils.chathandlers.ChatCommand;

public class cmd_gmlist extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {

        String sGMNames = "";
        Collection<Player> gms = GMService.getInstance().getGMs();
        int GMCount = 0;

        for (Player pPlayer : gms) {
            if (pPlayer.isGM() && !pPlayer.isProtectionActive() && pPlayer.getFriendList().getStatus() != FriendList.Status.OFFLINE) {
                String nameFormat = "%s";
                GMCount++;
                if (AdminConfig.ADMIN_TAG_ENABLE) {
                    switch (pPlayer.getAccessLevel()) {
                        case 1:
                            nameFormat = AdminConfig.ADMIN_TAG_1;
                            break;
                        case 2:
                            nameFormat = AdminConfig.ADMIN_TAG_2;
                            break;
                        case 3:
                            nameFormat = AdminConfig.ADMIN_TAG_3;
                            break;
                        case 4:
                            nameFormat = AdminConfig.ADMIN_TAG_4;
                            break;
                        case 5:
                            nameFormat = AdminConfig.ADMIN_TAG_5;
                            break;
                    }
                }

                sGMNames += String.format(nameFormat, pPlayer.getName()) + " : " + returnStringStatus(pPlayer.getFriendList().getStatus()) + ";\n";
            }
        }

        if (GMCount == 0) {
            player.sendMsg("В сети нет ГМ !");
        } else if (GMCount == 1) {
            player.sendMsg("Сейчас " + GMCount + " GM в сети !");
        } else {
            player.sendMsg("Сейчас " + GMCount + " GMs в сети !");
        }
        if (GMCount != 0) {
            player.sendMsg("Список : \n" + sGMNames);
        }
    }

    private String returnStringStatus(FriendList.Status p_status) {
        String return_string = "";
        if (p_status == FriendList.Status.ONLINE) {
            return_string = "Онлайн";
        }
        if (p_status == FriendList.Status.AWAY) {
            return_string = "Отошел";
        }
        return return_string;
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
