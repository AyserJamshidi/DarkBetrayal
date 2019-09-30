/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sanctum;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _1938BlackCloudFakery extends QuestHandler {

    private final static int questId = 1938;
    private final static int[] npcs = {203703, 279001, 279008};

    public _1938BlackCloudFakery() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203703).addOnQuestStart(questId);
        for (int npc : npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();

        if (sendQuestNoneDialog(env, 203703)) {
            return true;
        }

        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 279001:
                    switch (dialog) {
                        case START_DIALOG:
                            if (var == 0) {
                                return sendQuestDialog(env, 1352);
                            }
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1);
                    }
                    break;
                case 279008:
                    switch (dialog) {
                        case START_DIALOG:
                            if (var == 1) {
                                return sendQuestDialog(env, 1693);
                            }
                        case STEP_TO_2:
                            return defaultCloseDialog(env, 1, 2, true, false);
                    }
                    break;
            }
        }
        return sendQuestRewardDialog(env, 203703, 2375);
    }
}
