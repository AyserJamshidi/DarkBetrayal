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
 * @author Mr. Poke remod by Nephis and quest helper team
 */
public class _4015TheMissingLaborers extends QuestHandler {

    private final static int questId = 4015;

    public _4015TheMissingLaborers() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205130).addOnQuestStart(questId);
        qe.registerQuestNpc(205130).addOnTalkEvent(questId);
        qe.registerQuestNpc(730107).addOnTalkEvent(questId);
        qe.registerQuestNpc(205130).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            if (targetId == 205130) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 730107: {
                    if (qs.getQuestVarById(0) == 0 && env.getDialog() == QuestDialog.USE_OBJECT) {
                        return useQuestObject(env, 0, 1, false, false); // 1
                    }
                }
                case 205130: {
                    if (qs.getQuestVarById(0) == 1) {
                        if (env.getDialog() == QuestDialog.START_DIALOG) {
                            return sendQuestDialog(env, 2375);
                        } else if (env.getDialogId() == 1009) {
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestEndDialog(env);
                        } else {
                            return sendQuestEndDialog(env);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205130) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
