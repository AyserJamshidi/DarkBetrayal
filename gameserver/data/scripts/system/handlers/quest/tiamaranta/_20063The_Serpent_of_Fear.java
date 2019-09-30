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
 * Time: 21:00
 * To change this template use File | Settings | File Templates.
 */
public class _20063The_Serpent_of_Fear extends QuestHandler {

    private final static int questId = 20063;

    public _20063The_Serpent_of_Fear() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestNpc(800018).addOnTalkEvent(questId);
        qe.registerQuestNpc(800069).addOnTalkEvent(questId);
        qe.registerQuestNpc(205886).addOnTalkEvent(questId);
        qe.registerQuestNpc(259614).addOnKillEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("LDF4B_CASTLE_AREA_4041"), questId);

    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {20062};
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
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            } else if (var == 3) {
                                return sendQuestDialog(env, 2034);
                            }
                        }
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1);
                        case CHECK_COLLECTED_ITEMS: {
                            return checkQuestItems(env, 3, 3, true, 10000, 10001); // reward
                        }
                        case FINISH_DIALOG: {
                            return closeDialogWindow(env);
                        }
                    }
                    break;

                case 800069:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1693);
                        case STEP_TO_3:
                            return defaultCloseDialog(env, 2, 3);
                    }
                    break;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205886) {
                switch (env.getDialog()) {
                    case SELECT_REWARD:
                        removeQuestItem(env, 182212560, 1);
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
            case 259614:
                giveQuestItem(env, 182212560, 1);
                if (qs.getQuestVarById(1) < 2) {
                    qs.setStatus(QuestStatus.REWARD);
                    qs.setQuestVar(4);
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
        if (zoneName != ZoneName.get("LDF4B_CASTLE_AREA_4041")) {
            return false;
        }

        if (qs == null || qs.getStatus() != QuestStatus.START && qs.getQuestVarById(0) != 1) {
            return false;
        }
        qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
        updateQuestStatus(env);
        return true;
    }
}
