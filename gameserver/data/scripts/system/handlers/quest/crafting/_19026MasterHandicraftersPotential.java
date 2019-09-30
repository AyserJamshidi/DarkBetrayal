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

/**
 * @author Thuatan
 */
public class _19026MasterHandicraftersPotential extends QuestHandler {

    private final static int questId = 19026;

    public _19026MasterHandicraftersPotential() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203792).addOnQuestStart(questId);
        qe.registerQuestNpc(203792).addOnTalkEvent(questId);
        qe.registerQuestNpc(798013).addOnTalkEvent(questId);
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
            if (targetId == 203792) {
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
            switch (targetId) {
                case 798013:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1011);
                        case STEP_TO_10:
                            if (!giveQuestItem(env, 152202049, 1)) {
                                return true;
                            }
                            if (!giveQuestItem(env, 152020249, 1)) {
                                return true;
                            }
                            qs.setQuestVarById(0, 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        case STEP_TO_20:
                            if (!giveQuestItem(env, 152202050, 1)) {
                                return true;
                            }
                            if (!giveQuestItem(env, 152020249, 1)) {
                                return true;
                            }
                            qs.setQuestVarById(0, 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                    }
                case 203792:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            long itemCount1 = player.getInventory().getItemCountByItemId(182206767);
                            if (itemCount1 > 0) {
                                removeQuestItem(env, 182206767, 1);
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                return sendQuestDialog(env, 1352);
                            } else {
                                return sendQuestDialog(env, 10001);
                            }
                    }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203792) {
                if (env.getDialogId() == 34) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
