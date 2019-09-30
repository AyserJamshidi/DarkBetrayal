/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.beshmundirTemple;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;

/**
 * @author Gigi
 */
@AIName("door3")
public class Door3AI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        if (player.getInventory().getItemCountByItemId(185000091) > 0) {
            super.handleDialogStart(player);
        } else {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
        }
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        AI2Actions.deleteOwner(this);
    }
}
