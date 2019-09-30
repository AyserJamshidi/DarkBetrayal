/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.raksang;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _18716TheEnchantedMace extends QuestHandler {

    private static final int questId = 18716;

    public _18716TheEnchantedMace() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799433).addOnQuestStart(questId);
        qe.registerQuestNpc(799433).addOnTalkEvent(questId);
        qe.registerQuestNpc(205591).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 799433) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1011);
                    }
                    default:
                        return sendQuestStartDialog(env, 182212237, 1);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 205591) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 2375);
                    case SELECT_REWARD:
                        return checkQuestItems(env, 0, 0, true, 5, 2120);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205591) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
