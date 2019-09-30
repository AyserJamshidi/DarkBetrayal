/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.event_quests;

import java.util.List;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.model.templates.quest.QuestItems;
import com.ne.gs.model.templates.rewards.BonusType;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Rolandas
 */

public class _80037EventFromTheGutter extends QuestHandler {

    private final static int questId = 80037;

    public _80037EventFromTheGutter() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799780).addOnQuestStart(questId);
        qe.registerQuestNpc(799780).addOnTalkEvent(questId);
        qe.registerOnLevelUp(questId);
        qe.registerOnBonusApply(questId, BonusType.LUNAR);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.COMPLETE && QuestService.collectItemCheck(env, false)) {
            if (targetId == 799780) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                    return sendQuestDialog(env, 1003);
                } else if (env.getDialog() == QuestDialog.CHECK_COLLECTED_ITEMS) {
                    return checkQuestItems(env, 0, 0, true, 5, 2716);
                } else if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
                    return sendQuestRewardDialog(env, 799780, 5);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (env.getDialog() == QuestDialog.USE_OBJECT) {
                return sendQuestDialog(env, 5);
            }
            return sendQuestEndDialog(env);
        }
        return sendQuestRewardDialog(env, 799780, 0);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        if (env.getQuestId() != questId) {
            return false;
        }
        Player player = env.getPlayer();
        Storage storage = player.getInventory();
        if (storage.getItemCountByItemId(164002016) > 9) {
            QuestService.startEventQuest(new QuestEnv(null, player, questId, 0), QuestStatus.START);
        }

        return false;
    }

    @Override
    public HandlerResult onBonusApplyEvent(QuestEnv env, BonusType bonusType, List<QuestItems> rewardItems) {
        if (bonusType != BonusType.LUNAR) {
            return HandlerResult.UNKNOWN;
        }
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.COMPLETE)) {
            if (qs.getQuestVarById(0) == 0) {
                return HandlerResult.SUCCESS;
            }
        }
        return HandlerResult.FAILED;
    }

}
