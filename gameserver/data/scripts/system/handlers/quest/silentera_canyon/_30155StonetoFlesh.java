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
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Ritsu
 */
public class _30155StonetoFlesh extends QuestHandler {

    private final static int questId = 30155;

    public _30155StonetoFlesh() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799234).addOnQuestStart(questId);
        qe.registerQuestNpc(799234).addOnTalkEvent(questId);
        qe.registerQuestNpc(204433).addOnTalkEvent(questId);
        qe.registerQuestNpc(204304).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799234) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            int var = qs.getQuestVarById(0);
            if (targetId == 204304) {
                if (var == 3) {
                    return sendQuestEndDialog(env);
                }
            } else if (targetId == 799234) {
                switch (dialog) {
                    case SELECT_NO_REWARD:
                        if (var == 2) {
                            QuestService.finishQuest(env, qs.getQuestVars().getQuestVars() - 2);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 799234) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 1) {
                            return sendQuestDialog(env, 1693);
                        }
                    case STEP_TO_2:
                        if (var == 1) {
                            changeQuestStep(env, 1, 2, false);
                            return sendQuestDialog(env, 2375);
                        }
                    case SELECT_REWARD:
                        if (var == 2) {
                            removeQuestItem(env, 182209252, 1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 6);
                        }
                }
            } else if (targetId == 204433) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            return defaultCloseDialog(env, 0, 1, false, false, 182209252, 1, 0, 0);
                        }
                }
            } else if (targetId == 204304) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 1) {
                            return sendQuestDialog(env, 2034);
                        }
                    case STEP_TO_3:
                        if (var == 1) {
                            changeQuestStep(env, 1, 3, false);
                        }
                        return sendQuestDialog(env, 2375);
                    case SELECT_REWARD:
                        if (var == 3) {
                            removeQuestItem(env, 182209252, 1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                        }
                }
            }
        }
        return false;
    }
}
