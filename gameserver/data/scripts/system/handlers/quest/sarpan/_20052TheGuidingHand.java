/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sarpan;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author vlog
 */
public class _20052TheGuidingHand extends QuestHandler {

    private static final int questId = 20052;

    public _20052TheGuidingHand() {
        super(questId);
    }

    @Override
    public void register() {
        int[] npcIds = {205987, 205788, 730474, 205988, 205617};
        for (int npcId : npcIds) {
            qe.registerQuestNpc(npcId).addOnTalkEvent(questId);
        }
        qe.registerQuestNpc(218100).addOnKillEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("SARPAN_CAPITOL_600020000"), questId);
        qe.registerOnEnterZone(ZoneName.get("DEBARIM_PETRALITH_STUDIO_600020000"), questId);
        qe.registerOnLevelUp(questId);
        qe.registerOnEnterZoneMissionEnd(questId);
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 20051, true);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 205987: { // Garnon
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 1) {
                                return sendQuestDialog(env, 1352);
                            } else if (var == 4) {
                                return sendQuestDialog(env, 2375);
                            }
                        }
                        case STEP_TO_2: {
                            return defaultCloseDialog(env, 1, 2); // 2
                        }
                        case STEP_TO_5: {
                            return defaultCloseDialog(env, 4, 5); // 5
                        }
                    }
                    break;
                }
                case 205788: { // Nytia
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 2) {
                                return sendQuestDialog(env, 1693);
                            }
                        }
                        case STEP_TO_3: {
                            return defaultCloseDialog(env, 2, 3); // 3
                        }
                    }
                    break;
                }
                case 730474: { // Zayedan's Record
                    switch (dialog) {
                        case USE_OBJECT: {
                            if (var == 6) {
                                return sendQuestDialog(env, 3057);
                            }
                        }
                        case STEP_TO_7: {
                            changeQuestStep(env, 6, 7, false); // 7
                            return closeDialogWindow(env);
                        }
                    }
                    break;
                }
                case 205988: { // Ispharel
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 7) {
                                return sendQuestDialog(env, 3398);
                            }
                        }
                        case SET_REWARD: {
                            return defaultCloseDialog(env, 7, 7, true, false); // reward
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205617) { // Aimah
                if (dialog == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 10002);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (zoneName.equals(ZoneName.get("SARPAN_CAPITOL_600020000"))) {
                if (var == 0) {
                    changeQuestStep(env, 0, 1, false); // 1
                    playQuestMovie(env, 704);
                    return true;
                }
            } else if (zoneName.equals(ZoneName.get("DEBARIM_PETRALITH_STUDIO_600020000"))) {
                if (var == 5) {
                    changeQuestStep(env, 5, 6, false); // 6
                    playQuestMovie(env, 706);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        return defaultOnKillEvent(env, 218100, 3, 4); // 4
    }
}