/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.altgard;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Mr.Poke
 * @reworked vlog
 */
public class _2237AFertileField extends QuestHandler {

    private final static int questId = 2237;

    public _2237AFertileField() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203629).addOnQuestStart(questId);
        qe.registerQuestNpc(203629).addOnTalkEvent(questId);
        qe.registerQuestNpc(700145).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203629) { // Daike
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 700145: { // Fertilizer Sack
                    if (dialog == QuestDialog.USE_OBJECT) {
                        return true; // loot
                    }
                    break;
                }
                case 203629: { // Daike
                    switch (dialog) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 2375);
                        }
                        case CHECK_COLLECTED_ITEMS: {
                            return checkQuestItems(env, 0, 0, true, 5, 2716);
                        }
                        case FINISH_DIALOG: {
                            return sendQuestSelectionDialog(env);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203629) { // Daike
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
