/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.kromedesTrial;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;

/**
 * @author Gigi
 */
@AIName("krmagas")
public class KromedesMagasAI2 extends NpcAI2 {

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000) {
            if (player.getInventory().getItemCountByItemId(185000109) > 0) {
                player.sendPck(new SM_PLAY_MOVIE(0, 454));
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
            } else {
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
            }
        } else if (dialogId == 1012) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1012));
        }
        return true;
    }

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
}
