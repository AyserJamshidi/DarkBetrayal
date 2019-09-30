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

public class _80008PieceOfCake extends QuestHandler {

    private final static int questId = 80008;

    public _80008PieceOfCake() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798415).addOnTalkEvent(questId);
        qe.registerOnLevelUp(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();

        if (env.getTargetId() == 0) {
            if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                QuestService.startEventQuest(env, QuestStatus.START);
                closeDialogWindow(env);
                return true;
            }
        }

        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.START) {
            if (env.getTargetId() == 798415) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 2375);
                        }
                    case SELECT_REWARD:
                        removeQuestItem(env, 182214006, 1);
                        return defaultCloseDialog(env, 0, 1, true, true);
                }
            }
        }
        return sendQuestRewardDialog(env, 798415, 0);
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
