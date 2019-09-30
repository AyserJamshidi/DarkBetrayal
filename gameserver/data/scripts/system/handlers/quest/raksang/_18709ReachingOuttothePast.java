/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.raksang;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _18709ReachingOuttothePast extends QuestHandler {

    private static final int questId = 18709;

    public _18709ReachingOuttothePast() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799429).addOnQuestStart(questId);
        qe.registerQuestNpc(799429).addOnTalkEvent(questId);
        qe.registerQuestNpc(203890).addOnTalkEvent(questId);
        qe.registerQuestNpc(203864).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 799429) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1011);
                    }
                    case ACCEPT_QUEST_SIMPLE: {
                        return sendQuestStartDialog(env);
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 203890) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1352);
                    case SELECT_ACTION_1353:
                        return sendQuestDialog(env, 1353);
                    case STEP_TO_1:
                        return defaultCloseDialog(env, 0, 1);
                }
            } else if (targetId == 203864) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 2375);
                    case SELECT_REWARD:
                        changeQuestStep(env, 1, 1, true);
                        return sendQuestDialog(env, 5);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203864) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
