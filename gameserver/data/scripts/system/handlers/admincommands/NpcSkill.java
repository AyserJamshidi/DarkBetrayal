/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.List;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.npcskill.NpcSkillTemplate;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Wakizashi
 */
public class NpcSkill extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        Npc target = null;
        VisibleObject creature = admin.getTarget();
        if (admin.getTarget() instanceof Npc) {
            target = (Npc) creature;
        }

        if (target == null) {
            admin.sendMsg("You should select a valid target first!");
            return;
        }

        StringBuilder strbld = new StringBuilder("-list of skills:\n");

        List<NpcSkillTemplate> list = DataManager.NPC_SKILL_DATA.getNpcSkillList(target.getNpcId()).getNpcSkills();

        for (NpcSkillTemplate skill : list) {
            strbld.append("    level ").append(skill.getSkillLevel()).append(" of ").append(skill.getSkillid()).append(".\n");
        }
        showAllLines(admin, strbld.toString());
    }

    private void showAllLines(Player admin, String str) {
        int index = 0;
        String[] strarray = str.split("\n");

        while (index < strarray.length - 20) {
            StringBuilder strbld = new StringBuilder();
            for (int i = 0; i < 20; i++, index++) {
                strbld.append(strarray[index]);
                if (i < 20 - 1) {
                    strbld.append("\n");
                }
            }
            admin.sendMsg(strbld.toString());
        }
        int odd = strarray.length - index;
        StringBuilder strbld = new StringBuilder();
        for (int i = 0; i < odd; i++, index++) {
            strbld.append(strarray[index]).append("\n");
        }
        admin.sendMsg(strbld.toString());
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
