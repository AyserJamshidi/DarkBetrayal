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
import com.ne.gs.model.gameobjects.state.CreatureSeeState;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_STATE;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Mathew
 */
public class See extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (admin.getSeeState() < 2) {
            admin.setSeeState(CreatureSeeState.SEARCH2);
            PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
            admin.sendMsg("You got vision.");
        } else {
            admin.setSeeState(CreatureSeeState.NORMAL);
            PacketSendUtility.broadcastPacket(admin, new SM_PLAYER_STATE(admin), true);
            admin.sendMsg("You lost vision.");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //see");
    }
}
