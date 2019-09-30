/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import java.util.ArrayList;
import java.util.List;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AI2Actions.SelectDialogResult;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.model.QuestActionType;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.services.QuestService;
import com.ne.gs.services.drop.DropService;

/**
 * @author xTz
 */
@AIName("quest_use_item")
public class QuestItemNpcAI2 extends ActionItemNpcAI2 {

    private List<Player> registeredPlayers = new ArrayList<>();

    @Override
    protected void handleDialogStart(Player player) {
        if (!(QuestEngine.getInstance().onCanAct(new QuestEnv(getOwner(), player, 0, 0), getObjectTemplate().getTemplateId(), QuestActionType.ACTION_ITEM_USE))) {
            return;
        }
        super.handleDialogStart(player);
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        SelectDialogResult dialogResult = AI2Actions.selectDialog(this, player, 0, -1);
        if (!dialogResult.isSuccess()) {
            if (isDialogNpc()) {
                // show default dialog
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), QuestDialog.SELECT_ACTION_1011.id()));
            }
            return;
        }
        QuestEnv questEnv = dialogResult.getEnv();
        if (QuestService.getQuestDrop(getNpcId()).isEmpty()) {
            return;
        }

        if (registeredPlayers.isEmpty()) {
            AI2Actions.scheduleRespawn(this);
            if (player.isInGroup2()) {
                registeredPlayers = QuestService.getEachDropMembers(player.getPlayerGroup2(), getNpcId(), questEnv.getQuestId());
                if (registeredPlayers.isEmpty()) {
                    registeredPlayers.add(player);
                }
            } else {
                registeredPlayers.add(player);
            }
            AI2Actions.registerDrop(this, player, registeredPlayers);
            DropService.getInstance().requestDropList(player, getObjectId());
        } else if (registeredPlayers.contains(player)) {
            DropService.getInstance().requestDropList(player, getObjectId());
        }
    }

    private boolean isDialogNpc() {
        return getObjectTemplate().isDialogNpc();
    }

    @Override
    protected void handleDespawned() {
        super.handleDespawned();
        registeredPlayers.clear();
    }

}
