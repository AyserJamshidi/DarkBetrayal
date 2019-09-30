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

import com.ne.commons.utils.Rnd;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.quest.QuestItems;
import com.ne.gs.model.templates.rewards.BonusType;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.EventService;
import com.ne.gs.services.QuestService;

/**
 * @author Rolandas
 */
public class _80018EventSockItToEm extends QuestHandler {

    private final static int questId = 80018;

    public _80018EventSockItToEm() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(799778).addOnQuestStart(questId);
        qe.registerQuestNpc(799778).addOnTalkEvent(questId);
        qe.registerOnBonusApply(questId, BonusType.MOVIE);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if ((qs == null || qs.getStatus() == QuestStatus.NONE) && !onLvlUpEvent(env)) {
            return false;
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE && qs.getCompleteCount() < 10) {
            if (env.getTargetId() == 799778) {
                switch (env.getDialog()) {
                    case USE_OBJECT:
                        return sendQuestDialog(env, 1011);
                    case ACCEPT_QUEST: {
                        QuestService.startEventQuest(env, QuestStatus.START);
                        return sendQuestDialog(env, 1003);
                    }
                    default:
                        return sendQuestStartDialog(env);
                }
            }
            return false;
        }

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.START) {
            if (env.getTargetId() == 799778) {
                switch (env.getDialog()) {
                    case USE_OBJECT:
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 2375);
                        }
                    case CHECK_COLLECTED_ITEMS:
                        return checkQuestItems(env, 0, 1, true, 5, 2716);
                }
            }
        }

        return sendQuestRewardDialog(env, 799778, 0);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (EventService.getInstance().checkQuestIsActive(questId)) {
            if (!QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel())) {
                return false;
            }

            // Start once
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                return QuestService.startEventQuest(env, QuestStatus.START);
            }
        } else if (qs != null) {
            // Set as expired
            QuestService.abandonQuest(player, questId);
        }
        return false;
    }

    @Override
    public HandlerResult onBonusApplyEvent(QuestEnv env, BonusType bonusType, List<QuestItems> rewardItems) {
        if (bonusType != BonusType.MOVIE || env.getQuestId() != questId) {
            return HandlerResult.UNKNOWN;
        }

        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (qs.getCompleteCount() == 9) { // [Event] Hat Box
                rewardItems.add(new QuestItems(188051106, 1));
            }
            // randomize movie
            if (Rnd.chance(50)) {
                playQuestMovie(env, 135);
            } else {
                playQuestMovie(env, 136);
            }
            return HandlerResult.SUCCESS;
        }
        return HandlerResult.FAILED;
    }

}
