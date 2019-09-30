/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.fountain;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.configs.main.AIConfig;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.services.item.ItemService;


@AIName("mithril_fountain")
public class MithrilFountainAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        if (AIConfig.MITHRIL_FOUNTAIN_ENABLE && player.getCommonData().getLevel() >= 58) {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 10, questByRace(player)));
        } else {
            player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 1011));
        }
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        switch (dialogId) {
            case 10000:
                if (hasItem(player, 186000096)) {
                    player.sendPck(new SM_DIALOG_WINDOW(getOwner().getObjectId(), 5, questByRace(player)));
                } else {
                    player.sendPck(new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1011));
                }
                break;
            case 18:
                Item item = player.getInventory().getFirstItemByItemId(186000096);
                player.getInventory().decreaseByObjectId(item.getObjectId(), 1);
                giveItem(player);
                player.sendPck(new SM_DIALOG_WINDOW(getOwner().getObjectId(), 1008, questByRace(player)));
                break;
        }
        return true;
    }

    private boolean hasItem(Player player, int itemId) {
        return player.getInventory().getItemCountByItemId(itemId) > 0;
    }

    private int questByRace(Player player) {
        if (player.getRace() == Race.ASMODIANS) {
            return 22061;
        } else {
            return 12061;
        }
    }

    private void giveItem(Player player) {
        int medalType = Rnd.get(1, 2);
        int medalCount = Rnd.get(0, 10);

        if (Rnd.chance(AIConfig.MITHRIL_FOUNTAIN_SUCCESS_CHANCE)) {
            switch (medalType) {
                case 1:
                    if (medalCount < 3) {
                        ItemService.addItem(player, 186000147, 2);
                    } else {
                        ItemService.addItem(player, 186000147, 1);
                    }
                    break;
                case 2:
                    if (medalCount < 3) {
                        ItemService.addItem(player, 186000096, 2);
                    } else {
                        ItemService.addItem(player, 186000096, 1);
                    }
                    break;
            }
        } else {
            ItemService.addItem(player, 182005205, 1);
        }
    }
}
