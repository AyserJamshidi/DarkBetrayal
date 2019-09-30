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
import com.ne.gs.services.abyss.AbyssRankUpdateService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 */
public class Ranking extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length == 0) {
            onError(admin, null);
        } else if ("update".equalsIgnoreCase(params[0])) {
            AbyssRankUpdateService.getInstance().performUpdate();
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //ranking update");
    }
}
