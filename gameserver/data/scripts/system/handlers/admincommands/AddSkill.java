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
 * @author Phantom
 */
public class AddSkill extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (params.length != 2) {
            player.sendMsg("syntax //addskill <skillId> <skillLevel>");
            return;
        }

        VisibleObject target = player.getTarget();

        int skillId;
        int skillLevel;

        try {
            skillId = Integer.parseInt(params[0]);
            skillLevel = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            player.sendMsg("Parameters need to be an integer.");
            return;
        }

        if (target instanceof Player) {
            Player targetpl = (Player) target;
            targetpl.getSkillList().addSkill(targetpl, skillId, skillLevel);
            player.sendMsg("You have success add skill");
            targetpl.sendMsg("You have acquire a new skill");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //addskill <skillId> <skillLevel>");
    }
}
