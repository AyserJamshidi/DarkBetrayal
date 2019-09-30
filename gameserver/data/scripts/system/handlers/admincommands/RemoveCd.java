/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author kecimis
 */
public class RemoveCd extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        VisibleObject target = admin.getTarget();
        if (target == null) {
            target = admin;
        }

        if (params.length == 1) {
            if (params[0].equalsIgnoreCase("me")) {
                admin.flushSkillCd();
                admin.flushItemCd();

                admin.sendMsg("Your cooldowns were removed");
            }
        }

        if (target instanceof Player) {
            Player player = (Player) target;
            if (params.length == 0) {
                player.flushSkillCd();
                player.flushItemCd();

                if (player.equals(admin)) {
                    admin.sendMsg("Your cooldowns were removed");
                } else {
                    admin.sendMsg("You have removed cooldowns of player: " + player.getName());
                    player.sendMsg("Your cooldowns were removed by admin");
                }
            } else if (params[0].contains("instance")) {
                if (player.getPortalCooldownList() == null || player.getPortalCooldownList()
                                                                    .getPortalCoolDowns() == null) {
                    return;
                }
                if (params.length >= 2) {
                    if (params[1].equalsIgnoreCase("all")) {
                        List<Integer> mapIds = new ArrayList<>();
                        for (Entry<Integer, Long> mapId : player.getPortalCooldownList().getPortalCoolDowns()
                                                                .entrySet()) {
                            mapIds.add(mapId.getKey());
                        }

                        for (Integer id : mapIds) {
                            player.getPortalCooldownList().addPortalCooldown(id, 0);
                        }

                        mapIds.clear();
                        if (player.equals(admin)) {
                            admin.sendMsg("Your instance cooldowns were removed");
                        } else {
                            admin.sendMsg("You have removed instance cooldowns of player: " + player
                                .getName());
                            player.sendMsg("Your instance cooldowns were removed by admin");
                        }
                    } else {
                        int worldId;
                        try {
                            worldId = Integer.parseInt(params[1]);
                        } catch (NumberFormatException e) {
                            admin.sendMsg("WorldId has to be integer or use \"all\"");
                            return;
                        }

                        if (player.getPortalCooldownList().isPortalUseDisabled(worldId)) {
                            player.getPortalCooldownList().addPortalCooldown(worldId, 0);

                            if (player.equals(admin)) {
                                admin.sendMsg("Your instance cooldown worldId: " + worldId + " was removed");
                            } else {
                                admin.sendMsg("You have removed instance cooldown worldId: " + worldId + " of player: " + player
                                    .getName());
                                player.sendMsg("Your instance cooldown worldId: " + worldId + " was removed by admin");
                            }
                        } else {
                            admin.sendMsg("You or your target can enter given instance");
                        }

                    }
                } else {
                    admin.sendMsg("syntax: //removecd instance <all|worldId>");
                }
            }
        } else {
            admin.sendMsg("Only players are allowed as target");
        }
    }
}
