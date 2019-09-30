/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.event_quests;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.EventService;
import com.ne.gs.services.QuestService;

/**
 * @author Rolandas
 */

public class _80032EventACharmedExistence extends QuestHandler {

    private final static int questId = 80032;

    public _80032EventACharmedExistence() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799781).addOnQuestStart(questId);
        qe.registerQuestNpc(799781).addOnTalkEvent(questId);
        qe.registerOnLevelUp(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 799781) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
                    defaultCloseDialog(env, 0, 0, true, true);
                    return sendQuestDialog(env, 5);
                } else if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
                    return sendQuestRewardDialog(env, 799781, 5);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        return sendQuestRewardDialog(env, 799781, 0);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (!EventService.getInstance().checkQuestIsActive(questId) && qs != null) {
            QuestService.abandonQuest(player, questId);
        }
        return true;
    }

}
