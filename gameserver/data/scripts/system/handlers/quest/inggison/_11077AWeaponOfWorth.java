/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.inggison;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _11077AWeaponOfWorth extends QuestHandler {

    private final static int questId = 11077;

    public _11077AWeaponOfWorth() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798926).addOnQuestStart(questId); // Outremus
        qe.registerQuestNpc(798926).addOnTalkEvent(questId); // Outremus
        qe.registerQuestNpc(799028).addOnTalkEvent(questId); // Brontes
        qe.registerQuestNpc(798918).addOnTalkEvent(questId); // Pilipides
        qe.registerQuestNpc(798903).addOnTalkEvent(questId); // Drenia
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 798926) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    default: {
                        if (!giveQuestItem(env, 182214016, 1)) {
                            updateQuestStatus(env);
                        }
                        return sendQuestStartDialog(env);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 799028: // Brontes
                {
                    switch (dialog) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1353);
                        }
                        case SELECT_ACTION_1353: {
                            return sendQuestDialog(env, 1353);
                        }
                        case STEP_TO_1: {
                            return defaultCloseDialog(env, 0, 1);
                        }
                    }
                }
                case 798918: // Pilipides
                {
                    switch (dialog) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1693);
                        }
                        case SELECT_ACTION_1694: {
                            return sendQuestDialog(env, 1694);
                        }
                        case STEP_TO_2: {
                            return defaultCloseDialog(env, 1, 2);
                        }
                    }
                }
                case 798903: // Drenia
                {
                    switch (dialog) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 2375);
                        }
                        case SELECT_REWARD: {
                            return defaultCloseDialog(env, 2, 3, true, true);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798903) // Drenia
            {
                switch (env.getDialogId()) {
                    case 1009: {
                        return sendQuestDialog(env, 5);
                    }
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
