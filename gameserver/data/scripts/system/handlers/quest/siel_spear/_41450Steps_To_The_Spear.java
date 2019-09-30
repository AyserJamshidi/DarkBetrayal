/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.siel_spear;

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.SystemMessageId;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * User: Alexsis
 * Date: 24.04.13
 */
public class _41450Steps_To_The_Spear extends QuestHandler {
    private final static int questId = 41450;

    private final static int[] npc_ids = {205798, 205799, 205800, 205801, 205579, 730527, 800280, 800298};

    public _41450Steps_To_The_Spear() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnDie(questId);
        qe.registerQuestNpc(205579).addOnQuestStart(questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 205579) {
                if (dialog == QuestDialog.START_DIALOG) {
                    QuestService.questTimerStart(env, 780);
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 205798) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (dialog == QuestDialog.STEP_TO_1) {
                    return defaultCloseDialog(env, 0, 1);
                }
            }
            if (targetId == 205799 && var == 1) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1352);
                } else if (dialog == QuestDialog.STEP_TO_2) {
                    return defaultCloseDialog(env, 1, 2);
                }
            }
            if (targetId == 205800 && var == 2) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1693);
                } else if (dialog == QuestDialog.STEP_TO_3) {
                    return defaultCloseDialog(env, 2, 3);
                }
            }
            if (targetId == 205801 && var == 3) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2034);
                } else if (dialog == QuestDialog.STEP_TO_4) {
                    return defaultCloseDialog(env, 3, 4);
                }
            }
            if (targetId == 205579 && var == 4) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2375);
                } else if (dialog == QuestDialog.STEP_TO_5) {
                    return defaultCloseDialog(env, 4, 5);
                }
            }
            if (targetId == 205579 && var == 5) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2716);
                } else if (dialog == QuestDialog.CHECK_COLLECTED_ITEMS_SIMPLE) {
                    return checkQuestItems(env, 5, 6, true, 10002, 10001);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205579) {
                if (dialog == QuestDialog.SELECT_REWARD) {
                    QuestService.questTimerEnd(env);
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onQuestTimerEndEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var > 1) {
                qs.setQuestVarById(0, 0);
                updateQuestStatus(env);
                player.sendPck(new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
                    DataManager.QUEST_DATA.getQuestById(questId).getName()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onDieEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var > 1) {
                qs.setQuestVarById(0, 0);
                updateQuestStatus(env);
                player.sendPck(new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
                    DataManager.QUEST_DATA.getQuestById(questId).getName()));
                return true;
            }
        }
        return false;
    }
}