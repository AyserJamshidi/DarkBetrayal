/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sanctum;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Rolandas
 */
public class _3967AndusDyeBox extends QuestHandler {

    private final static int questId = 3967;

    public _3967AndusDyeBox() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798391).addOnQuestStart(questId);// Andu
        qe.registerQuestNpc(798309).addOnTalkEvent(questId);// Arenzes
        qe.registerQuestNpc(798391).addOnTalkEvent(questId);// Andu
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;

        QuestState qs2 = player.getQuestStateList().getQuestState(3966);
        if (qs2 == null || qs2.getStatus() != QuestStatus.COMPLETE) {
            return false;
        }

        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (targetId == 798391)// Andu
        {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);

        if (targetId == 798309)// Arenzes
        {
            if (qs.getStatus() == QuestStatus.START && var == 0) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1352);
                } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    if (giveQuestItem(env, 182206122, 1)) {
                        qs.setQuestVar(++var);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                    }
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 798391 && qs.getStatus() == QuestStatus.REWARD)// Andu
        {
            if (env.getDialog() == QuestDialog.USE_OBJECT) {
                return sendQuestDialog(env, 2375);
            } else if (env.getDialogId() == 1009) {
                removeQuestItem(env, 182206122, 1);
                return sendQuestEndDialog(env);
            } else {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
