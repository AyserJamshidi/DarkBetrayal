/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.inggison;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author dta3000
 * @reworked Gigi
 */

public class _11010AngelToTheWounded extends QuestHandler {

    private final static int questId = 11010;

    public _11010AngelToTheWounded() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798931).addOnQuestStart(questId);
        qe.registerQuestNpc(798931).addOnTalkEvent(questId);
        qe.registerQuestNpc(799071).addOnTalkEvent(questId);
        qe.registerQuestNpc(798906).addOnTalkEvent(questId);
        qe.registerQuestNpc(730323).addOnTalkEvent(questId);
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
            if (targetId == 798931) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs == null) {
            return false;
        }

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 799071:	
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 0) {
                                return sendQuestDialog(env, 1352);
                            }
							if (var == 3) {
                                return defaultCloseDialog(env, 3, 3, true, false);
                            }
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1); // 1
                    }
                    break;
                case 798906:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 1) {
                                return sendQuestDialog(env, 1693);
                            }
                        case STEP_TO_2:
                            return defaultCloseDialog(env, 1, 2); // 2
                    }
                    break;
                case 730323:
                    if (env.getDialog() == QuestDialog.USE_OBJECT) {
						if (var == 2) {
							return sendQuestDialog(env, 2034);
						}
                    } else if (env.getDialogId() == 2035){
						return defaultCloseDialog(env, 2, 3);
					}
                    return false;
            }
        }
        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799071) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2375);
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
