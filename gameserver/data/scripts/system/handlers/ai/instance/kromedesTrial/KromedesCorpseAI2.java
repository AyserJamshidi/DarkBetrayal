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
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.item.ItemService;

/**
 * @author Gigi
 */
@AIName("krcorpse")
public class KromedesCorpseAI2 extends NpcAI2 {

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 1012) {
            if (player.getInventory().getItemCountByItemId(164000141) < 1) {
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1012));
                player.sendPck(new SM_SYSTEM_MESSAGE(1400701));
                // not needed!
                ItemService.addItem(player, 164000141, 1);
            } else {
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
            }
        }
        return true;
    }

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
}
