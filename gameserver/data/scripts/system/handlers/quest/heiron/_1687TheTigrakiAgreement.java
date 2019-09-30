/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * Go to Draupnir Cave in Asmodae and get Blue Balaur Blood (186000035) (2) and Balaur Rainbow Scales (186000036) (5)
 * for Brosia (204601). Go to Brosia to choose your reward.
 *
 * @author Balthazar
 * @reworked vlog
 */

public class _1687TheTigrakiAgreement extends QuestHandler {

    private final static int questId = 1687;
    private int rewardGroup;

    public _1687TheTigrakiAgreement() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204601).addOnQuestStart(questId);
        qe.registerQuestNpc(204601).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 204601) { // Brosia
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 204601) { // Brosia
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case CHECK_COLLECTED_ITEMS: {
                        long collect1 = player.getInventory().getItemCountByItemId(186000035);
                        long collect2 = player.getInventory().getItemCountByItemId(186000036);
                        if (collect1 >= 2 && collect2 >= 5) {
                            removeQuestItem(env, 186000035, 2);
                            removeQuestItem(env, 186000036, 5);
                            return sendQuestDialog(env, 1352); // choose your reward
                        } else {
                            return sendQuestDialog(env, 1097);
                        }
                    }
                    case FINISH_DIALOG:
                        return defaultCloseDialog(env, var, var);
                    case STEP_TO_10: {
                        rewardGroup = 0;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    }
                    case STEP_TO_20: {
                        rewardGroup = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 6);
                    }
                    case STEP_TO_30: {
                        rewardGroup = 2;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 7);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204601) { // Brosia
                return sendQuestEndDialog(env, rewardGroup);
            }
        }
        return false;
    }
}
