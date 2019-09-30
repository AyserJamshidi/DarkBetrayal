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
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.world.zone.ZoneName;


/**
 * Created with IntelliJ IDEA.
 * User: Alexsis
 * Date: 13.04.13
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */
public class _20062Watching_Waiting extends QuestHandler {

    private final static int questId = 20062;

    public _20062Watching_Waiting() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestNpc(800018).addOnTalkEvent(questId);
        qe.registerQuestNpc(218820).addOnKillEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("LDF4B_CASTLE_AREA_4031"), questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {20061};
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
                case 800018:
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
                    case SELECT_REWARD:
                        return sendQuestEndDialog(env, 10002);
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
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }
        switch (env.getTargetId()) {
            case 218820:
                giveQuestItem(env, 182212592, 1);
                if (qs.getQuestVarById(1) < 2) {

                    qs.setQuestVar(3);
                    updateQuestStatus(env);
                    return true;
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
        if (zoneName != ZoneName.get("LDF4B_CASTLE_AREA_4031")) {
            return false;
        }
        if (qs == null || qs.getStatus() != QuestStatus.START && qs.getQuestVarById(0) != 2) {
            return false;
        }
        removeQuestItem(env, 182212592, 1);
        qs.setStatus(QuestStatus.REWARD);
        qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
        updateQuestStatus(env);
        return true;
    }
}
