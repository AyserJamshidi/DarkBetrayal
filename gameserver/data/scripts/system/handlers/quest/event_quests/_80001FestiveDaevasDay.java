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

public class _80001FestiveDaevasDay extends QuestHandler {

    private final static int questId = 80001;

    public _80001FestiveDaevasDay() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798417).addOnTalkEvent(questId); // Belenus
        qe.registerOnLevelUp(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (env.getTargetId() == 0) {
            if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                QuestService.startEventQuest(env, QuestStatus.START);
                closeDialogWindow(env);
                return true;
            }
        } else if (env.getTargetId() == 798417) // Belenus
        {
            if (qs != null) {
                if (env.getDialog() == QuestDialog.START_DIALOG && qs.getStatus() == QuestStatus.START) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
                    qs.setQuestVar(1);
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
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
