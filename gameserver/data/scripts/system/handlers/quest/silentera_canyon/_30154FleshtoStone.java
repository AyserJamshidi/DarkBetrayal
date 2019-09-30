/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.silentera_canyon;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 */
public class _30154FleshtoStone extends QuestHandler {

    private final static int questId = 30154;

    public _30154FleshtoStone() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799234).addOnQuestStart(questId);
        qe.registerQuestNpc(799234).addOnTalkEvent(questId);
        qe.registerQuestNpc(204433).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799234) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799234) {
                return sendQuestEndDialog(env);
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 204433) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            return defaultCloseDialog(env, 0, 1);
                        }
                }
            }
            if (targetId == 799234) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 1) {
                            return sendQuestDialog(env, 2375);
                        }
                    case SELECT_REWARD:
                        if (var == 1) {
                            return defaultCloseDialog(env, 1, 1, true, true);
                        }
                }
            }
        }
        return false;
    }
}
