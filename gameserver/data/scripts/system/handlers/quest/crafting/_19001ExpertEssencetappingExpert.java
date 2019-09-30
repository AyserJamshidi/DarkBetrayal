/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.crafting;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Gigi
 */
public class _19001ExpertEssencetappingExpert extends QuestHandler {

    private final static int questId = 19001;

    public _19001ExpertEssencetappingExpert() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203780).addOnQuestStart(questId);
        qe.registerQuestNpc(203780).addOnTalkEvent(questId);
        qe.registerQuestNpc(798600).addOnTalkEvent(questId);
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
            if (targetId == 203780) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    if (giveQuestItem(env, 182206127, 1)) {
                        return sendQuestDialog(env, 1011);
                    } else {
                        return true;
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 798600: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 2375);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798600) {
                if (env.getDialogId() == 34) {
                    return sendQuestDialog(env, 5);
                } else {
                    player.getSkillList().addSkill(player, 30002, 400);
                    removeQuestItem(env, 182206127, 1);
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
