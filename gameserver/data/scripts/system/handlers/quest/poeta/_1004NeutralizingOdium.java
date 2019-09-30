/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.poeta;

import com.ne.gs.model.EmotionId;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author MrPoke
 */
public class _1004NeutralizingOdium extends QuestHandler {

    private final static int questId = 1004;

    public _1004NeutralizingOdium() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(203082).addOnTalkEvent(questId);
        qe.registerQuestNpc(700030).addOnTalkEvent(questId);
        qe.registerQuestNpc(790001).addOnTalkEvent(questId);
        qe.registerQuestNpc(203067).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203082) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 5) {
                            return sendQuestDialog(env, 2034);
                        }
                    case SELECT_ACTION_1013:
                        if (var == 0) {
                            playQuestMovie(env, 19);
                        }
                        return false;
                    case STEP_TO_1:
                        qs.setQuestVarById(0, var + 1);
                        updateQuestStatus(env);
                        sendQuestSelectionDialog(env);
                        return true;
                    case STEP_TO_3:
                        if (var == 5) {
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            sendQuestSelectionDialog(env);
                            return true;
                        }
                }
            } else if (targetId == 700030 && var == 1 || var == 4) {
                switch (env.getDialog()) {
                    case USE_OBJECT:
                        if (qs.getQuestVarById(0) == 1) {
                            if (giveQuestItem(env, 182200005, 1)) {
                                qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            }
                        } else if (qs.getQuestVarById(0) == 4) {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            removeQuestItem(env, 182200005, 1);
                        }
                        updateQuestStatus(env);
                        sendEmotion(env, player, EmotionId.STAND, true);
                        return false;
                }
            } else if (targetId == 790001) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 2) {
                            return sendQuestDialog(env, 1352);
                        } else if (var == 3) {
                            return sendQuestDialog(env, 1693);
                        } else if (var == 11) {
                            return sendQuestDialog(env, 1694);
                        }
                    case STEP_TO_2:
                        if (var == 2) {
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            sendQuestSelectionDialog(env);
                            return true;
                        }
                    case STEP_TO_3:
                        if (var == 11) {
                            if (!giveQuestItem(env, 182200006, 1)) {
                                return true;
                            }
                            qs.setQuestVarById(0, 4);
                            updateQuestStatus(env);
                            removeQuestItem(env, 182200006, 1);
                            sendQuestSelectionDialog(env);
                            return true;
                        }
                    case CHECK_COLLECTED_ITEMS: {
                        if (QuestService.collectItemCheck(env, true)) {
                            qs.setQuestVarById(0, 11);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 1694);
                        } else {
                            return sendQuestDialog(env, 1779);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203067) {
                player.getInventory().decreaseByItemId(182200005, 1);
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 1100, true);
    }
}
