/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.commons.utils.Rnd;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Rhys2002
 */
public class _1053TheKlawThreat extends QuestHandler {

    private final static int questId = 1053;
    private final static int[] npc_ids = {204583, 204502};

    public _1053TheKlawThreat() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(700169).addOnKillEvent(questId);
        qe.registerQuestNpc(212120).addOnKillEvent(questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 1500, true);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204502) {
                return sendQuestEndDialog(env);
            }
        } else if (qs.getStatus() != QuestStatus.START) {
            return false;
        }
        if (targetId == 204583) {
            switch (env.getDialog()) {
                case START_DIALOG:
                    if (var == 0) {
                        return sendQuestDialog(env, 1011);
                    } else if (var == 1) {
                        return sendQuestDialog(env, 1352);
                    } else if (var == 2) {
                        return sendQuestDialog(env, 1693);
                    }
                case CHECK_COLLECTED_ITEMS:
                    if (var == 1 && QuestService.collectItemCheck(env, true)) {
                        return sendQuestDialog(env, 10000);
                    } else {
                        return sendQuestDialog(env, 10001);
                    }
                case SELECT_ACTION_1693:
                    playQuestMovie(env, 186);
                    return false;
                case STEP_TO_1:
                    if (var == 0) {
                        qs.setQuestVarById(0, var + 1);
                        updateQuestStatus(env);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
                case STEP_TO_3:
                    if (var == 1) {
                        qs.setQuestVarById(0, var + 2);
                        updateQuestStatus(env);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
                    return false;
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

        Npc npc = (Npc) env.getVisibleObject();
        int targetId = env.getTargetId();

        if (targetId == 700169) {
            int spawn = Rnd.get(5);
            if (spawn == 1) {
                QuestService.addNewSpawn(210040000, 1, 212120, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
                return true;
            }
        } else if (targetId == 212120 && qs.getQuestVarById(0) == 3) {
            qs.setStatus(QuestStatus.REWARD);
            updateQuestStatus(env);
        }
        return false;
    }
}
