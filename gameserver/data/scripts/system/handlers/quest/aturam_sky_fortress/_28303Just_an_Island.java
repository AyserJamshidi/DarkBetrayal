/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.aturam_sky_fortress;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.teleport.TeleportService;

/**
 * @author zhkchi
 */
public class _28303Just_an_Island extends QuestHandler {

    private final static int questId = 28303;

    public _28303Just_an_Island() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799530).addOnQuestStart(questId);
        qe.registerQuestNpc(799530).addOnTalkEvent(questId);
        qe.registerQuestNpc(730390).addOnTalkEvent(questId);
        qe.registerQuestNpc(700980).addOnTalkEvent(questId);
        qe.registerQuestNpc(799531).addOnTalkEvent(questId);
        qe.registerQuestNpc(217382).addOnKillEvent(questId);
        qe.registerQuestNpc(217376).addOnKillEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799530) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 4762);
                    default:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 730390) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case USE_OBJECT:
                        return sendQuestDialog(env, 1007);
                    case STEP_TO_1:
                        TeleportService.teleportTo(player, 300240000, 158.88f, 624.42f, 901f, (byte) 20);
                        return closeDialogWindow(env);
                    default:
                        return sendQuestStartDialog(env);
                }
            } else if (targetId == 700980) {
                return useQuestObject(env, 2, 3, true, true);
            }
        }
        if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799531) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 10002);
                    case SELECT_REWARD:
                        return sendQuestDialog(env, 5);
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 0) {
                return defaultOnKillEvent(env, 217382, 0, 1);
            } else {
                return defaultOnKillEvent(env, 217376, 1, 2);
            }
        }
        return false;

    }
}
