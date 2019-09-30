/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.satra_treasure_hoard;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 */
public class _29804ApsilonsAbilities extends QuestHandler {

    private static final int questId = 28904;

    public _29804ApsilonsAbilities() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(800331).addOnQuestStart(questId);
        qe.registerQuestNpc(205866).addOnTalkEvent(questId);
        qe.registerQuestNpc(219350).addOnKillEvent(questId);
        qe.registerQuestNpc(219352).addOnKillEvent(questId);
        qe.registerQuestNpc(219354).addOnKillEvent(questId);
        qe.registerQuestNpc(219356).addOnKillEvent(questId);
        qe.registerQuestNpc(219358).addOnKillEvent(questId);
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();

        switch (targetId) {
            case 219350:
            case 219352:
            case 219354:
            case 219356:
            case 219358:
                if (var >= 0 && var < 10) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 800331) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 205866) {
                if (env.getDialog() == QuestDialog.START_DIALOG && qs.getQuestVarById(0) == 10) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return sendQuestDialog(env, 1352);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205866) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 5);
                } else if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
