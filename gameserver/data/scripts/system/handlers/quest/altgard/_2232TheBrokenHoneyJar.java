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
 * @author Mr. Poke fix by Nephis and quest helper team.
 * @reworked vlog
 */
public class _2232TheBrokenHoneyJar extends QuestHandler {

    private final static int questId = 2232;

    public _2232TheBrokenHoneyJar() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203613).addOnQuestStart(questId);
        qe.registerQuestNpc(203613).addOnTalkEvent(questId);
        qe.registerQuestNpc(203622).addOnTalkEvent(questId);
        qe.registerQuestNpc(700061).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203613) { // Gilungk
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 203613) { // Gilungk
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 2375);
                        }
                    }
                    case CHECK_COLLECTED_ITEMS: {
                        return checkQuestItems(env, 1, 1, true, 5, 2716); // reward
                    }
                    case FINISH_DIALOG: {
                        return sendQuestSelectionDialog(env);
                    }
                }
            } else if (targetId == 700061) { // Beehive
                if (dialog == QuestDialog.USE_OBJECT) {
                    return true; // loot
                }
            } else if (targetId == 203622) { // Tatural
                if (dialog == QuestDialog.START_DIALOG) {
                    if (var == 0) {
                        return sendQuestDialog(env, 1352);
                    }
                } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    return defaultCloseDialog(env, 0, 1); // 1
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203613) { // Gilungk
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
