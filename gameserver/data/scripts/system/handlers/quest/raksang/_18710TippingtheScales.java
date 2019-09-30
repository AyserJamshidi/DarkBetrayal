/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.raksang;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author zhkchi
 */
public class _18710TippingtheScales extends QuestHandler {

    private static final int questId = 18710;

    public _18710TippingtheScales() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799436).addOnQuestStart(questId);
        qe.registerQuestNpc(799436).addOnTalkEvent(questId);
        qe.addHandlerSideQuestDrop(questId, 217647, 182006427, 1, 100, true);
        qe.addHandlerSideQuestDrop(questId, 217475, 182006427, 1, 100, true);
		qe.registerQuestItem(182006427, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 799436) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        QuestService.startQuest(env);
                        return sendQuestDialog(env, 1011);
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
            if (targetId == 799436) {
                switch (dialog) {
                    case USE_OBJECT:
                        return sendQuestDialog(env, 1011);
                    case SELECT_ACTION_1011:
                        if (player.getInventory().getItemCountByItemId(182006427) >= 4) {
                            removeQuestItem(env, 182006427, 4);
                            qs.setQuestVar(1);
                            qs.setStatus(QuestStatus.REWARD);
                            qs.setCompleteCount(0);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 5);
                        } else {
                            return sendQuestDialog(env, 1009);
                        }
                    case SELECT_ACTION_1352:
                        if (player.getInventory().getItemCountByItemId(182006427) >= 7) {
                            removeQuestItem(env, 182006427, 7);
                            qs.setQuestVar(2);
                            qs.setStatus(QuestStatus.REWARD);
                            qs.setCompleteCount(0);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 6);
                        } else {
                            return sendQuestDialog(env, 1009);
                        }
                    case SELECT_ACTION_1693:
                        if (player.getInventory().getItemCountByItemId(182006427) >= 10) {
                            removeQuestItem(env, 182006427, 10);
                            qs.setQuestVar(3);
                            qs.setStatus(QuestStatus.REWARD);
                            qs.setCompleteCount(0);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 7);
                        } else {
                            return sendQuestDialog(env, 1009);
                        }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799436) {
                int var = qs.getQuestVarById(0);
                switch (dialog) {
                    case USE_OBJECT:
                        if (var == 1) {
                            return sendQuestDialog(env, 5);
                        } else if (var == 2) {
                            return sendQuestDialog(env, 6);
                        } else if (var == 3) {
                            return sendQuestDialog(env, 7);
                        } else if (var == 4) {
                            return sendQuestDialog(env, 8);
                        }
                    case SELECT_NO_REWARD:
                        QuestService.finishQuest(env, qs.getQuestVars().getQuestVars() - 1);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                }
            }
        }
        return false;
    }
}
