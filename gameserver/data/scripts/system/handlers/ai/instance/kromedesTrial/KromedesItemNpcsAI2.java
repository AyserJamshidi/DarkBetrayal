/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.kromedesTrial;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.item.ItemService;

/**
 * @author Gigi, xTz
 */
@AIName("krobject")
public class KromedesItemNpcsAI2 extends ActionItemNpcAI2 {

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 1012) {
            switch (getNpcId()) {
                case 730325:
                    if (player.getInventory().getItemCountByItemId(164000142) < 1) {
                        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1012));
                        player.sendPck(new SM_SYSTEM_MESSAGE(1400701));
                        // now not needed!
                        ItemService.addItem(player, 164000142, 1);
                    } else {
                        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
                    }
                    break;
                case 730340:
                    if (player.getInventory().getItemCountByItemId(164000140) < 1) {
                        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1012));
                        player.sendPck(new SM_SYSTEM_MESSAGE(1400701));
                        // now not needed!
                        ItemService.addItem(player, 164000140, 1);
                    } else {
                        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
                    }
                    break;
                case 730341:
                    if (player.getInventory().getItemCountByItemId(164000143) < 1) {
                        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1012));
                        player.sendPck(new SM_SYSTEM_MESSAGE(1400701));
                        // now not needed!
                        ItemService.addItem(player, 164000143, 1);
                    } else {
                        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }
}
