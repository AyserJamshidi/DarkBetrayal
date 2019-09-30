/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sarpan;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

public class _11512LoversLost extends QuestHandler {

    private final static int questId = 11512;

    public _11512LoversLost() {
        super(questId);
    }

    public void register() {
        qe.registerQuestNpc(205989).addOnQuestStart(questId);
        qe.registerQuestNpc(205746).addOnTalkEvent(questId);
        qe.registerQuestNpc(205989).addOnTalkEvent(questId);
        qe.registerQuestNpc(730467).addOnTalkEvent(questId);
        qe.registerQuestNpc(218650).addOnKillEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 205989) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                }
                else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 205989) {
                if (dialog == QuestDialog.START_DIALOG) {
                    if(qs.getQuestVarById(0) == 0) {
                        return sendQuestDialog(env, 1011);
                    }
                }
                else if (dialog == QuestDialog.STEP_TO_1) {
                    return defaultCloseDialog(env, 0, 1);
                }
            }
            else if (targetId == 730467) {
                if (dialog == QuestDialog.USE_OBJECT) {
                    if(qs.getQuestVarById(0) == 1) {
                        return sendQuestDialog(env, 1352);
                    }
                }
                else if (dialog == QuestDialog.STEP_TO_2) {
                    Npc npc = (Npc) env.getVisibleObject();
                    QuestService.addNewSpawn(npc.getWorldId(), npc.getInstanceId(), 218650, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
                    npc.getController().onDelete();
                    return defaultCloseDialog(env, 1, 2);
                }
            }
        }
        else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205746) {
                if (dialog == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        return defaultOnKillEvent(env, 218650, 2, true);
    }
}
