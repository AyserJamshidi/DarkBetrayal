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
public class _28805SomethingOldSomethingNew extends QuestHandler {

    private static final int questId = 28805;

    public _28805SomethingOldSomethingNew() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(830154).addOnQuestStart(questId);
        qe.registerQuestNpc(830154).addOnTalkEvent(questId);
        qe.registerQuestNpc(830521).addOnTalkEvent(questId);
        qe.registerQuestNpc(830662).addOnTalkEvent(questId);
        qe.registerQuestNpc(830663).addOnTalkEvent(questId);
        qe.registerQuestNpc(730525).addOnTalkEvent(questId);
        qe.registerQuestNpc(730522).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 830154) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 830521:
                case 830662:
                case 830663: {
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1352);
                            } else if (var == 2) {
                                return sendQuestDialog(env, 2375);
                            }
                        }
                        case STEP_TO_1: {
                            return defaultCloseDialog(env, 0, 1);
                        }
                        case SELECT_REWARD: {
                            changeQuestStep(env, 2, 2, true);
                            return sendQuestDialog(env, 5);
                        }
                    }
                }
                case 730525:
                case 730522: {
                    switch (dialog) {
                        case USE_OBJECT: {
                            if (var == 1) {
                                return sendQuestDialog(env, 1693);
                            }
                        }
                        case STEP_TO_2: {
                            return defaultCloseDialog(env, 1, 2);
                        }
                    }
                }

            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            switch (targetId) {
                case 830521:
                case 830662:
                case 830663:
                    return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
