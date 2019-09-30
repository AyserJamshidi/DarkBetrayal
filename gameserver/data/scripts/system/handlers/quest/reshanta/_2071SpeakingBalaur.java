/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.reshanta;

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.spawns.SpawnSearchResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.MathUtil;

/**
 * @author Rhys2002
 * @reworked vlog
 */
public class _2071SpeakingBalaur extends QuestHandler {

    private final static int questId = 2071;

    public _2071SpeakingBalaur() {
        super(questId);
    }

    @Override
    public void register() {
        int[] npcs = {278003, 278086, 278039, 279027, 204210};
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(253610).addOnAttackEvent(questId);
        for (int npc : npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        if (qs == null) {
            return false;
        }
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 278003: { // Hisui
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case STEP_TO_1: {
                            return defaultCloseDialog(env, 0, 1); // 1
                        }
                    }
                    break;
                }
                case 278086: { // Sinjah
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 1) {
                                return sendQuestDialog(env, 1352);
                            }
                        }
                        case STEP_TO_2: {
                            return defaultCloseDialog(env, 1, 2); // 2
                        }
                    }
                    break;
                }
                case 278039: { // Grunn
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 3) {
                                return sendQuestDialog(env, 2034);
                            }
                        }
                        case STEP_TO_4: {
                            return defaultCloseDialog(env, 3, 4); // 4
                        }
                    }
                    break;
                }
                case 279027: { // Kaoranerk
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 4) {
                                return sendQuestDialog(env, 2375);
                            } else if (var == 6) {
                                return sendQuestDialog(env, 3057);
                            }
                        }
                        case SELECT_ACTION_3058: {
                            removeQuestItem(env, 182205501, 1);
                            playQuestMovie(env, 293);
                            return sendQuestDialog(env, 3058);
                        }
                        case STEP_TO_5: {
                            return defaultCloseDialog(env, 4, 5); // 5
                        }
                        case SET_REWARD: {
                            return defaultCloseDialog(env, 6, 6, true, false); // reward
                        }
                    }
                    break;
                }
                case 204210: { // Phosphor
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 5) {
                                return sendQuestDialog(env, 2716);
                            }
                        }
                        case STEP_TO_6: {
                            return defaultCloseDialog(env, 5, 6, 182205501, 1, 0, 0); // 6
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 278003) { // Hisui
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onAttackEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 2) {
                Npc npc = (Npc) env.getVisibleObject();
				SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(npc.getWorldId(), 278086);
                if (MathUtil.getDistance(searchResult.getSpot().getX(), searchResult.getSpot().getY(), searchResult.getSpot().getZ(), npc.getX(), npc.getY(), npc.getZ()) <= 15) {
					if(npc.getLifeStats().getCurrentHp() < npc.getLifeStats().getMaxHp() / 3){
						npc.getController().onDelete();
						changeQuestStep(env, 2, 3, false); // 3
						playQuestMovie(env, 289);
					}
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 2701, true);
    }
}
