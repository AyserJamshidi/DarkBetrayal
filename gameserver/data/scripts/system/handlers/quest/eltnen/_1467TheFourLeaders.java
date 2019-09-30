/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.eltnen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Balthazar
 */

public class _1467TheFourLeaders extends QuestHandler {

    private final static int questId = 1467;

    public _1467TheFourLeaders() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204045).addOnQuestStart(questId);
        qe.registerQuestNpc(204045).addOnTalkEvent(questId);
        qe.registerQuestNpc(211696).addOnKillEvent(questId);
        qe.registerQuestNpc(211697).addOnKillEvent(questId);
        qe.registerQuestNpc(211698).addOnKillEvent(questId);
        qe.registerQuestNpc(211699).addOnKillEvent(questId);
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
            if (targetId == 204045) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 4762);
                    }
                    case ACCEPT_QUEST: {
                        return sendQuestDialog(env, 1011);
                    }
                    case STEP_TO_1: {
                        if (QuestService.startQuest(env)) {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                    case STEP_TO_2: {
                        if (QuestService.startQuest(env)) {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 2);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }

                    }
                    case STEP_TO_3: {
                        if (QuestService.startQuest(env)) {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 3);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                    case STEP_TO_4: {
                        if (QuestService.startQuest(env)) {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 4);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                    default:
                        return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204045) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        switch (qs.getQuestVarById(0)) {
                            case 1: {
                                return sendQuestDialog(env, 5);
                            }
                            case 2: {
                                return sendQuestDialog(env, 6);
                            }
                            case 3: {
                                return sendQuestDialog(env, 7);
                            }
                            case 4: {
                                return sendQuestDialog(env, 8);
                            }
                        }
                    }
                    case SELECT_NO_REWARD: {
                        QuestService.finishQuest(env, qs.getQuestVarById(0) - 1);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
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

        int var = 0;
        int targetId = 0;

        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        switch (targetId) {
            case 211696: {
                if (qs.getQuestVarById(0) == 1) {
                    if (var == 0) {
                        var = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return true;
                    }
                }
            }
            case 211697: {
                if (qs.getQuestVarById(0) == 2) {
                    if (var == 0) {
                        var = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return true;
                    }
                }
            }
            case 211698: {
                if (qs.getQuestVarById(0) == 3) {
                    if (var == 0) {
                        var = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return true;
                    }
                }
            }
            case 211699: {
                if (qs.getQuestVarById(0) == 4) {
                    if (var == 0) {
                        var = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}