/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.terath_dredgion;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * User: Alexsis
 * Date: 24.04.13
 */
public class _30604Unending_Assault extends QuestHandler {

    private final static int questId = 30604;

    public _30604Unending_Assault() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(800325).addOnQuestStart(questId);
        qe.registerQuestNpc(800325).addOnTalkEvent(questId);
        qe.registerQuestNpc(205842).addOnTalkEvent(questId);
        qe.registerQuestNpc(219245).addOnKillEvent(questId);
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if(qs == null || qs.getStatus() != QuestStatus.START)
            return false;

        int targetId = env.getTargetId();

        switch(targetId) {
            case 219245:
                if (qs.getQuestVarById(2) < 15) {
                    qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
                    updateQuestStatus(env);
                }
                if (qs.getQuestVarById(2) >= 15)	{
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                }
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env)
    {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();

        if(qs == null || qs.getStatus() == QuestStatus.NONE)
        {
            if (targetId == 800325)
            {
                if (env.getDialogId() == 26)
                    return sendQuestDialog(env, 4762);
                else
                    return sendQuestStartDialog(env);
            }
        }
        else if(qs.getStatus() == QuestStatus.START)
        {
            if (targetId == 205842)
            {
                switch(env.getDialogId())
                {
                    case 26:
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    default:
                        return sendQuestStartDialog(env);
                }
            }
            else
                return false;
        }

        else if (qs.getStatus() == QuestStatus.REWARD)
        {
            if (targetId == 205842)
                return sendQuestEndDialog(env);
        }
        return false;
    }
}
