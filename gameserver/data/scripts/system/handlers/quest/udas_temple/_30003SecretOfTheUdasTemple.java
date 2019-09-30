/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.udas_temple;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author vlog
 */
public class _30003SecretOfTheUdasTemple extends QuestHandler {

    private static final int questId = 30003;

    public _30003SecretOfTheUdasTemple() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799029).addOnQuestStart(questId);
        qe.registerQuestNpc(799029).addOnTalkEvent(questId);
        qe.registerGetingItem(182209161, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799029) { // Honeus
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 799029) { // Honeus
                if (dialog == QuestDialog.START_DIALOG) {
                    if (var == 1) {
                        return sendQuestDialog(env, 2375);
                    }
                } else if (dialog == QuestDialog.SELECT_REWARD) {
                    changeQuestStep(env, 1, 1, true); // reward
                    return sendQuestDialog(env, 5);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799029) { // Honeus
                removeQuestItem(env, 182209161, 1);
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onGetItemEvent(QuestEnv env) {
        return defaultOnGetItemEvent(env, 0, 1, false); // 1
    }
}
