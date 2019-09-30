/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

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
 * Go to the Mudthorn Experiment Lab and find Belbua (204645). When you're ready to leave, talk to Belbua. Escort Belbua
 * outside the Mudthorn Experiment Lab. Let Phuthollo (204519) know Belbua is free.
 *
 * @author Rhys2002
 * @reworked vlog
 */
public class _1614WheresBelbua extends QuestHandler {

    private final static int questId = 1614;

    public _1614WheresBelbua() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204519).addOnQuestStart(questId);
        qe.registerQuestNpc(204519).addOnTalkEvent(questId);
        qe.registerQuestNpc(204645).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204519) { // Phuthollo
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 204645: { // Belbua
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            if (qs.getQuestVarById(0) == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case STEP_TO_1: {
                            Npc npc = (Npc) env.getVisibleObject();
                            npc.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
                            player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, npc,376, 529, 133));
                            return defaultCloseDialog(env, 0, 1); // 1
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204519) { // Phuthollo
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onNpcReachTargetEvent(QuestEnv env) {
        return defaultFollowEndEvent(env, 1, 1, true); // reward
    }

    @Override
    public boolean onNpcLostTargetEvent(QuestEnv env) {
        return defaultFollowEndEvent(env, 1, 0, false); // 0
    }
}
