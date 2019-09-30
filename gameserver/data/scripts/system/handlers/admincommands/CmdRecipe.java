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
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_RECIPE_LIST;
import com.ne.gs.services.RecipeService;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author hex1r0
 */
public class CmdRecipe extends ChatCommand {

    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {
        String cmd = params[0];
        if (cmd.startsWith("a"))
        {
            VisibleObject target = player.getTarget();
            int id = Integer.parseInt(params[1]);
            RecipeService.addRecipe((Player) target, id, false);
            SM_RECIPE_LIST.sendTo((Player) target);
        }
    }
}
