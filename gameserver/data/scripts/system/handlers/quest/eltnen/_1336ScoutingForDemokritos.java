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
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Ritsu
 */
public class _1336ScoutingForDemokritos extends QuestHandler {

    private final static int questId = 1336;

    public _1336ScoutingForDemokritos() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204006).addOnQuestStart(questId); // Demokritos
        qe.registerQuestNpc(204006).addOnTalkEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("EASTERN_ERACUS_DESERT_210020000"), questId);
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        if (zoneName != ZoneName.get("EASTERN_ERACUS_DESERT_210020000")) {
            return false;
        }
        Player player = env.getPlayer();
        if (player == null) {
            return false;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getQuestVars().getQuestVars() != 0) {
            return false;
        }
        if (qs.getStatus() != QuestStatus.START) {
            return false;
        }
        env.setQuestId(questId);
        qs.setStatus(QuestStatus.REWARD);
        updateQuestStatus(env);
        return true;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204006) {// Demokritos
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204006) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
