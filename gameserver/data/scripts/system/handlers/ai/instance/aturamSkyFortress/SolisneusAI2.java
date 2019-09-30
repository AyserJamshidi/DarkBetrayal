/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.aturamSkyFortress;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.item.ItemService;

@AIName("solisneus")
public class SolisneusAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        if (dialogId == 10000) {
            Storage storage = player.getInventory();
            if (storage.getItemCountByItemId(164000163) == 0) {
                if (!storage.isFull()) {
                    ItemService.addItem(player, 164000163, 1);
                } else {
                    player.sendPck(new SM_SYSTEM_MESSAGE(1330036));
                    player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
                    return true;
                }
            }
            if (storage.getItemCountByItemId(164000202) == 0) {
                if (!storage.isFull()) {
                    ItemService.addItem(player, 164000202, 1);
                } else {
                    player.sendPck(new SM_SYSTEM_MESSAGE(1330036));
                    player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
                    return true;
                }
            }
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 0));
        }
        return true;
    }
}
