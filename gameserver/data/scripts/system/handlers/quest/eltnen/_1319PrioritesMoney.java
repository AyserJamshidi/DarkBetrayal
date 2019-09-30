/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.eltnen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Xitanium
 */
public class _1319PrioritesMoney extends QuestHandler // NEED FIX ITEM
{

    private final static int questId = 1319;
    private final static int Mapaen = 205240;

    public _1319PrioritesMoney() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203908).addOnQuestStart(questId); // Priorite
        qe.registerQuestNpc(203908).addOnTalkEvent(questId); // Priorite
        qe.registerQuestNpc(203923).addOnTalkEvent(questId); // Krato
        qe.registerQuestNpc(203910).addOnTalkEvent(questId); // Hebestis
        qe.registerQuestNpc(203906).addOnTalkEvent(questId); // Benos
        qe.registerQuestNpc(203915).addOnTalkEvent(questId); // Diokles
        qe.registerQuestNpc(203907).addOnTalkEvent(questId); // Tuskeos
        qe.registerQuestNpc(798050).addOnTalkEvent(questId); // Girrinerk
        qe.registerQuestNpc(798049).addOnTalkEvent(questId); // Shaoranyerk
        qe.registerQuestNpc(798046).addOnTalkEvent(questId); // Arnesonerk
        qe.registerQuestNpc(Mapaen).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203908) // Priorite
            {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) // Reward
        {
            if (env.getDialog() == QuestDialog.START_DIALOG) {
                return sendQuestDialog(env, 4080);
            } else if (env.getDialogId() == 1009) {
                qs.setQuestVar(8);
                qs.setStatus(QuestStatus.REWARD);
                updateQuestStatus(env);
                return sendQuestEndDialog(env);
            } else {
                return sendQuestEndDialog(env);
            }
        } else if (targetId == 203923) // Krato
        {

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
            }

        } else if (targetId == 203910) // Hebestis
        {
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1693);
                } else if (env.getDialog() == QuestDialog.STEP_TO_2) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 203906) // Benos
        {
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 2) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2034);
                } else if (env.getDialog() == QuestDialog.STEP_TO_3) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 203915) // Diokles
        {
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 3) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialog() == QuestDialog.STEP_TO_4) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 203907) // Tuskeos
        {
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 4) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2716);
                } else if (env.getDialogId() == 10004) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 798050) // Girrinerk
        {
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 5) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 3057);
                } else if (env.getDialogId() == 10005) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 798049) // Shaoranranerk
        {
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 6) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 3398);
                } else if (env.getDialogId() == 10006) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == Mapaen)
        {
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 7) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 3739);
                } else if (env.getDialogId() == 10007 && qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE) {
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        return false;
    }
}
