/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.verteron;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * Quest starter: Nemia (203132). Give the Odella (182200526) (1) to Eradis (203130) and ask him to cook it. Buy some
 * Verteron Pepper (169400112) for Eradis.
 *
 * @author vlog
 */
public class _1152OdellaRecipe extends QuestHandler {

    private final static int questId = 1152;

    public _1152OdellaRecipe() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203132).addOnQuestStart(questId);
        qe.registerQuestNpc(203132).addOnTalkEvent(questId);
        qe.registerQuestNpc(203130).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203132) { // Nemia
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env, 182200526, 1);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 203130) { // Eradis
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        } else if (var == 1) {
                            return sendQuestDialog(env, 2375);
                        }
                    }
                    case STEP_TO_1: {
                        return defaultCloseDialog(env, 0, 1, 0, 0, 182200526, 1); // 1
                    }
                    case CHECK_COLLECTED_ITEMS: {
                        return checkQuestItems(env, 1, 1, true, 5, 2716); // reward
                    }
                    case FINISH_DIALOG: {
                        return sendQuestSelectionDialog(env);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203130) { // Eradis
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
