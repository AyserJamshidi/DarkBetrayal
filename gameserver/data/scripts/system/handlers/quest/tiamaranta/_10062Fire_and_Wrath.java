/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.tiamaranta;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.world.zone.ZoneName;

/**
 * Created with IntelliJ IDEA. User: Alexsis Date: 13.04.13 Time: 18:16 To
 * change this template use File | Settings | File Templates.
 */
public class _10062Fire_and_Wrath extends QuestHandler {

    private final static int questId = 10062;

    public _10062Fire_and_Wrath() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLogOut(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(205886).addOnTalkEvent(questId);
        qe.registerQuestNpc(800018).addOnTalkEvent(questId);
        qe.registerQuestNpc(218766).addOnKillEvent(questId);
        qe.registerGetingItem(182212556, questId);
        qe.registerOnEnterZone(ZoneName.get("WRATH_VENT_600030000"), questId);

    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {10061};
        return defaultOnLvlUpEvent(env, quests, true);
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            return QuestService.startQuest(env);
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 205886:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1011);
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1);
                    }
                    break;

            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 800018) {
                switch (env.getDialog()) {
                    case USE_OBJECT: {
                        if (var == 3) {
                            return sendQuestDialog(env, 10002);
                        }
                    }
                    default: {
                        removeQuestItem(env, 182212556, 1);
                        return sendQuestEndDialog(env);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }
        switch (env.getTargetId()) {
            case 218766:
                if (qs == null || qs.getQuestVars().getQuestVars() == 2) {
                    Npc npc = (Npc) env.getVisibleObject();
                    npc.getController().onDelete();
                    return defaultOnKillEvent(env, 218766, 2, 3);
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        if (player == null) {
            return false;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (zoneName != ZoneName.get("WRATH_VENT_600030000")) {
            return false;
        }
        if (qs == null || qs.getStatus() != QuestStatus.START && qs.getQuestVarById(0) != 1) {
            return false;
        }
        qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
        updateQuestStatus(env);
        return true;
    }

    @Override
    public boolean onLogOutEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 3 && player.getInventory().getItemCountByItemId(182212556) >= 1) {
                changeQuestStep(env, 3, 3, true); // reward
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onGetItemEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (var == 3) {
                changeQuestStep(env, 3, 3, true); // reward
                return true;
            }
        }
        return false;
    }
}
