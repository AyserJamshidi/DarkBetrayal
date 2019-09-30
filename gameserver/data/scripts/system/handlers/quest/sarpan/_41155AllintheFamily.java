/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sarpan;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _41155AllintheFamily extends QuestHandler {

    private final static int questId = 41155;

    public _41155AllintheFamily() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205592).addOnQuestStart(questId);
        qe.registerQuestNpc(205592).addOnTalkEvent(questId);
        qe.registerQuestNpc(800082).addOnTalkEvent(questId);
        qe.registerQuestNpc(800083).addOnTalkEvent(questId);
        qe.registerQuestNpc(205572).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 205592) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case ACCEPT_QUEST_SIMPLE:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 800082) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1352);
                    case STEP_TO_1:
                        return defaultCloseDialog(env, 0, 1);
                }
            } else if (targetId == 800083) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1693);
                    case STEP_TO_2:
                        return defaultCloseDialog(env, 1, 2);
                }
            } else if (targetId == 205572) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 2375);
                    case SELECT_REWARD:
                        changeQuestStep(env, 2, 2, true);
                        return sendQuestDialog(env, 5);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205572) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
