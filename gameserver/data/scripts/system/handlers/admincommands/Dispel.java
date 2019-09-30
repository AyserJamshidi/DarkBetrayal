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
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Hilgert
 */
public class Dispel extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        Player target;
        VisibleObject creature = admin.getTarget();

        if (creature == null) {
            admin.sendMsg("You should select a target first!");
            return;
        }

        if (creature instanceof Player) {
            target = (Player) creature;
            target.getEffectController().removeAllEffects();
            admin.sendMsg(creature.getName() + " had all buff effects dispelled !");
        }
    }
}
