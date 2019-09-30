/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.altgard;

import com.ne.gs.model.gameobjects.Npc;
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
public class _2239MalodorAntidote extends QuestHandler {

    private final static int questId = 2239;

    public _2239MalodorAntidote() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203613).addOnQuestStart(questId); // Gilungk
        qe.registerQuestNpc(203613).addOnTalkEvent(questId);// Gilungk
        qe.registerQuestNpc(203630).addOnTalkEvent(questId); // Vovetirn
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203613) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 203630) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
                        if (var == 1) {
                            return sendQuestDialog(env, 1693);
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    case CHECK_COLLECTED_ITEMS:
                        if (var == 1) {
                            if (QuestService.collectItemCheck(env, true)) {
                                if (!giveQuestItem(env, 182203227, 1)) {
                                    return true;
                                }
                                qs.setQuestVarById(0, qs.getQuestVarById(0) + 2);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                            } else {
                                return sendQuestDialog(env, 1694);
                            }
                        }
                }
            }
            if (targetId == 203613) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
                    case STEP_TO_3:
                        if (var == 3) {
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 5);
                        }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203613) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
