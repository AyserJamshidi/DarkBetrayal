/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.theobomos;

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.RewardType;
import com.ne.gs.model.templates.quest.Rewards;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */

public class _3096ExamineTheExtractionDevices extends QuestHandler {

    private final static int questId = 3096;

    public _3096ExamineTheExtractionDevices() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798225).addOnQuestStart(questId);
        qe.registerQuestNpc(798225).addOnTalkEvent(questId);
        qe.registerQuestNpc(700423).addOnTalkEvent(questId);
        qe.registerQuestNpc(700424).addOnTalkEvent(questId);
        qe.registerQuestNpc(700425).addOnTalkEvent(questId);
        qe.registerQuestNpc(700426).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE) {
            if (targetId == 798225) {
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
                case 798225: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            long itemCount1 = player.getInventory().getItemCountByItemId(182208067);
                            long itemCount2 = player.getInventory().getItemCountByItemId(182208068);
                            long itemCount3 = player.getInventory().getItemCountByItemId(182208069);
                            long itemCount4 = player.getInventory().getItemCountByItemId(182208070);
                            if (itemCount1 >= 1 && itemCount2 >= 1 && itemCount3 >= 1 && itemCount4 >= 1) {
                                return sendQuestDialog(env, 5);
                            }
                        }
                        case SELECT_NO_REWARD: {
                            qs.setStatus(QuestStatus.COMPLETE);
                            qs.setCompleteCount(qs.getCompleteCount() + 1);
                            removeQuestItem(env, 182208067, 1);
                            removeQuestItem(env, 182208068, 1);
                            removeQuestItem(env, 182208069, 1);
                            removeQuestItem(env, 182208070, 1);
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
                case 700423: {
                    switch (env.getDialog()) {
                        case USE_OBJECT: {
                            if (player.getInventory().getItemCountByItemId(182208067) < 1) {
                                return true;
                            }
                        }
                    }
                }
                case 700424: {
                    switch (env.getDialog()) {
                        case USE_OBJECT: {
                            if (player.getInventory().getItemCountByItemId(182208068) < 1) {
                                return true;
                            }
                        }
                    }
                }
                case 700425: {
                    switch (env.getDialog()) {
                        case USE_OBJECT: {
                            if (player.getInventory().getItemCountByItemId(182208069) < 1) {
                                return true;
                            }
                        }
                    }
                }
                case 700426: {
                    switch (env.getDialog()) {
                        case USE_OBJECT: {
                            if (player.getInventory().getItemCountByItemId(182208070) < 1) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
