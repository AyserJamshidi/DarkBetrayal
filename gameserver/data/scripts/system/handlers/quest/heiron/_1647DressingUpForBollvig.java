/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 * @reworked vlog
 */
public class _1647DressingUpForBollvig extends QuestHandler {

    private final static int questId = 1647;

    public _1647DressingUpForBollvig() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(790019).addOnQuestStart(questId);
        qe.registerQuestNpc(790019).addOnTalkEvent(questId);
        qe.registerQuestNpc(700272).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 790019) { // Zetus
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 4762);
                    }
                    default: {
                        return sendQuestStartDialog(env, 182201783, 1);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 700272) { // Suspicious Stone Statue
                if (dialog == QuestDialog.USE_OBJECT) {
                    // Wearing Stenon Blouse and Stenon Skirt
                    if (!player.getEquipment().getEquippedItemsByItemId(110100150).isEmpty()
                        && !player.getEquipment().getEquippedItemsByItemId(113100144).isEmpty()) {
                        // Having Myanee's Flute
                        if (player.getInventory().getItemCountByItemId(182201783) > 0) {
                            // TODO: movie
                            return useQuestObject(env, 0, 0, true, false); // reward
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 790019) { // Zetus
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 10002);
                    }
                    default: {
                        return sendQuestEndDialog(env);
                    }
                }
            }
        }
        return false;
    }
}
