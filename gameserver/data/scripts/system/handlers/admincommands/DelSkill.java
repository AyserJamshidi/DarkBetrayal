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
import com.ne.gs.model.skill.PlayerSkillEntry;
import com.ne.gs.model.skill.PlayerSkillList;
import com.ne.gs.services.SkillLearnService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author xTz
 */
public class DelSkill extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1 || params.length > 2) {
            admin.sendMsg("No parameters detected.\n" + "Please use //delskill <Player name> <all | skillId>\n"
                + "or use //delskill [target] <all | skillId>");
            return;
        }

        Player player;
        PlayerSkillList playerSkillList = null;
        String recipient;
        recipient = params[0];
        int skillId = 0;
        if (params.length == 2) {
            player = World.getInstance().findPlayer(recipient);
            if (player == null) {
                admin.sendMsg("The specified player is not online.");
                return;
            }

            if ("all".startsWith(params[1])) {
                playerSkillList = player.getSkillList();
            } else {
                try {
                    skillId = Integer.parseInt(params[1]);
                } catch (NumberFormatException e) {
                    admin.sendMsg("Param 1 must be an integer or <all>.");
                    return;
                }

                if (!check(admin, player, skillId)) {
                    return;
                }
            }
            apply(admin, player, skillId, playerSkillList);

        }
        if (params.length == 1) {
            VisibleObject target = admin.getTarget();
            if (target == null) {
                admin.sendMsg("You should select a target first!");
                return;
            }

            if (target instanceof Player) {
                player = (Player) target;

                if ("all".startsWith(params[0])) {
                    playerSkillList = player.getSkillList();
                } else {
                    try {
                        skillId = Integer.parseInt(params[0]);
                    } catch (NumberFormatException e) {
                        admin.sendMsg("Param 0 must be an integer or <all>.");
                        return;
                    }

                    if (!check(admin, player, skillId)) {
                        return;
                    }
                }
                apply(admin, player, skillId, playerSkillList);
            } else {
                admin.sendMsg("This command can only be used on a player !");
            }
        }
    }

    private static boolean check(Player admin, Player player, int skillId) {
        if (skillId != 0 && !player.getSkillList().isSkillPresent(skillId)) {
            admin.sendMsg("Player dont have this skill.");
            return false;
        }
        if (player.getSkillList().getSkillEntry(skillId).isStigma()) {
            admin.sendMsg("You can't remove stigma skill.");
            return false;
        }
        return true;
    }

    public void apply(Player admin, Player player, int skillId, PlayerSkillList playerSkillList) {
        if (skillId != 0) {
            SkillLearnService.removeSkill(player, skillId);
            admin.sendMsg("You have successfully deleted the specified skill.");
        } else {
            for (PlayerSkillEntry skillEntry : playerSkillList.getAllSkills()) {
                if (!skillEntry.isStigma()) {
                    SkillLearnService.removeSkill(player, skillEntry.getSkillId());
                }
            }

            admin.sendMsg("You have success delete All skills.");
        }

    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("No parameters detected.\n" + "Please use //delskill <Player name> <all | skillId>\n"
            + "or use //delskill [target] <all | skillId>");
    }
}
