/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.theobomos;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */

public class _3087DivingForTreasure extends QuestHandler {

    private final static int questId = 3087;

    public _3087DivingForTreasure() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798201).addOnQuestStart(questId);
        qe.registerQuestNpc(798201).addOnTalkEvent(questId);
        qe.registerQuestNpc(700419).addOnTalkEvent(questId);
        qe.registerQuestNpc(798144).addOnTalkEvent(questId);
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
            if (targetId == 798201) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1011);
                    }
                    default:
                        return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 700419: {
                    switch (env.getDialog()) {
                        case USE_OBJECT: {
                            if (qs.getQuestVarById(0) == 0) {
                                return sendQuestDialog(env, 1352);
                            }
                        }
                        case STEP_TO_1: {
                            if (player.getInventory().getItemCountByItemId(182208063) == 0) {
                                if (!giveQuestItem(env, 182208063, 1)) {
                                    return true;
                                }
                            }

                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            return true;
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798144) {
                if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
