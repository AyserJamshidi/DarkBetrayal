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
 * @author Phantom, ATracer
 */
public class Admin extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        HTMLService.showHTML(player, HTMLCache.getInstance().getHTML("commands.xhtml"));
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
