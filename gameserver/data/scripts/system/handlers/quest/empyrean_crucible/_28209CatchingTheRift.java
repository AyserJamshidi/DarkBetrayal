/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.empyrean_crucible;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Kamui
 */
public class _28209CatchingTheRift extends QuestHandler {

    private static final int questId = 28209;

    public _28209CatchingTheRift() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205321).addOnQuestStart(questId);
        qe.registerQuestNpc(205321).addOnTalkEvent(questId);
        qe.registerQuestNpc(217819).addOnKillEvent(questId);
        qe.registerQuestNpc(218185).addOnKillEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 205321) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205321) {
                switch (env.getDialog()) {
                    case SELECT_REWARD:
                        return sendQuestDialog(env, 5);
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            int var1 = qs.getQuestVarById(1);

            if (var == 0 && var1 < 4) {
                return defaultOnKillEvent(env, 217819, 0, 4, 1);
            } else if (var == 0 && var1 == 4) {
                return defaultOnKillEvent(env, 217819, 0, 1, 0);
            } else if (var == 1 && env.getTargetId() == 218185) {
                qs.setQuestVarById(2, 1);
                qs.setStatus(QuestStatus.REWARD);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}
