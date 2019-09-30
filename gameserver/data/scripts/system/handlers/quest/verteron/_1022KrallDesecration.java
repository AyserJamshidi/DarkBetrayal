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
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author MrPoke, Dune11
 */
public class _1022KrallDesecration extends QuestHandler {

    private final static int questId = 1022;
    private final static int[] mob_ids = {210178};

    public _1022KrallDesecration() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203178).addOnTalkEvent(questId);
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        for (int mob_id : mob_ids) {
            qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
        }
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env, 1017);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {1130, 1017};
        return defaultOnLvlUpEvent(env, quests, true);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203178) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                    case STEP_TO_1:
                    case STEP_TO_2:
                        if (var == 0) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203178) {
                return sendQuestEndDialog(env);
            }
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

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        switch (targetId) {
            case 216892:
			case 210178:
                if (var >= 1 && var <= 4) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    return true;
                } else if (var == 5) {
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return true;
                }
        }
        return false;
    }
}