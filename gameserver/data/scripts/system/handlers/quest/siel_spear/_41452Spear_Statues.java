/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.siel_spear;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * User: Alexsis
 * Date: 16.05.13
 */
public class _41452Spear_Statues extends QuestHandler {
    private final static int questId = 41452;

    private final static int kahrun = 205579;
    private final static int seles = 205591;

    public _41452Spear_Statues() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(kahrun).addOnQuestStart(questId);
        qe.registerQuestNpc(kahrun).addOnTalkEvent(questId);
        qe.registerQuestNpc(seles).addOnTalkEvent(questId);
    }


    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();

        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == kahrun) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case ACCEPT_QUEST_SIMPLE:
                        return sendQuestStartDialog(env);
                    case REFUSE_QUEST_SIMPLE:
                        return sendQuestEndDialog(env);
                }
            }

            return false;
        }

        int var = qs.getQuestVarById(0);
        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case seles:
                    switch (dialog) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1352);
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1);
                    }
                    break;
                case kahrun:
                    switch (dialog) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 2375);
                        case CHECK_COLLECTED_ITEMS_SIMPLE:
                            return checkQuestItemsSimple(env, var, var, true, 5, 0, 0); //reward
                    }
                    break;

            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == kahrun) {
                switch (dialog) {
                    case SELECT_REWARD:
                        return sendQuestDialog(env, 5);
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

}
