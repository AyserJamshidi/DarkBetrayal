/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.morheim;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Hellboy aion4Free
 */
public class _2035TheThreeKeys extends QuestHandler {

    private final static int questId = 2035;
    private final static int[] npc_ids = {204317, 204408, 204407};

    public _2035TheThreeKeys() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerOnEnterWorld(questId);
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
        return defaultOnLvlUpEvent(env, 2300, true);
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.getWorldId() == 320050000 && qs.getQuestVarById(0) == 5) {
                qs.setQuestVar(6);
                updateQuestStatus(env);
            }
        }
        return false;
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
            switch (targetId) {
                case 204317: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        case STEP_TO_1:
                            if (var == 0) {
                                qs.setQuestVarById(0, 4);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                            }
                    }
                }
                break;
                case 204408: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 4) {
                                return sendQuestDialog(env, 2375);
                            } else if (var == 6) {
                                return sendQuestDialog(env, 2716);
                            }
                        case SELECT_ACTION_2376:
                            playQuestMovie(env, 78);
                            break;
						case SELECT_ACTION_2377:
                            playQuestMovie(env, 160);
                            break;
                        case STEP_TO_5:
                            if (var == 4) {
                                if (!giveQuestItem(env, 182204012, 1)) {
                                    return true;
                                }
                                qs.setQuestVarById(0, 5);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                            }
                        case CHECK_COLLECTED_ITEMS:
                            if (var == 6) {
                                if (QuestService.collectItemCheck(env, true)) {
                                    removeQuestItem(env, 182204012, 1);
                                    qs.setStatus(QuestStatus.REWARD);
                                    updateQuestStatus(env);
                                    return sendQuestDialog(env, 10000);
                                } else {
                                    return sendQuestDialog(env, 10001);
                                }
                            }

                    }
                }
                break;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204407) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}