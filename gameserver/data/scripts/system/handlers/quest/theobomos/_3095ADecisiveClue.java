/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.theobomos;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

public class _3095ADecisiveClue extends QuestHandler {

    private final static int questId = 3095;

    public _3095ADecisiveClue() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(730148).addOnQuestStart(questId); // Red Journal
        qe.registerQuestNpc(730148).addOnTalkEvent(questId);
        qe.registerQuestNpc(798225).addOnTalkEvent(questId);
        qe.registerQuestNpc(203898).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        switch (targetId) {
            case 730148: {
                giveQuestItem(env, 182208053, 1);
            }
            case 798225: {
                if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
                    if (env.getDialog() == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, 1352);
                    } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                        qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                        updateQuestStatus(env);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    } else {
                        return sendQuestStartDialog(env);
                    }
                } else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
                    if (env.getDialog() == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, 2375);
                    } else if (env.getDialogId() == 1009) {
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    } else {
                        return sendQuestStartDialog(env);
                    }
                } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                    return sendQuestEndDialog(env);
                }
            }

            case 203898: {
                if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
                    if (env.getDialog() == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, 1693);
                    } else if (env.getDialog() == QuestDialog.STEP_TO_2) {
                        removeQuestItem(env, 182208053, 1);
                        qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                        updateQuestStatus(env);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    } else {
                        return sendQuestStartDialog(env);
                    }
                }
            }

            case 0: {
                if (env.getDialogId() == 1002) {
                    QuestService.startQuest(env);
                    player.sendPck(new SM_DIALOG_WINDOW(0, 0));
                    return true;
                }
            }
        }
        return false;
    }
}
