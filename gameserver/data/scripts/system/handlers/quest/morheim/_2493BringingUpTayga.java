/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.morheim;

import com.ne.gs.ai2.event.AIEventType;
import com.ne.gs.model.TaskId;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.questEngine.task.QuestTasks;

/**
 * @author MrPoke remod By Nephis
 * @reworked vlog
 */
public class _2493BringingUpTayga extends QuestHandler {

    private final static int questId = 2493;

    public _2493BringingUpTayga() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204325).addOnQuestStart(questId);
        qe.registerQuestNpc(204325).addOnTalkEvent(questId);
        qe.registerQuestNpc(204435).addOnTalkEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204325) { // Ipoderr
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 204435) { // Purra?
                if (dialog == QuestDialog.START_DIALOG) {
                    if (var == 0) {
                        return sendQuestDialog(env, 1011);
                    }
                } else if (dialog == QuestDialog.SET_REWARD) {
                    Npc npc = (Npc) env.getVisibleObject();
                    npc.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
                    player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, npc, 204325));
                    changeQuestStep(env, 0, 0, true); // reward
                    return closeDialogWindow(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204325) { // Ipoderr
                if (dialog == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onNpcLostTargetEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            qs.setStatus(QuestStatus.START);
            updateQuestStatus(env);
            return true;
        }
        return false;
    }
}
