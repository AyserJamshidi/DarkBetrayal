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
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Gigi
 */
@AIName("door2")
public class Door2AI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        QuestState qsneedasmo = player.getQuestStateList().getQuestState(30311);
        QuestState qsneedelyos = player.getQuestStateList().getQuestState(30211);
        if (player.getRace() == Race.ELYOS) {
            if (qsneedelyos != null && qsneedelyos.getStatus() != QuestStatus.NONE) {// TODO: Only one player in group has to // have this quest
                super.handleDialogStart(player);
            } else {
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
            }
        } else {
            if (qsneedasmo != null && qsneedasmo.getStatus() != QuestStatus.NONE) { // TODO: Only one player in group has to // have this quest
                super.handleDialogStart(player);
            } else {
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 27));
            }
        }
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        AI2Actions.deleteOwner(this);
    }
}
