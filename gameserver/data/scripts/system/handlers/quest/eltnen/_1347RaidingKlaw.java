/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.eltnen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Rhys2002
 */
public class _1347RaidingKlaw extends QuestHandler {

    private final static int questId = 1347;
    private final static int[] npc_ids = {203965, 203966};
    private final static int[] mob_ids = {210908, 210874, 212137, 212056, 210917};

    public _1347RaidingKlaw() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203965).addOnQuestStart(questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
        for (int mob_id : mob_ids) {
            qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (targetId == 203965) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203966) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return sendQuestDialog(env, 1352);
                }
            }
            return false;
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203966) {
                if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        int[] mobs1 = {210908, 210874};
        int[] mobs2 = {212137, 212056, 210917};
        if (defaultOnKillEvent(env, mobs1, 0, 15, 0) || defaultOnKillEvent(env, mobs2, 0, 7, 1)) {
            return true;
        } else {
            return false;
        }
    }
}
