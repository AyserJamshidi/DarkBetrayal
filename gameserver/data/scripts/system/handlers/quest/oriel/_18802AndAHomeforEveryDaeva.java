/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.oriel;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.modules.housing.Housing;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _18802AndAHomeforEveryDaeva extends QuestHandler {

    private static final int questId = 18802;

    public _18802AndAHomeforEveryDaeva() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(830005).addOnQuestStart(questId);
        qe.registerQuestNpc(830005).addOnTalkEvent(questId);
        qe.registerQuestNpc(830069).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 830005) {
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1011);
                    }
                    case ACCEPT_QUEST:
                    case ACCEPT_QUEST_SIMPLE:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 830069: {
                    switch (dialog) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 2375);
                        }
                        case SELECT_REWARD: {
                            changeQuestStep(env, 0, 0, true);
                            return sendQuestEndDialog(env);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 830069) {
                if (dialog.equals(QuestDialog.SELECT_NO_REWARD)) {
                    Housing.giveFlat(player);
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

}
