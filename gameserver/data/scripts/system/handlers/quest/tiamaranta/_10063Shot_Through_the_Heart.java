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
 * @author Romanz
 */
public class _10063Shot_Through_the_Heart extends QuestHandler {

    private final static int questId = 10063;

    public _10063Shot_Through_the_Heart() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestNpc(800018).addOnTalkEvent(questId);
        qe.registerQuestNpc(800065).addOnTalkEvent(questId);
        qe.registerQuestNpc(205886).addOnTalkEvent(questId);
        qe.registerQuestNpc(701237).addOnTalkEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("LDF4B_CASTLE_AREA_4041"), questId);

    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {10062};
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

                case 800065:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1693);
                        case STEP_TO_3:
                            return defaultCloseDialog(env, 2, 3);
                    }
                    break;
                case 701237:
                    switch (env.getDialog()) {
                        case USE_OBJECT: {
                            if (var == 3) {
                                if (!giveQuestItem(env, 182212557, 1)) {
                                    return true;
                                }
                            }
                        }
                    }
                    break;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 205886) {
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
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        if (player == null) {
            return false;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (zoneName != ZoneName.get("LDF4B_CASTLE_AREA_4041")) {
            return false;
        }

        if (qs == null) {
            return false;
        }

        if (qs.getQuestVarById(0) == 1) {
            changeQuestStep(env, 1, 2, false);
            return true;
        }
        return false;
    }
}
