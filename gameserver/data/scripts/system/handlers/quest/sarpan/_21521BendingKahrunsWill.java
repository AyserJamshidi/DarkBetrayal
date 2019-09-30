/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sarpan;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author zhkchi
 */
public class _21521BendingKahrunsWill extends QuestHandler {

    private static final int questId = 21521;

    public _21521BendingKahrunsWill() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799266).addOnQuestStart(questId);
        qe.registerQuestNpc(799266).addOnTalkEvent(questId);
        qe.registerOnKillInWorld(600020000, questId);
    }

    @Override
    public boolean onKillInWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        if (env.getVisibleObject() instanceof Player && player != null && player.isInsideZone(ZoneName.get("HEROS_DISCUS_600020000"))) {
            if ((env.getPlayer().getLevel() >= (((Player) env.getVisibleObject()).getLevel() - 5))
                && (env.getPlayer().getLevel() <= (((Player) env.getVisibleObject()).getLevel() + 9))) {
                return defaultOnKillRankedEvent(env, 0, 5, true); // reward
            }
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 799266) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 4762);
                    default:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799266) {
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
}
