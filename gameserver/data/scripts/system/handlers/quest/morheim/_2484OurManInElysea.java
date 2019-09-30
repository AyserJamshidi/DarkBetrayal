/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.morheim;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Mr.Poke remod by Nephis and quest helper team
 */
public class _2484OurManInElysea extends QuestHandler {

    private final static int questId = 2484;

    public _2484OurManInElysea() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204407).addOnQuestStart(questId);
        qe.registerQuestNpc(204407).addOnTalkEvent(questId);
        qe.registerQuestNpc(700267).addOnTalkEvent(questId);
        qe.registerQuestNpc(203331).addOnTalkEvent(questId);
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
            if (targetId == 204407) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else if (env.getDialogId() == 1002) {
                    if (giveQuestItem(env, 182204205, 1)) {
                        return sendQuestStartDialog(env);
                    } else {
                        return true;
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 700267:
                    if (qs.getQuestVarById(0) == 0 && env.getDialog() == QuestDialog.USE_OBJECT) {
                        qs.setQuestVarById(0, 1);
                        updateQuestStatus(env);
                        removeQuestItem(env, 182204205, 1);
                    }
                case 203331: {
                    if (qs.getQuestVarById(0) == 1) {
                        if (env.getDialogId() == 18) {
                            return sendQuestDialog(env, 5);
                        } else if (env.getDialog() == QuestDialog.START_DIALOG) {
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 5);
                        } else {
                            return sendQuestEndDialog(env);
                        }
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203331) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
