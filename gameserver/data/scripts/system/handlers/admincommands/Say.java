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
import com.ne.gs.model.ChatType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_MESSAGE;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Divinity
 */
public class Say extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            onError(admin, null);
            return;
        }

        VisibleObject target = admin.getTarget();

        if (target == null) {
            admin.sendMsg("You must select a target !");
            return;
        }

        StringBuilder sbMessage = new StringBuilder();

        for (String p : params) {
            sbMessage.append(p).append(" ");
        }

        String sMessage = sbMessage.toString().trim();

        if (target instanceof Player) {
            PacketSendUtility.broadcastPacket((Player) target, new SM_MESSAGE((Player) target, sMessage, ChatType.NORMAL), true);
        } else if (target instanceof Npc) {
            // admin is not right, but works
            PacketSendUtility.broadcastPacket(admin, new SM_MESSAGE(target.getObjectId(), target.getName(), sMessage, ChatType.NORMAL), true);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //say <Text>");
    }
}
