/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds;

import ai.GeneralNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("world_blesser")
public class WorldBlesserAI2 extends GeneralNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000) {
            SkillEngine.getInstance().getSkill(getOwner(), 955, 1, player).useNoAnimationSkill();
            SkillEngine.getInstance().getSkill(getOwner(), 951, 1, player).useNoAnimationSkill();
        }
        return true;
    }

}
