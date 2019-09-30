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
import com.ne.gs.modules.instanceentry.InstanceEntryManager;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author hex1r0
 */
public class CmdInstanceEntryManager extends ChatCommand {
    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        String cmd = params[0];

        if (cmd.equals("dump")) {
            InstanceEntryManager.tell(new InstanceEntryManager.DumpStats());
        }
    }
}
