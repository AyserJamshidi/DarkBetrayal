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
 * @author VladimirZ
 */
public class _4012TroublesomePromise extends QuestHandler {

    private final static int questId = 4012;

    public _4012TroublesomePromise() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205142).addOnQuestStart(questId);
        qe.registerQuestNpc(205142).addOnTalkEvent(questId);
        qe.registerQuestNpc(730104).addOnTalkEvent(questId);
        qe.registerQuestNpc(700342).addOnTalkEvent(questId);
        qe.addHandlerSideQuestDrop(questId, 700342, 182209005, 1, 100, true);
        qe.registerGetingItem(182209005, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 205142) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 700342: {
                    if (qs.getQuestVarById(0) == 0 && env.getDialog() == QuestDialog.USE_OBJECT) {
                        return true; // loot
                    }
                    break;
                }
                case 730104: {
                    if (qs.getQuestVarById(0) == 1) {
                        if (env.getDialog() == QuestDialog.START_DIALOG) {
                            return sendQuestDialog(env, 2375);
                        } else if (env.getDialogId() == 34) {
                            removeQuestItem(env, 182209005, 1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 5);
                        } else {
                            return sendQuestEndDialog(env);
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 730104) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onGetItemEvent(QuestEnv env) {
        return defaultOnGetItemEvent(env, 0, 0, true); // reward
    }
}
