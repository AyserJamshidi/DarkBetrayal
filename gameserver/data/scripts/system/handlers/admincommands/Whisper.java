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
import com.ne.gs.utils.chathandlers.ChatCommand;

public class Whisper extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {

        if (params[0].equalsIgnoreCase("off")) {
            admin.setUnWispable();
            admin.sendMsg("Accepting Whisper : OFF");
        } else if (params[0].equalsIgnoreCase("on")) {
            admin.setWispable();
            admin.sendMsg("Accepting Whisper : ON");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //whisper [on for wispable / off for unwispable]");
    }
}
