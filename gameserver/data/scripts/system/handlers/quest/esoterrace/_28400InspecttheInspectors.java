/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.esoterrace;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 */
public class _28400InspecttheInspectors extends QuestHandler {

    private final static int questId = 28400;

    public _28400InspecttheInspectors() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799557).addOnQuestStart(questId);
        qe.registerQuestNpc(799557).addOnTalkEvent(questId);
        qe.registerQuestNpc(799587).addOnTalkEvent(questId);
        qe.registerQuestNpc(799588).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();

        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (targetId == 799557) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialogId() == 26) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 799587 || targetId == 799588) {
            if (qs != null) {
                if (env.getDialogId() == 26 && qs.getStatus() == QuestStatus.START) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialogId() == 1009) {
                    qs.setQuestVar(1);
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return sendQuestEndDialog(env);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
