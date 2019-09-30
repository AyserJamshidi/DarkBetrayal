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
import com.ne.gs.services.FindGroupService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author KID
 */
public class Clear extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params[0].equalsIgnoreCase("groups")) {
            admin.sendMsg("Not implemented, if need this - pm to AT");
        } else if (params[0].equalsIgnoreCase("allys")) {
            admin.sendMsg("Not implemented, if need this - pm to AT");
        } else if (params[0].equalsIgnoreCase("findgroup")) {
            FindGroupService.getInstance().clean();
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("<usage //clear groups | allys | findgroup");
    }
}
