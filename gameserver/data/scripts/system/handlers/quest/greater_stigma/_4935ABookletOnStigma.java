/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.greater_stigma;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author kecimis, hex1r0
 */
public class _4935ABookletOnStigma extends QuestHandler {

    private final static int QUEST_ID = 4935;

    private final static int VERGELMIR_ID = 204051;
    private final static int TEIRUNERK_ID = 204285;
    private final static int KOHRUNERK_ID = 279005;

    // private final static int PIRATES_RESEARCH_LOG_ID = 182207104;
    private final static int TEIRUNERKS_LETTER_ID = 182207107;
    private final static int TATTERED_BOOKLET_ID = 182207108;

    private final static int[] NPC_IDS = {VERGELMIR_ID, TEIRUNERK_ID, KOHRUNERK_ID};

    public _4935ABookletOnStigma() {
        super(QUEST_ID);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(VERGELMIR_ID).addOnQuestStart(QUEST_ID);
        qe.registerQuestItem(TEIRUNERKS_LETTER_ID, QUEST_ID);
        qe.registerQuestItem(TATTERED_BOOKLET_ID, QUEST_ID);
        for (int npc_id : NPC_IDS) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(QUEST_ID);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(QUEST_ID);
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == VERGELMIR_ID) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
            return false;
        }

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == VERGELMIR_ID) {
                if (player.getInventory().getItemCountByItemId(TATTERED_BOOKLET_ID) >= 1) {
                    if (env.getDialog() == QuestDialog.USE_OBJECT) {
                        return sendQuestDialog(env, 10002);
                    } else if (env.getDialogId() == 1009) {
                        return sendQuestDialog(env, 5);
                    } else {
                        return sendQuestEndDialog(env);
                    }
                } else {
                    // FIXME SM_SYSTEM_MESSAGE?
                    player.sendMsg("You do not have required item: " + TATTERED_BOOKLET_ID);
                    return false;
                }
            }
            return false;
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == TEIRUNERK_ID) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
                    case CHECK_COLLECTED_ITEMS:
                        if (var == 1) {
                            if (QuestService.collectItemCheck(env, true)) {
                                if (!giveQuestItem(env, TEIRUNERKS_LETTER_ID, 1)) {
                                    return false;
                                }
                                qs.setQuestVarById(0, var + 1);
                                updateQuestStatus(env);
                                return sendQuestDialog(env, 10000);
                            } else {
                                return sendQuestDialog(env, 10001);
                            }
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            qs.setQuestVarById(0, var + 1);
                        }
                        updateQuestStatus(env);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                }
                return false;
            } else if (targetId == KOHRUNERK_ID) {
                if (player.getInventory().getItemCountByItemId(TEIRUNERKS_LETTER_ID) >= 1) {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 2) {
                                return sendQuestDialog(env, 1693);
                            }
                        case SET_REWARD:
                            if (var == 2) {
                                removeQuestItem(env, TEIRUNERKS_LETTER_ID, 1);
                            }
                            if (!giveQuestItem(env, TATTERED_BOOKLET_ID, 1)) {
                                return false;
                            }
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return true;
                    }

                } else {
                    // FIXME SM_SYSTEM_MESSAGE?
                    player.sendMsg("You do not have required item: " + TEIRUNERKS_LETTER_ID);
                    return false;
                }
            }
            return false;
        }
        return false;
    }
}
