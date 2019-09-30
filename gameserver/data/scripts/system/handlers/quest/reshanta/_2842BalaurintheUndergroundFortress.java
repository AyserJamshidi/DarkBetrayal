/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.reshanta;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Hilgert
 */

public class _2842BalaurintheUndergroundFortress extends QuestHandler {

    private final static int questId = 2842;

    public _2842BalaurintheUndergroundFortress() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(266568).addOnQuestStart(questId);
        qe.registerQuestNpc(266568).addOnTalkEvent(questId);
        qe.registerQuestNpc(215447).addOnKillEvent(questId);
        qe.registerQuestNpc(214777).addOnKillEvent(questId);
        qe.registerQuestNpc(214773).addOnKillEvent(questId);
        qe.registerQuestNpc(214781).addOnKillEvent(questId);
        qe.registerQuestNpc(214784).addOnKillEvent(questId);
        qe.registerQuestNpc(700472).addOnKillEvent(questId);
        qe.registerQuestNpc(700474).addOnKillEvent(questId);
        qe.registerQuestNpc(700473).addOnKillEvent(questId);
        qe.registerOnEnterWorld(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE) {
            if (targetId == 266568) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 266568) {
                return true;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD && targetId == 266568) {
            qs.setQuestVarById(0, 0);
            updateQuestStatus(env);
            return sendQuestEndDialog(env);
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (player.getCommonData().getPosition().getMapId() == 300070000) {
                if (qs.getQuestVarById(0) < 38) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    return true;
                } else if (qs.getQuestVarById(0) == 38 || qs.getQuestVarById(0) > 38) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return true;
                }
            }
        }
        return false;
    }
}