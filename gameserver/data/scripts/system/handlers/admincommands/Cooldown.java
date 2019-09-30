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

/**
 * @author Cura
 */
public class Cooldown extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (player.isCoolDownZero()) {
            player.sendMsg("Cooldown time of all skills has been recovered.");
            player.setCoolDownZero(false);
        } else {
            player.sendMsg("Cooldown time of all skills is set to 0.");
            player.setCoolDownZero(true);
        }
    }
}
