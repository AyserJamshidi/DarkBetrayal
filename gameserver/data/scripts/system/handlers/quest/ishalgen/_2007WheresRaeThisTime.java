/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.ishalgen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;


public class _2007WheresRaeThisTime extends QuestHandler {

    private final static int questId = 2007;

    public _2007WheresRaeThisTime() {
        super(questId);
    }

    @Override
    public void register() {
        int[] talkNpcs = {203516, 203519, 203539, 203552, 203554, 700085, 700086, 700087};
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
            switch (targetId) {
                case 203516:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        case STEP_TO_1:
                            if (var == 0) {
                                qs.setQuestVarById(0, var + 1);
                                updateQuestStatus(env);
                                closeDialogWindow(env);
                                return true;
                            }
                    }
                    break;
                case 203519:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 1) {
                                return sendQuestDialog(env, 1352);
                            }
                        case STEP_TO_2:
                            if (var == 1) {
                                qs.setQuestVarById(0, var + 1);
                                updateQuestStatus(env);
                                closeDialogWindow(env);
                                return true;
                            }
                    }
                    break;
                case 203539:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 2) {
                                return sendQuestDialog(env, 1693);
                            }
                        case SELECT_ACTION_1694:
                            playQuestMovie(env, 55);
                            break;
                        case STEP_TO_3:
                            if (var == 2) {
                                qs.setQuestVarById(0, var + 1);
                                updateQuestStatus(env);
                                closeDialogWindow(env);
                                return true;
                            }
                    }
                    break;
                case 203552:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 3) {
                                return sendQuestDialog(env, 2034);
                            }
                        case STEP_TO_4:
                            if (var == 3) {
                                qs.setQuestVarById(0, var + 1);
                                updateQuestStatus(env);
                                closeDialogWindow(env);
                                return true;
                            }
                    }
                    break;
                case 203554:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 4) {
                                return sendQuestDialog(env, 2375);
                            } else if (var == 8) {
                                return sendQuestDialog(env, 2716);
                            }
                        case STEP_TO_5:
                            if (var == 4) {
                                qs.setQuestVar(5);
                                updateQuestStatus(env);
                                closeDialogWindow(env);
                                return true;
                            }
                            break;
                        case STEP_TO_6:
                            if (var == 8) {
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                closeDialogWindow(env);
                                return true;
                            }
                    }
                    break;
                case 700085:
                    if (var == 5) {
                        qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
                        return false;
                    }
                    break;
                case 700086:
                    if (var == 6) {
                        qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
                        return false;
                    }
                    break;
                case 700087:
                    if (var == 7) {
						playQuestMovie(env, 56);
                        qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
                        return false;
                    }
                    break;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203516) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    playQuestMovie(env, 58);
                    return sendQuestDialog(env, 3057);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        int[] quests = {2006, 2005, 2004, 2003, 2002, 2001};
        return defaultOnZoneMissionEndEvent(env, quests);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {2100, 2006, 2005, 2004, 2003, 2002, 2001};
        return defaultOnLvlUpEvent(env, quests, true);
    }
}
