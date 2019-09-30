/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.portals;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("portal_elevator")
public class PortalElevatorAI2 extends PortalAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(getOwner(), EmotionType.EMOTE, 144, 0), true);
        super.handleUseItemFinish(player);
    }

}
