/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.beshmundir;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Gigi
 */

public class _30301TrackingSupplies extends QuestHandler {

    private final static int questId = 30301;

    public _30301TrackingSupplies() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799225).addOnQuestStart(questId);
        qe.registerQuestNpc(799225).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = 0;
        if (player.getCommonData().getLevel() < 55) {
            return false;
        }
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        if (targetId == 799225) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (qs.getStatus() == QuestStatus.START) {
                long itemCount;
                if (env.getDialog() == QuestDialog.START_DIALOG && qs.getQuestVarById(0) == 0) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialogId() == 34 && qs.getQuestVarById(0) == 0) {
                    itemCount = player.getInventory().getItemCountByItemId(182209701);
                    if (itemCount > 0) {
                        removeQuestItem(env, 182209701, 1);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    } else {
                        return sendQuestDialog(env, 2716);
                    }
                } else {
                    return sendQuestEndDialog(env);
                }
            } else {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
