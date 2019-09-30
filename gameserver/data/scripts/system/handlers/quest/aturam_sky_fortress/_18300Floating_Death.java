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
import com.ne.gs.world.zone.ZoneName;

/**
 * @author zhkchi
 */
public class _18300Floating_Death extends QuestHandler {

    private final static int questId = 18300;

    public _18300Floating_Death() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799532).addOnQuestStart(questId);
        qe.registerQuestNpc(799532).addOnTalkEvent(questId);
        qe.registerQuestNpc(799531).addOnTalkEvent(questId);
        qe.registerQuestNpc(799530).addOnTalkEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("ATURAM_SKY_FORTRESS_2_300240000"), questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799532) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 4762);
                    default:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
            if (targetId == 799531) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case SELECT_ACTION_1013:
                        return sendQuestDialog(env, 1013);
                    case STEP_TO_1:
                        changeQuestStep(env, 0, 1, false);
                        return closeDialogWindow(env);
                }
            }
        } else if ((qs.getStatus() == QuestStatus.REWARD)) {
            if (targetId == 799530) {
                switch (env.getDialog()) {
                    case USE_OBJECT:
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
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        if (zoneName == ZoneName.get("ATURAM_SKY_FORTRESS_2_300240000")) {
            Player player = env.getPlayer();
            if (player == null) {
                return false;
            }
            QuestState qs = player.getQuestStateList().getQuestState(questId);
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                int var = qs.getQuestVarById(0);
                if (var == 1) {
                    changeQuestStep(env, 1, 1, true);
                    return true;
                }
            }
        }
        return false;
    }
}
