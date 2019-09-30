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
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.skillengine.model.SkillTemplate;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Source
 */
public class UseSkill extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length > 3) {
            onError(admin, null);
            return;
        }

        VisibleObject target = admin.getTarget();

        int skillId;
        int skillLevel;

        try {
            skillId = Integer.parseInt(params[0]);
            skillLevel = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            admin.sendMsg("Parameters need to be an integer.");
            return;
        }

        if (target == null || !(target instanceof Creature)) {
            admin.sendMsg("You must select a target!");
            return;
        }
        if (target.getTarget() == null || !(target.getTarget() instanceof Creature)) {
            admin.sendMsg("Target must select some creature!");
            return;
        }

        SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);

        if (skillTemplate == null) {
            admin.sendMsg("No skill template id:" + skillId);
            return;
        }

        if (params.length == 3 && params[2].equalsIgnoreCase("true")) {
            SkillEngine.getInstance().applyEffectDirectly(skillId, (Creature) target, (Creature) target.getTarget(), 2000);
            admin.sendMsg("applyingskillid:" + skillId);
        } else {
            ((Creature) target).getController().useSkill(skillId, skillLevel);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //skill <skillId> <skillLevel>");
    }
}
