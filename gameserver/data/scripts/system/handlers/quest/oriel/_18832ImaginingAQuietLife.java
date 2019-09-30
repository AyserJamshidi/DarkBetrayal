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
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Bobobear & Ritsu
 */
public class _18832ImaginingAQuietLife extends QuestHandler {

    private static final int questId = 18832;

    public _18832ImaginingAQuietLife() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(830365).addOnQuestStart(questId);
        qe.registerQuestNpc(830365).addOnTalkEvent(questId);
        qe.registerQuestNpc(830001).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 830365) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 830001: {
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 2375);
                            }
                        }
                        case SELECT_REWARD: {
                            playQuestMovie(env, 801);
                            changeQuestStep(env, 0, 0, true);
                            return sendQuestDialog(env, 5);
                        }
                    }
                }

            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 830001) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
