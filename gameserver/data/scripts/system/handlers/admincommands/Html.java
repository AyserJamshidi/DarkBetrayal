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
import com.ne.gs.cache.HTMLCache;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.HTMLService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author lord_rex
 */
public class Html extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            player.sendMsg("Usage: //html <reload|show>");
            return;
        }

        if (params[0].equals("reload")) {
            HTMLCache.getInstance().reload(true);
            player.sendMsg(HTMLCache.getInstance().toString());
        } else if (params[0].equals("show")) {
            if (params.length >= 2) {
                HTMLService.showHTML(player, HTMLCache.getInstance().getHTML(params[1] + ".xhtml"));
            } else {
                player.sendMsg("Usage: //html show <filename>");
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Usage: //html <reload|show>");
    }
}
