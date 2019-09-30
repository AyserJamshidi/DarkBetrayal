/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.altgard;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Mr. Poke
 * @reworked vlog
 */
public class _2221ManirsUncle extends QuestHandler {

    private final static int questId = 2221;

    public _2221ManirsUncle() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203607).addOnQuestStart(questId);
        qe.registerQuestNpc(203607).addOnTalkEvent(questId);
        qe.registerQuestNpc(203608).addOnTalkEvent(questId);
        qe.registerQuestNpc(700214).addOnTalkEvent(questId);
        qe.registerGetingItem(182203215, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203607) { // Manir
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 203608: { // Groken
                    if (dialog == QuestDialog.START_DIALOG) {
                        if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        } else if (var == 2) {
                            return sendQuestDialog(env, 2375);
                        }
                    } else if (dialog == QuestDialog.STEP_TO_1) {
                        return defaultCloseDialog(env, 0, 1); // 1
                    } else if (dialog == QuestDialog.SELECT_REWARD) {
                        removeQuestItem(env, 182203215, 1);
                        changeQuestStep(env, 2, 2, true); // reward
                        return sendQuestDialog(env, 5);
                    }
                    break;
                }
                case 700214: { // Groken's Safe
                    if (dialog == QuestDialog.USE_OBJECT) {
                        if (var == 1) {
                            return sendQuestDialog(env, 1693);
                        }
                    } else if (dialog == QuestDialog.STEP_TO_2) {
                        return closeDialogWindow(env);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203608) { // Groken
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onGetItemEvent(QuestEnv env) {
        return defaultOnGetItemEvent(env, 1, 2, false); // 2
    }
}
