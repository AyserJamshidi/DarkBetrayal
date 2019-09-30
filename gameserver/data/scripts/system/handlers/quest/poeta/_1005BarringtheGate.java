/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.poeta;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;


public class _1005BarringtheGate extends QuestHandler {

    private final static int questId = 1005;

    public _1005BarringtheGate() {
        super(questId);
    }

    @Override
    public void register() {
        int[] talkNpcs = {203067, 203081, 790001, 203085, 203086, 700080, 700081, 700082, 700083};
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        for (int id : talkNpcs) {
            qe.registerQuestNpc(id).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203067) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            sendQuestSelectionDialog(env);
                            return true;
                        }
                }
            } else if (targetId == 203081) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
                    case STEP_TO_2:
                        if (var == 1) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            sendQuestSelectionDialog(env);
                            return true;
                        }
                }
            } else if (targetId == 790001) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
                    case STEP_TO_3:
                        if (var == 2) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            sendQuestSelectionDialog(env);
                            return true;
                        }
                }
            } else if (targetId == 203085) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 3) {
                            return sendQuestDialog(env, 2034);
                        }
                    case STEP_TO_4:
                        if (var == 3) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            sendQuestSelectionDialog(env);
                            return true;
                        }
                }
            } else if (targetId == 203086) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
                    case STEP_TO_5:
                        if (var == 4) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            sendQuestSelectionDialog(env);
                            return true;
                        }
                }
            } else if (targetId == 700081) {
                if (var == 5) {
                    qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
                    return false;
                }
            } else if (targetId == 700082) {
                if (var == 6) {
                    qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
                    return false;
                }
            } else if (targetId == 700083) {
                if (var == 7) {
                    qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
                    return false;
                }
            } else if (targetId == 700080) {
                if (var == 8) {
                    playQuestMovie(env, 21);
                    qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
                    return false;
                }
            }

        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203067) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2716);
                }
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        int[] quests = {1100, 1001, 1002, 1003, 1004};
        return defaultOnZoneMissionEndEvent(env, quests);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {1100, 1001, 1002, 1003, 1004};
        return defaultOnLvlUpEvent(env, quests, true);
    }
}
