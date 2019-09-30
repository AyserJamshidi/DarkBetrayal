/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.brusthonin;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Nephis
 */
public class _4004TheSeedsOfHope extends QuestHandler {

    private final static int questId = 4004;

    public _4004TheSeedsOfHope() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205128).addOnQuestStart(questId); // Randet
        qe.registerQuestNpc(205128).addOnTalkEvent(questId); // Randet
        qe.registerQuestNpc(700340).addOnTalkEvent(questId); // Earth Mound
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
            if (targetId == 205128) { // Randet
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205128) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 700340: { // Earth Mound
                    if (qs != null && env.getDialog() == QuestDialog.USE_OBJECT) {
                        if (var < 4) {
                            return useQuestObject(env, var, var + 1, false, true);
                        } else if (var == 4) {
                            return useQuestObject(env, 4, 4, true, true); // reward
                        }
                    }
                }
            }
        }
        return false;
    }
}
