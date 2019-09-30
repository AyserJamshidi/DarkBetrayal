/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.theobomos;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */

public class _3102TheDisappearingStatue extends QuestHandler {

    private final static int questId = 3102;

    public _3102TheDisappearingStatue() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798206).addOnQuestStart(questId);
        qe.registerQuestNpc(798206).addOnTalkEvent(questId);
        qe.registerQuestNpc(798167).addOnTalkEvent(questId);
        qe.registerQuestNpc(798177).addOnTalkEvent(questId);
        qe.registerQuestNpc(798225).addOnTalkEvent(questId);

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
            if (targetId == 798206) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1011);
                    }
                    default:
                        return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 798167: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1352);
                        }
                        case STEP_TO_1: {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            return true;
                        }
                    }
                }
                case 798177: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1693);
                        }
                        case STEP_TO_2: {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            return true;
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798225) {
                if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
