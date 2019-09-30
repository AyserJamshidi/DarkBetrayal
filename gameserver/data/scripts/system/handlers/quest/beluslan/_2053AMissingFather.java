/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.beluslan;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Rhys2002
 * @modified & reworked Gigi, vlog
 */
public class _2053AMissingFather extends QuestHandler {

    private final static int questId = 2053;

    public _2053AMissingFather() {
        super(questId);
    }

    @Override
    public void register() {
        int[] npcs = {204707, 204749, 204800, 730108};
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestItem(182204305, questId);
        qe.registerOnEnterZone(ZoneName.get("MALEK_MINE_220040000"), questId);
        qe.registerOnEnterWorld(questId);
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
            if (targetId == 204707) { // Mani
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        } else if (var == 5) {
                            return sendQuestDialog(env, 2716);
                        }
                    }
                    case STEP_TO_1: {
                        return defaultCloseDialog(env, 0, 1); // 1
                    }
                    case STEP_TO_6: {
                        return defaultCloseDialog(env, 5, 6, 0, 0, 182204306, 1); // 6
                    }
                }
            } else if (targetId == 204749) { // Paeru
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
                    }
                    case STEP_TO_2: {
                        return defaultCloseDialog(env, 1, 2, 182204305, 1, 0, 0); // 2
                    }
                }
            } else if (targetId == 730108) { // Strahein's Liquor Bottle
                switch (dialog) {
                    case USE_OBJECT: {
                        if (var == 4) {
                            return sendQuestDialog(env, 2375);
                        }
                    }
                    case STEP_TO_5: {
                        return defaultCloseDialog(env, 4, 5, 182204306, 1, 182204305, 1); // 5
                    }
                }
            } else if (targetId == 204800) { // Hammel
                switch (dialog) {
                    case START_DIALOG: {
                        if (var == 6) {
                            return sendQuestDialog(env, 3057);
                        }
                    }
                    case STEP_TO_7: {
                        return defaultCloseDialog(env, 6, 7); // 7
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204707) { // Mani
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
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 2) {
                // TODO: readable text dialog
                return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false)); // 3
            }
        }
        return HandlerResult.FAILED;
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName name) {
        Player player = env.getPlayer();
        if (player == null) {
            return false;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 3) {
                changeQuestStep(env, 3, 4, false); // 4
            }
        }
        return false;
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.getWorldId() == 320110000) {
                changeQuestStep(env, 7, 7, true); // reward
                removeQuestItem(env, 182204307, 1);
                return true;
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
        return defaultOnLvlUpEvent(env, 2500, true);
    }
}
