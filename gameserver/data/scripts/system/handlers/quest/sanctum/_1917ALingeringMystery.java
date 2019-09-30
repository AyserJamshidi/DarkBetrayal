/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sanctum;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _1917ALingeringMystery extends QuestHandler {

    private final static int questId = 1917;

    public _1917ALingeringMystery() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203835).addOnQuestStart(questId);
        qe.registerQuestNpc(203835).addOnTalkEvent(questId);
        qe.registerQuestNpc(203075).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (sendQuestNoneDialog(env, 203835)) {
            return true;
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203075) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    if (qs.getQuestVarById(0) == 0) {
                        return sendQuestDialog(env, 1352);
                    }
                } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    return defaultCloseDialog(env, 0, 1, true, false);
                }
            }
        }
        return sendQuestRewardDialog(env, 203835, 0);
    }
}
