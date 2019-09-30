/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.reshanta;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

public class _2722TheComfortsofHome extends QuestHandler {

    private final static int questId = 2722;

    public _2722TheComfortsofHome() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(278047).addOnQuestStart(questId);
        qe.registerQuestNpc(278056).addOnTalkEvent(questId);
        qe.registerQuestNpc(278126).addOnTalkEvent(questId);
        qe.registerQuestNpc(278043).addOnTalkEvent(questId);
        qe.registerQuestNpc(278032).addOnTalkEvent(questId);
        qe.registerQuestNpc(278037).addOnTalkEvent(questId);
        qe.registerQuestNpc(278040).addOnTalkEvent(questId);
        qe.registerQuestNpc(278068).addOnTalkEvent(questId);
        qe.registerQuestNpc(278066).addOnTalkEvent(questId);
        qe.registerQuestNpc(278047).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 278047) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 278056) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            } else if (targetId == 278126) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1352);
                } else if (env.getDialog() == QuestDialog.STEP_TO_2) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            } else if (targetId == 278043) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1693);
                } else if (env.getDialog() == QuestDialog.STEP_TO_3) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            } else if (targetId == 278032) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2034);
                } else if (env.getDialog() == QuestDialog.STEP_TO_4) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            } else if (targetId == 278037) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialogId() == 10004) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            } else if (targetId == 278040) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2716);
                } else if (env.getDialogId() == 10005) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            } else if (targetId == 278068) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 3057);
                } else if (env.getDialogId() == 10006) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            } else if (targetId == 278066) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 3398);
                } else if (env.getDialogId() == 10255) {
                    if (!giveQuestItem(env, 182205654, 1)) {
                        return true;
                    }
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD && targetId == 278047) {
            return sendQuestEndDialog(env);
        }
        return false;
    }
}
