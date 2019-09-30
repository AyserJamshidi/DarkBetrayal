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
import com.ne.gs.network.aion.serverpackets.SM_TRANSFORM;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author ATracer
 * @modified By aionchs- Wylovech
 */
public class Morph extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length != 1) {
            admin.sendMsg("syntax //morph <NPC Id | cancel> ");
            return;
        }

        Player target = admin;
        int param = 0;

        if (admin.getTarget() instanceof Player) {
            target = (Player) admin.getTarget();
        }

        if (!"cancel".startsWith(params[0].toLowerCase())) {
            try {
                param = Integer.parseInt(params[0]);

            } catch (NumberFormatException e) {
                admin.sendMsg("Parameter must be an integer, or cancel.");
                return;
            }
        }

        if (param != 0 && param < 200000 || param > 298021) {
            admin.sendMsg("Something wrong with the NPC Id!");
            return;
        }

        target.getTransformModel().setModelId(param);
        PacketSendUtility.broadcastPacketAndReceive(target, new SM_TRANSFORM(target, true));

        if (param == 0) {
            if (target.equals(admin)) {
                target.sendMsg("Morph successfully cancelled.");
            } else {
                target.sendMsg("Your morph has been cancelled by " + admin.getName() + ".");
                admin.sendMsg("You have cancelled " + target.getName() + "'s morph.");
            }
        } else if (target.equals(admin)) {
            target.sendMsg("Successfully morphed to npcId " + param + ".");
        } else {
            target.sendMsg(admin.getName() + " morphs you into an NPC form.");
            admin.sendMsg("You morph " + target.getName() + " to npcId " + param + ".");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //morph <NPC Id | cancel> ");
    }
}
