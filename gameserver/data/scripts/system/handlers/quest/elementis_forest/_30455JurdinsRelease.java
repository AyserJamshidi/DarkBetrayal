/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.elementis_forest;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Ritsu
 */
public class _30455JurdinsRelease extends QuestHandler {

    private static final int questId = 30455;

    public _30455JurdinsRelease() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205492).addOnQuestStart(questId);
        qe.registerQuestNpc(205492).addOnTalkEvent(questId);
        qe.registerQuestNpc(282203).addOnTalkEvent(questId);
        qe.registerQuestNpc(282204).addOnTalkEvent(questId);
        qe.registerQuestNpc(217238).addOnKillEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 205492) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 282203: {
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case STEP_TO_1: {
                            Npc npc = (Npc) env.getVisibleObject();
                            npc.getController().scheduleRespawn();
                            npc.getController().onDelete();
                            QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 217238, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
                            return defaultCloseDialog(env, 0, 1);
                        }
                    }
                }

                case 282204: {
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 2) {
                                return sendQuestDialog(env, 1693);
                            }
                        }
                        case SET_REWARD: {
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return closeDialogWindow(env);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205492) {
                switch (dialog) {
                    case USE_OBJECT: {
                        return sendQuestDialog(env, 10002);
                    }
                    default: {
                        return sendQuestEndDialog(env);
                    }
                }
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
            case 217238:
                if (qs.getQuestVarById(0) == 1) {
                    Npc npc = (Npc) env.getVisibleObject();
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 282204, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
                }
        }
        return false;
    }
}
