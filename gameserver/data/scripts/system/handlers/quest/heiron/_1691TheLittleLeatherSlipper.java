/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */

public class _1691TheLittleLeatherSlipper extends QuestHandler {

    private final static int questId = 1691;

    public _1691TheLittleLeatherSlipper() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798386).addOnQuestStart(questId);
        qe.registerQuestNpc(798386).addOnTalkEvent(questId);
        qe.registerQuestNpc(790005).addOnTalkEvent(questId);
        qe.registerQuestNpc(700563).addOnTalkEvent(questId);
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
            if (targetId == 798386) {
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

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 790005: {
                    switch (env.getDialog()) {
						case START_DIALOG: {
							return sendQuestDialog(env, 1352);
						}
						case SELECT_ACTION_1353: {
							return sendQuestDialog(env, 1353);
						}
						case STEP_TO_1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
                    }
                }
                case 798386: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            if (qs.getQuestVarById(0) == 1) {
                                return sendQuestDialog(env, 1693);
                            }
                        }
                        case STEP_TO_2: {
                            changeQuestStep(env, 1, 2, false);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                }
                case 700563: {
                    switch (env.getDialog()) {
                        case USE_OBJECT: {
                            if (qs.getQuestVarById(0) == 2) {
                                return sendQuestDialog(env, 2034);
                            }
                        }
                        case STEP_TO_3: {
                            changeQuestStep(env, 2, 3, true);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798386) {
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
