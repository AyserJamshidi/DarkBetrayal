/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.verteron;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Rolandas
 * @reworked vlog
 */
public class _1170HeadlessStoneStatue extends QuestHandler {

    private final static int questId = 1170;

    public _1170HeadlessStoneStatue() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(730000).addOnQuestStart(questId);
        qe.registerQuestNpc(730000).addOnTalkEvent(questId);
        qe.registerQuestNpc(700033).addOnTalkEvent(questId);
        qe.registerOnMovieEndQuest(16, questId);
        qe.registerGetingItem(182200504, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 730000) { // Headless Stone Statue
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 700033) { // Head of Stone Statue
                if (dialog == QuestDialog.USE_OBJECT) {
                    return giveQuestItem(env, 182200504, 1);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 730000) { // Headless Stone Statue
                if (dialog == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 1352);
                } else if (dialog == QuestDialog.STEP_TO_1) {
                    playQuestMovie(env, 16);
                    return closeDialogWindow(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onGetItemEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            changeQuestStep(env, 0, 0, true); // reward
            return true;
        }
        return false;
    }

    @Override
    public boolean onMovieEndEvent(QuestEnv env, int movieId) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (movieId == 16) {
                removeQuestItem(env, 182200504, 1);
                return QuestService.finishQuest(env);
            }
        }
        return false;
    }
}