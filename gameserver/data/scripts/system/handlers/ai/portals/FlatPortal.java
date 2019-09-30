/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.portals;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.modules.housing.Housing;
import com.ne.gs.world.WorldMapType;

import static com.ne.gs.modules.housing.Housing.housing;

/**
 * @author hex1r0
 */
@AIName("FlatPortal")
public class FlatPortal extends ActionItemNpcAI2 {

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        return true;
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        housing().tell(new Housing.EnterLeaveFlat(player,
            WorldMapType.of(player.getWorldId()).isPersonal()));
    }
}
