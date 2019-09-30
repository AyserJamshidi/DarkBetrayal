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
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Atomics
 */
public class _1351EarningMaranasRespect extends QuestHandler {

    private final static int questId = 1351;

    public _1351EarningMaranasRespect() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203965).addOnQuestStart(questId); // Castor
        qe.registerQuestNpc(203965).addOnTalkEvent(questId); // Castor
        qe.registerQuestNpc(203983).addOnTalkEvent(questId); // Marana
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        long itemCount;
        if (targetId == 203965) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 203983) {
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialogId() == 34) {
                    itemCount = player.getInventory().getItemCountByItemId(182201321);
                    if (itemCount > 9) {
                        removeQuestItem(env, 182201321, 10);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    } else {
                        return sendQuestDialog(env, 2716);
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
