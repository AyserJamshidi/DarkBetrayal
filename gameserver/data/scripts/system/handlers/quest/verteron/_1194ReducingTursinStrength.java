/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.verteron;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Balthazar
 */
public class _1194ReducingTursinStrength extends QuestHandler {

    private final static int questId = 1194;

    public _1194ReducingTursinStrength() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203098).addOnQuestStart(questId);
        qe.registerQuestNpc(203098).addOnTalkEvent(questId);
        qe.registerQuestNpc(210185).addOnKillEvent(questId);
        qe.registerQuestNpc(210186).addOnKillEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("TURSIN_GARRISON_210030000"), questId);
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
            if (targetId == 203098) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203098) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        return sendQuestDialog(env, 1352);
                    }
                    case SELECT_REWARD: {
                        return sendQuestDialog(env, 5);
                    }
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        if (zoneName != ZoneName.get("TURSIN_GARRISON_210030000")) {
            return false;
        }

        Player player = env.getPlayer();
        if (player == null) {
            return false;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null) {
            return false;
        }

        if (qs.getQuestVarById(0) == 0) {
            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
            updateQuestStatus(env);
            return true;
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

        int targetId = 0;
        int questVar = qs.getQuestVarById(0);

        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (targetId == 210185 || targetId == 210186) {
            if (questVar >= 1 && questVar < 10) {
                if (questVar == 9) {
                    qs.setStatus(QuestStatus.REWARD);
                } else {
                    qs.setQuestVarById(0, questVar + 1);
                }
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}
