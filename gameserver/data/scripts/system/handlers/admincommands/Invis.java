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
import com.ne.gs.model.gameobjects.state.CreatureVisualState;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_STATE;
import com.ne.gs.skillengine.effect.AbnormalState;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Divinity
 */
public class Invis extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        if (player.getVisualState() < 3) {
            player.getEffectController().setAbnormal(AbnormalState.HIDE.getId());
            player.setVisualState(CreatureVisualState.HIDE3);
            PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
            player.sendMsg("You are invisible.");
        } else {
            player.getEffectController().unsetAbnormal(AbnormalState.HIDE.getId());
            player.unsetVisualState(CreatureVisualState.HIDE3);
            PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
            player.sendMsg("You are visible.");
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
