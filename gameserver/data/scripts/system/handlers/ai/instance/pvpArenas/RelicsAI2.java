/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.pvpArenas;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.instance.instancereward.InstanceReward;

/**
 * @author xTz
 */
@AIName("pvparenarelics")
public class RelicsAI2 extends ActionItemNpcAI2 {

    private boolean isRewarded;

    @Override
    protected void handleDialogStart(Player player) {
        InstanceReward<?> instance = getPosition().getWorldMapInstance().getInstanceHandler().getInstanceReward();
        if (instance != null && !instance.isStartProgress()) {
            return;
        }
        super.handleDialogStart(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (!isRewarded) {
            isRewarded = true;
            AI2Actions.handleUseItemFinish(this, player);
            int npcId = getNpcId();
            if (npcId != 701187 && npcId != 701188) {
                AI2Actions.scheduleRespawn(this);
            }
            AI2Actions.deleteOwner(this);
        }
    }
}
