/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sanctum;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * Talk with Erdos (203740). Bring the Yellow Aether Powder (186000090) and Kinah (90000) to Flora (798384).
 *
 * @author undertrey
 * @modified vlog
 */
public class _3964GrowthFlorasFourthCharm extends QuestHandler {

    private final static int questId = 3964;

    public _3964GrowthFlorasFourthCharm() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798384).addOnQuestStart(questId);
        qe.registerQuestNpc(798384).addOnTalkEvent(questId);
        qe.registerQuestNpc(203740).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 798384) { // Flora
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 203740: { // Erdos
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 0) {
                                return sendQuestDialog(env, 1352);
                            }
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1, 182206111, 1, 0, 0); // 1
                    }
                }
                case 798384: // Flora
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 1) {
                                removeQuestItem(env, 182206111, 1);
                                return sendQuestDialog(env, 2375);
                            }
                        case CHECK_COLLECTED_ITEMS:
                            long itemAmount = player.getInventory().getItemCountByItemId(186000090);
                            if (var == 1 && player.getInventory().tryDecreaseKinah(90000) && itemAmount >= 1) {
                                player.getInventory().decreaseKinah(90000);
                                removeQuestItem(env, 186000090, 1);
                                changeQuestStep(env, 1, 1, true); // reward
                                return sendQuestDialog(env, 5);
                            } else {
                                return sendQuestDialog(env, 2716);
                            }
                        case FINISH_DIALOG:
                            return defaultCloseDialog(env, 1, 1);
                    }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798384) { // Flora
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
