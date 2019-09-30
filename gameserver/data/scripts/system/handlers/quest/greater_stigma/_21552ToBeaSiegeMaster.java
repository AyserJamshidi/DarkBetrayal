/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.greater_stigma;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _21552ToBeaSiegeMaster extends QuestHandler {

    private final static int questId = 21552;

    public _21552ToBeaSiegeMaster() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205613).addOnQuestStart(questId);
        qe.registerQuestNpc(205613).addOnTalkEvent(questId);
        qe.registerQuestNpc(259014).addOnAttackEvent(questId);
        qe.registerQuestNpc(259214).addOnAttackEvent(questId);
        qe.registerQuestNpc(259414).addOnAttackEvent(questId);
        qe.registerQuestNpc(259614).addOnAttackEvent(questId);
    }

    @Override
    public boolean onKillInWorldEvent(QuestEnv env) {
        return defaultOnKillRankedEvent(env, 0, 10, false); // reward
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (env.getTargetId() == 205613) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 4762);
                    case ACCEPT_QUEST_SIMPLE:
                        return sendQuestStartDialog(env);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1352);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onAttackEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVars().getQuestVars();
            if (var == 0 && env.getTargetId() == 259014) {
                changeQuestStep(env, var, var + 1, false);
                return true;
            } else if (var == 1 && env.getTargetId() == 259214) {
                changeQuestStep(env, var, var + 1, false);
                return true;
            } else if (var == 2 && env.getTargetId() == 259414) {
                changeQuestStep(env, var, var + 1, false);
                return true;
            } else if (var == 3 && env.getTargetId() == 259614) {
                changeQuestStep(env, var, var + 1, true);
                return true;
            }
        }
        return false;
    }
}
