/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.crafting;

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
 * @author Ritsu
 */
public class _19057MasterConstructorsPotential extends QuestHandler {

    private final static int questId = 19057;
    private final static int[] recipesItemIds = {152203543, 152203544};
    private final static int[] recipesIds = {155003543, 155003544};

    public _19057MasterConstructorsPotential() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798450).addOnQuestStart(questId);
        qe.registerQuestNpc(798450).addOnTalkEvent(questId);
        qe.registerQuestNpc(798451).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 798450) {
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

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 798451:
                    long kinah = player.getInventory().getKinah();
                    switch (dialog) {
                        case START_DIALOG: {
                            switch (var) {
                                case 0:
                                    return sendQuestDialog(env, 1011);
                                case 2:
                                    return sendQuestDialog(env, 4080);
                            }
                        }
                        case STEP_TO_10:
                            if (kinah >= 167500) // Need check how many kinah decrased
                            {
                                if (!giveQuestItem(env, 152203543, 1)) {
                                    return true;
                                }
                                player.getInventory().decreaseKinah(167500);
                                qs.setQuestVarById(0, 1);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                            } else {
                                return sendQuestDialog(env, 4400);
                            }
                        case STEP_TO_20:
                            if (kinah >= 223000) {
                                if (!giveQuestItem(env, 152203544, 1)) {
                                    return true;
                                }
                                player.getInventory().decreaseKinah(223000);
                                qs.setQuestVarById(0, 1);
                                updateQuestStatus(env);
                                player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                                return true;
                            } else {
                                return sendQuestDialog(env, 4400);
                            }
                    }
                case 798450:
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1352);
                        }
                        case CHECK_COLLECTED_ITEMS:
                            if (QuestService.collectItemCheck(env, true)) {
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                return sendQuestDialog(env, 5);
                            } else {
                                int checkFailId = 3398;
                                if (player.getRecipeList().isRecipePresent(recipesIds[0]) || player.getRecipeList().isRecipePresent(recipesIds[1])) {
                                    checkFailId = 2716;
                                } else if (player.getInventory().getItemCountByItemId(recipesItemIds[0]) > 0
                                    || player.getInventory().getItemCountByItemId(recipesItemIds[1]) > 0) {
                                    checkFailId = 3057;
                                }

                                if (checkFailId == 3398) {
                                    qs.setQuestVar(2);
                                    updateQuestStatus(env);
                                }
                                return sendQuestDialog(env, checkFailId);
                            }
                    }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798450) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
