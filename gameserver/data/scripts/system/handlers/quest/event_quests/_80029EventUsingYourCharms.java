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

/**
 * @author Rolandas
 */

public class _80029EventUsingYourCharms extends QuestHandler {

    private final static int questId = 80029;

    public _80029EventUsingYourCharms() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799766).addOnQuestStart(questId);
        qe.registerQuestNpc(799766).addOnTalkEvent(questId);
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
            if (targetId == 799766) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
                    defaultCloseDialog(env, 0, 0, true, true);
                    return sendQuestDialog(env, 5);
                } else if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
                    return sendQuestRewardDialog(env, 799766, 5);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        return sendQuestRewardDialog(env, 799766, 0);
    }

}
