/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.brusthonin;

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
 * @author Nephis
 */
public class _4042MessageinaBottle extends QuestHandler {

    private final static int questId = 4042;

    public _4042MessageinaBottle() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(730150).addOnQuestStart(questId); // Bottle
        qe.registerQuestNpc(730150).addOnTalkEvent(questId);
        qe.registerQuestNpc(205192).addOnTalkEvent(questId); // Sahnu
        qe.registerQuestNpc(204225).addOnTalkEvent(questId); // Gunter
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
            case 730150: {
                return giveQuestItem(env, 182209024, 1);
            }
            case 205192: {
                if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
                    if (env.getDialog() == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, 1352);
                    } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                        if (!giveQuestItem(env, 182209025, 1)) {
                            return true;
                        }
                        removeQuestItem(env, 182209024, 1);
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

            case 204225: {
                if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
                    if (env.getDialog() == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, 1693);
                    } else if (env.getDialog() == QuestDialog.STEP_TO_2) {
                        removeQuestItem(env, 182209025, 1);
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
