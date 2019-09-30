/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.eltnen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Atomics
 */
public class _1376AMountaineOfTrouble extends QuestHandler {

    private final static int questId = 1376;

    public _1376AMountaineOfTrouble() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203947).addOnQuestStart(questId); // Beramones
        qe.registerQuestNpc(203947).addOnTalkEvent(questId); // Beramones
        qe.registerQuestNpc(203964).addOnTalkEvent(questId); // Agrips
        qe.registerQuestNpc(210976).addOnKillEvent(questId); // Kerubien Hunter
        qe.registerQuestNpc(210986).addOnKillEvent(questId); // Kerubien Hunter
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
            if (targetId == 203947) // Beramones
            {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203964) // Agrips
            {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 1352);
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        int[] mobs = {210976, 210986};
        if (defaultOnKillEvent(env, mobs, 0, 6) || defaultOnKillEvent(env, mobs, 6, true)) {
            return true;
        } else {
            return false;
        }
    }
}
