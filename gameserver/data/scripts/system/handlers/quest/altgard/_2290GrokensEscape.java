/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.altgard;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * Escort Groken (203608) to the sailboat (700178). Talk with Manir (203607).
 *
 * @author Mr. Poke
 * @reworked vlog
 */
public class _2290GrokensEscape extends QuestHandler {

    private final static int questId = 2290;

    public _2290GrokensEscape() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203608).addOnQuestStart(questId);
        qe.registerQuestNpc(203608).addOnTalkEvent(questId);
        qe.registerQuestNpc(700178).addOnTalkEvent(questId);
        qe.registerQuestNpc(203607).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203608) { // Groken
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                }
                if (env.getDialogId() == 1007) {
                    return sendQuestDialog(env, 4);
                }
                if (env.getDialogId() == 1002) {
                    return sendQuestDialog(env, 1003);
                }
                if (env.getDialogId() == 1003) {
                    return sendQuestDialog(env, 1004);
                }
                if (env.getDialogId() == 1008) {
                    return sendQuestSelectionDialog(env);
                }
                if (env.getDialogId() == 1012) {
                    if (QuestService.startQuest(env)) {
                        Npc npc = (Npc) env.getVisibleObject();
                        return defaultStartFollowEvent(env, npc, 700178, 0, 1); // 1
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203608) { // Groken
                Npc npc = (Npc) env.getVisibleObject();
                if (env.getDialog() == QuestDialog.START_DIALOG && qs.getQuestVarById(0) == 0) {
                    return defaultStartFollowEvent(env, npc, 700178, 0, 1); // 1
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203607) { // Manir
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1693);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onNpcReachTargetEvent(QuestEnv env) {
        playQuestMovie(env, 69);
        return defaultFollowEndEvent(env, 1, 1, true); // reward
    }

    @Override
    public boolean onNpcLostTargetEvent(QuestEnv env) {
        return defaultFollowEndEvent(env, 1, 0, false); // 0
    }
}
