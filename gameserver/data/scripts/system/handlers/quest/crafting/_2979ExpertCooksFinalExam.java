/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.crafting;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Gigi
 */
public class _2979ExpertCooksFinalExam extends QuestHandler {

    private final static int questId = 2979;

    public _2979ExpertCooksFinalExam() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204100).addOnQuestStart(questId);
        qe.registerQuestNpc(204100).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204100) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 204100: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            long itemCount1 = player.getInventory().getItemCountByItemId(182207955);
                            long itemCount2 = player.getInventory().getItemCountByItemId(182207956);
                            long itemCount3 = player.getInventory().getItemCountByItemId(182207957);
                            long itemCount4 = player.getInventory().getItemCountByItemId(182207958);
                            if (itemCount1 > 0 && itemCount2 > 0 && itemCount3 > 0 && itemCount4 > 0) {
                                removeQuestItem(env, 182207955, 1);
                                removeQuestItem(env, 182207956, 1);
                                removeQuestItem(env, 182207957, 1);
                                removeQuestItem(env, 182207958, 1);
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                return sendQuestDialog(env, 2375);
                            } else {
                                return sendQuestDialog(env, 2716);
                            }
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204100) {
                if (env.getDialogId() == 34) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
