/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.RewardType;
import com.ne.gs.model.templates.quest.Rewards;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */

public class _1692ADayOlderAndDeeperInDebt extends QuestHandler {

    private final static int questId = 1692;

    public _1692ADayOlderAndDeeperInDebt() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798386).addOnQuestStart(questId);
        qe.registerQuestNpc(798386).addOnTalkEvent(questId);
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
            if (targetId == 798386) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 798386: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            long itemCount1 = player.getInventory().getItemCountByItemId(152000104);
                            if (itemCount1 >= 10) {
                                return sendQuestDialog(env, 1003);
                            }
                        }
                        case SELECT_NO_REWARD: {
                            removeQuestItem(env, 152000104, 1);
                            removeQuestItem(env, 182400001, 1);
                            qs.setStatus(QuestStatus.COMPLETE);
                            qs.setCompleteCount(1);
                            Rewards rewards = DataManager.QUEST_DATA.getQuestById(questId).getRewards().get(0);
                            int rewardExp = rewards.getExp();
                            int rewardKinah = (int) (player.getRates().getQuestKinahRate() * rewards.getGold());
                            giveQuestItem(env, 182400001, rewardKinah);
                            player.getCommonData().addExp(rewardExp, RewardType.QUEST);
                            player.sendPck(new SM_QUEST_ACTION(questId, QuestStatus.COMPLETE, 2));
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            updateQuestStatus(env);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
