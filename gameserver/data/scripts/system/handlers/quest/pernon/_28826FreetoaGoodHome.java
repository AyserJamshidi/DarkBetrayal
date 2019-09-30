/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.pernon;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _28826FreetoaGoodHome extends QuestHandler {

    private static final int questId = 28826;

    public _28826FreetoaGoodHome() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(830662).addOnQuestStart(questId);
        qe.registerQuestNpc(830662).addOnTalkEvent(questId);
        qe.registerQuestNpc(730525).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 830662) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 730525) {
                switch (dialog) {
                    case USE_OBJECT: {
                        return sendQuestDialog(env, 2375);
                    }
                    case SELECT_REWARD: {
                        changeQuestStep(env, 0, 0, true);
                        return sendQuestDialog(env, 5);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 730525) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
