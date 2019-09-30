/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.steelRake;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.state.CreatureState;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;

/**
 * @author xTz
 */
@AIName("suspiciouscannon")
public class SuspiciousCannonAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        player.setState(CreatureState.FLIGHT_TELEPORT);
        player.unsetState(CreatureState.ACTIVE);
        player.setFlightTeleportId(73001);
        player.sendPck(new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 73001, 0));
    }

}
