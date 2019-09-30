/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2014, Neon-Eleanor Team. All rights reserved.
 */
package quest.kromedes_trial;

import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestActionType;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author VladimirZ
 */
public class _18627KaligasCollection extends QuestHandler {

    private final static int questId = 18627;

    public _18627KaligasCollection() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(730335).addOnQuestStart(questId);
        qe.registerQuestNpc(730335).addOnTalkEvent(questId);
    }

    @Override
    public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
        int targetId = env.getTargetId();
        if (targetId == 730335) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (targetId == 730335 && player.getRace() == Race.ELYOS) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialog() == QuestDialog.CHECK_COLLECTED_ITEMS) {
                    if (player.getInventory().getItemCountByItemId(185000102) >= 1) {
                        removeQuestItem(env, 185000102, 1);
                        qs.setStatus(QuestStatus.REWARD);
                        qs.setQuestVar(1);
                        qs.setCompleteCount(0);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    } else {
                        return sendQuestDialog(env, 2716);
                    }
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                int var = qs.getQuestVarById(0);
                switch (env.getDialog()) {
                    case USE_OBJECT:
                        if (var == 1) {
                            return sendQuestDialog(env, 5);
                        }
                    case SELECT_NO_REWARD:
                        QuestService.finishQuest(env, qs.getQuestVars().getQuestVars() - 1);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                }
            }
            return false;
        }
        return false;
    }
}
