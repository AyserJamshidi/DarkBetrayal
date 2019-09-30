/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.tiamaranta;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestActionType;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Luzien
 */
public class _41598MakingBalaurLuck extends QuestHandler {

    private final static int questId = 41598;

    public _41598MakingBalaurLuck() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(730555).addOnQuestStart(questId);
        qe.registerQuestNpc(730555).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 730555) {
                switch (dialog) {
                    case USE_OBJECT: {
                        if (!QuestService.inventoryItemCheck(env, true)) {
                            return true;
                        } else {
                            return sendQuestDialog(env, 1011);
                        }
                    }
                    case ACCEPT_QUEST_SIMPLE: {
                        if (!player.getInventory().isFull()) {
                            if (QuestService.startQuest(env)) {
                                return sendQuestDialog(env, 2375);
                            }
                        } else {
                            player.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_FULL_INVENTORY);
                            return sendQuestSelectionDialog(env);
                        }
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 730555) {
                if (dialog.equals(QuestDialog.CHECK_COLLECTED_ITEMS_SIMPLE)) {
                    if (QuestService.collectItemCheck(env, false)) {
                        changeQuestStep(env, 0, 0, true);
                        return sendQuestDialog(env, 5);
                    }
                }
                return sendQuestDialog(env, 2375);
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 730555) {
                if (dialog == QuestDialog.SELECT_NO_REWARD) {
                    if (QuestService.collectItemCheck(env, true)) {
                        return sendQuestEndDialog(env);
                    }
                } else {
                    return QuestService.abandonQuest(player, questId);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
        return env.getTargetId() == 730555;
    }
}
