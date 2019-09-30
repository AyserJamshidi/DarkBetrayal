/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.brusthonin;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.MathUtil;

/**
 * @author Nephis
 */
public class _4077PorgusRoundup extends QuestHandler {

    private final static int questId = 4077;

    public _4077PorgusRoundup() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205158).addOnQuestStart(questId); // Holekk
        qe.registerQuestNpc(205158).addOnTalkEvent(questId);
        qe.registerQuestNpc(214732).addOnAttackEvent(questId);
    }

    @Override
    public boolean onAttackEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        if (targetId != 214732) {
            return false;
        }

        Npc npc = (Npc) env.getVisibleObject();
        if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
            if (MathUtil.getDistance(1356, 1901, 46, npc.getX(), npc.getY(), npc.getZ()) > 10) {
                return false;
            }
            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
            updateQuestStatus(env);
            npc.getController().scheduleRespawn();
            npc.getController().onDelete();
            return true;
        } else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {

            if (MathUtil.getDistance(1356, 1901, 46, npc.getX(), npc.getY(), npc.getZ()) > 10) {
                return false;
            }
            qs.setStatus(QuestStatus.REWARD);
            updateQuestStatus(env);
            npc.getController().scheduleRespawn();
            npc.getController().onDelete();
            return true;
        }

        return false;
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
            if (targetId == 205158) // Holekk
            {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205158) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}