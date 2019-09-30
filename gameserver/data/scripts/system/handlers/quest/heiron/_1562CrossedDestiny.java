/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * Find Litonos (204616) (bring him the Berone's Necklace (182201780)). Talk with Litonos. Take Litonos to Berone
 * (204589). Talk with Berone.
 *
 * @author Balthazar
 * @reworked vlog
 */

public class _1562CrossedDestiny extends QuestHandler {

    private final static int questId = 1562;

    public _1562CrossedDestiny() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204589).addOnQuestStart(questId);
        qe.registerQuestNpc(204589).addOnTalkEvent(questId);
        qe.registerQuestNpc(204616).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
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
            if (targetId == 204589) { // Berone
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                }
                if (env.getDialogId() == 1007) {
                    return sendQuestDialog(env, 4);
                }
                if (env.getDialogId() == 1003) {
                    return sendQuestDialog(env, 1004);
                }
                if (env.getDialogId() == 1002) {
                    if (QuestService.startQuest(env)) {
                        return defaultCloseDialog(env, 0, 1, false, false, 182201780, 1, 0, 0); // 1
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 204616: { // Litonos
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (qs.getQuestVarById(0) == 1 && player.getInventory().getItemCountByItemId(182201780) == 1) {
                                return sendQuestDialog(env, 1352);
                            } else {
                                return sendQuestDialog(env, 1438);
                            }
                        case FINISH_DIALOG:
                            return defaultCloseDialog(env, 0, 0);
                        case STEP_TO_2:
                            if (qs.getQuestVarById(0) == 1) {
                                Npc npc = (Npc) env.getVisibleObject();
                                defaultStartFollowEvent(env, npc, 204589, 0, 0);
                                return defaultCloseDialog(env, 1, 2, false, false, 0, 0, 182201780, 1); // 2
                            }
                        case USE_OBJECT:
                            if (qs.getQuestVarById(0) == 1) {
                                Npc npc = (Npc) env.getVisibleObject();
                                return defaultStartFollowEvent(env, npc, 204589, 1, 2);
                            }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204589) { // Berone
                if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onNpcReachTargetEvent(QuestEnv env) {
        return defaultFollowEndEvent(env, 2, 2, true); // reward
    }

    @Override
    public boolean onNpcLostTargetEvent(QuestEnv env) {
        return defaultFollowEndEvent(env, 2, 1, false); // 1
    }
}
