/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sarpan;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _11511SurprisingSolutions extends QuestHandler {

    private final static int questId = 11511;

    public _11511SurprisingSolutions() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205989).addOnQuestStart(questId);
        qe.registerQuestNpc(205989).addOnTalkEvent(questId);
        qe.registerQuestNpc(205746).addOnTalkEvent(questId);
        qe.registerGetingItem(182213115, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 205989) {
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1011);
                    }
                    default: {
                        return sendQuestStartDialog(env);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 205746) {
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1352);
                    }
                    case STEP_TO_1: {
                        return defaultCloseDialog(env, 0, 1);
                    }
                }
            } else if (targetId == 205989) {
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 2375);
                    }
                    case CHECK_COLLECTED_ITEMS_SIMPLE: {
                        return checkQuestItemsSimple(env, 1, 1, true, 5, 0, 0); // reward
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205989) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onGetItemEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int var = qs.getQuestVarById(1);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (var == 15) {
                changeQuestStep(env, 1, 1, false);
                return true;
            } else {
                qs.setQuestVarById(1, var);
            }

        }
        return false;
    }
}
