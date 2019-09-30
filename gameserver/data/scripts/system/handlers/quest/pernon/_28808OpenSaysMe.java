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
 * @author Ritsu
 */
public class _28808OpenSaysMe extends QuestHandler {

    private static final int questId = 28808;

    public _28808OpenSaysMe() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(830392).addOnQuestStart(questId);
        qe.registerQuestNpc(830392).addOnTalkEvent(questId);
        qe.registerQuestNpc(730534).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 830392) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                }
                if (dialog == QuestDialog.ACCEPT_QUEST_SIMPLE) {
                    if (giveQuestItem(env, 182213216, 1)) {
                        return sendQuestStartDialog(env);
                    } else {
                        return true;
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 730534: {
                    switch (dialog) {
                        case USE_OBJECT: {
                            if (var == 0) {
                                return sendQuestDialog(env, 2375);
                            }
                        }
                        case SELECT_REWARD: {
                            changeQuestStep(env, 0, 0, true);
                            return sendQuestDialog(env, 5);
                        }
                    }
                }

            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 730534) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
