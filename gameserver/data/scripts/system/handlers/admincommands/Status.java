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
import com.ne.gs.model.team2.alliance.PlayerAllianceService;
import com.ne.gs.model.team2.group.PlayerGroupService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author KID
 */
public class Status extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params[0].equalsIgnoreCase("alliance")) {
            admin.sendMsg(PlayerAllianceService.getServiceStatus());
        } else if (params[0].equalsIgnoreCase("group")) {
            admin.sendMsg(PlayerGroupService.getServiceStatus());
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("<usage //status alliance | group");
    }
}
