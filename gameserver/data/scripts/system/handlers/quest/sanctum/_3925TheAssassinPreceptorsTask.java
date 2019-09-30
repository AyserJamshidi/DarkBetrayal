/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sanctum;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _3925TheAssassinPreceptorsTask extends QuestHandler {

    private final static int questId = 3925;

    public _3925TheAssassinPreceptorsTask() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203705).addOnQuestStart(questId);
        qe.registerQuestNpc(203705).addOnTalkEvent(questId);
        qe.registerQuestSkill(851, 3925);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203705) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            return sendQuestEndDialog(env);
        }
        return false;
    }

    @Override
    public boolean onUseSkillEvent(QuestEnv env, int skillUsedId) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        if (qs.getStatus() != QuestStatus.START) {
            return false;
        }

        if (skillUsedId == 851) {
            if (var >= 0 && var < 9) {
                changeQuestStep(env, var, var + 1, false);
                return true;
            } else if (var == 9) {
                changeQuestStep(env, var, var + 1, true);
                return true;
            }
        }
        return false;
    }
}
