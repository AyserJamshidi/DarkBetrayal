/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.event_quests;

/**
 * @author Rolandas
 */

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.EmotionId;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.QuestTemplate;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.EventService;
import com.ne.gs.services.QuestService;

public class _80020EventSoloriusJoy extends QuestHandler {

    private final static int questId = 80020;
    private final static int[] npcs = {799769, 799768, 203170, 203140};

    public _80020EventSoloriusJoy() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(799769).addOnQuestStart(questId);
        for (int npc : npcs) {
            qe.registerQuestNpc(npc).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if ((qs == null || qs.getStatus() == QuestStatus.NONE) && !onLvlUpEvent(env)) {
            return false;
        }

        QuestTemplate template = DataManager.QUEST_DATA.getQuestById(env.getQuestId());

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE && qs.getCompleteCount() < template.getMaxRepeatCount()) {
            if (env.getTargetId() == 799769) {
                if (env.getDialog() == QuestDialog.USE_OBJECT || env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestNoneDialog(env, 799769, 182214012, 1);
                }
            }
            return false;
        }

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.START) {
            if (env.getTargetId() == 799768) {
                if (env.getDialog() == QuestDialog.USE_OBJECT || env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1352);
                } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    defaultCloseDialog(env, 0, 1, 182214013, 2, 182214012, 1);
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (env.getTargetId() == 203170 && var == 1) {
                if (env.getDialog() == QuestDialog.USE_OBJECT || env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1693);
                } else if (env.getDialog() == QuestDialog.SELECT_ACTION_1694) {
                    sendEmotion(env, (Creature) env.getVisibleObject(), EmotionId.NO, true);
                    return sendQuestDialog(env, 1694);
                } else if (env.getDialog() == QuestDialog.STEP_TO_2) {
                    defaultCloseDialog(env, 1, 2, 0, 0, 182214013, 1);
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (env.getTargetId() == 203140 && var == 2) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2034);
                } else if (env.getDialog() == QuestDialog.SELECT_ACTION_2035) {
                    sendEmotion(env, (Creature) env.getVisibleObject(), EmotionId.PANIC, true);
                    return sendQuestDialog(env, 2035);
                } else if (env.getDialog() == QuestDialog.STEP_TO_3) {
                    return defaultCloseDialog(env, 2, 3, true, false, 0, 0, 0, 182214013, 1);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        return sendQuestRewardDialog(env, 799769, 2375);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (EventService.getInstance().checkQuestIsActive(questId)) {
            return QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel());
        } else if (qs != null) {
            // Set as expired
            QuestService.abandonQuest(player, questId);
        }
        return false;
    }

}
