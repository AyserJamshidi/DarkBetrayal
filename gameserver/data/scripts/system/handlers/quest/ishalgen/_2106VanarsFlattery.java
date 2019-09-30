/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.ishalgen;

import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.teleport.TeleportService;

/**
 * @author zhkchi
 */
public class _2106VanarsFlattery extends QuestHandler {

    private final static int questId = 2106;

    public _2106VanarsFlattery() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203502).addOnQuestStart(questId);
        qe.registerQuestNpc(203502).addOnTalkEvent(questId);
        qe.registerQuestNpc(203517).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();
        QuestState qs = player.getQuestStateList().getQuestState(getQuestId());

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 203502) {
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 4762);
                    }
                    case ASK_ACCEPTION: {
                        return sendQuestDialog(env, 4);
                    }
                    case ACCEPT_QUEST: {
                        if (giveQuestItem(env, 182203106, 1)) {
                            return sendQuestStartDialog(env);
                        }
                    }
                    case REFUSE_QUEST: {
                        return sendQuestDialog(env, 1004);
                    }
                    case FINISH_DIALOG:
                        return closeDialogWindow(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203502) {
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 1003);
                    }
                    case STEP_TO_1:
                        TeleportService.teleportTo(player, 220010000, 576, 2538, 272, (byte) 9, TeleportAnimation.BEAM_ANIMATION);
                        changeQuestStep(env, 0, 1, true);
                        return closeDialogWindow(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203517) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
