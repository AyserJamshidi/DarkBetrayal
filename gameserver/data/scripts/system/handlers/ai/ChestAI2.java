/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.configs.main.GroupConfig;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.chest.ChestTemplate;
import com.ne.gs.model.templates.chest.KeyItem;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.drop.DropRegistrationService;
import com.ne.gs.services.drop.DropService;
import com.ne.gs.utils.MathUtil;

import static ch.lambdaj.Lambda.maxFrom;

/**
 * @author ATracer, xTz
 */
@AIName("chest")
public class ChestAI2 extends ActionItemNpcAI2 {

    private ChestTemplate chestTemplate;

    private List<KeyItem> _keyItems;

    private boolean isActive;

    @Override
    protected void handleDialogStart(Player player) {
        chestTemplate = DataManager.CHEST_DATA.getChestTemplate(getNpcId());

        if (chestTemplate == null) {
            return;
        }

        _keyItems = chestTemplate.getKeyItem();

        if (_keyItems != null && _keyItems.size() > 0)
            for (KeyItem keyItem : _keyItems) {

                if (keyItem.getItemId() == 0)
                    continue;

                if (keyItem.getQuantity() > player.getInventory().getItemCountByItemId(keyItem.getItemId())) {
                    player.sendMsg("Для использования предмета, требуется " +
                            (keyItem.getQuantity() > 1 ? keyItem.getQuantity() + " " : "")
                            +"[item:" + keyItem.getItemId() + "]");
                    return;
                }
            }

        super.handleDialogStart(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (analyzeOpening(player)) {
            if (isAlreadyDead()) {
                return;
            }

            Collection<Player> players = new HashSet<>();
            if (player.isInGroup2()) {
                for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
                    if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE)) {
                        players.add(member);
                    }
                }
            } else if (player.isInAlliance2()) {
                for (Player member : player.getPlayerAlliance2().getOnlineMembers()) {
                    if (MathUtil.isIn3dRange(member, getOwner(), GroupConfig.GROUP_MAX_DISTANCE)) {
                        players.add(member);
                    }
                }
            } else {
                players.add(player);
            }
            DropRegistrationService.getInstance().registerDrop(getOwner(), player, maxFrom(players).getLevel(), players);
            AI2Actions.dieSilently(this, player);
            DropService.getInstance().requestDropList(player, getObjectId());
            super.handleUseItemFinish(player);
        } else {
            player.sendPck(new SM_SYSTEM_MESSAGE(1111301));
        }
    }

    private boolean analyzeOpening(Player player) {

        int i = 0;
        for (KeyItem keyItem : _keyItems) {
            if (keyItem.getItemId() == 0) {
                return true;
            }
            Item item = player.getInventory().getFirstItemByItemId(keyItem.getItemId());
            if (item != null) {
                if (item.getItemCount() != keyItem.getQuantity()) {
                    int _i = 0;
                    for (Item findedItem : player.getInventory().getItemsByItemId(keyItem.getItemId())) {
                        _i += findedItem.getItemCount();
                    }
                    if (_i < keyItem.getQuantity()) {
                        return false;
                    }
                }
                i++;
                continue;
            } else {
                return false;
            }
        }
        if (i == _keyItems.size()) {
            for (KeyItem keyItem : _keyItems) {
                player.getInventory().decreaseByItemId(keyItem.getItemId(), keyItem.getQuantity());
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean isForSinglePlayerUse() {
        return true;
    }

    @Override
    protected void handleDialogFinish(Player player) {
    }
}
