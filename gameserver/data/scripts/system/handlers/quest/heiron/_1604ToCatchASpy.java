/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.MathUtil;

/**
 * @author Balthazar
 */

public class _1604ToCatchASpy extends QuestHandler {

    private final static int questId = 1604;

    public _1604ToCatchASpy() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204576).addOnQuestStart(questId);
        qe.registerQuestNpc(204576).addOnTalkEvent(questId);
        qe.registerQuestNpc(212615).addOnAttackEvent(questId);
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
            if (targetId == 204576) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204576) {
                if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onAttackEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVars().getQuestVars() != 0) {
            return false;
        }

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        if (targetId != 212615) {
            return false;
        }

        if (MathUtil.getDistance(env.getVisibleObject(), 700.78f, 634.50f, 132) <= 8) {
            ((Npc) env.getVisibleObject()).getController().onDie(player);
            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
            qs.setStatus(QuestStatus.REWARD);
            updateQuestStatus(env);

        }
        return false;
    }
}
