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

/**
 * Created with IntelliJ IDEA.
 * User: Alexsis
 * Date: 14.04.13
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
public class _10061Heaviest_of_Hearts extends QuestHandler {

    private final static int questId = 10061;

    private final static int ADELLA_ID = 205886;
    private final static int GARNON_ID = 800019;
    private final static int GUXOREN_THE_EVASIVE_ID = 218826;
    private final static int SANSTORM_THE_PERSISTENT_ID = 218827;
    private final static int DULSINA_THE_TYPHOON_ID = 218828;

    public _10061Heaviest_of_Hearts() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestNpc(ADELLA_ID).addOnTalkEvent(questId);
        qe.registerQuestNpc(GARNON_ID).addOnTalkEvent(questId);
        qe.registerQuestNpc(GUXOREN_THE_EVASIVE_ID).addOnKillEvent(questId);
        qe.registerQuestNpc(SANSTORM_THE_PERSISTENT_ID).addOnKillEvent(questId);
        qe.registerQuestNpc(DULSINA_THE_TYPHOON_ID).addOnKillEvent(questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {10065};
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
        if (qs == null)
            return false;
        int var = qs.getQuestVarById(0);
        int targetId = env.getTargetId();

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case ADELLA_ID:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1011);
                        case STEP_TO_1:
                            return defaultCloseDialog(env, 0, 1);
                    }
                    break;
                case GARNON_ID:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1693);
                        case STEP_TO_3:
                            qs.setQuestVar(4);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return defaultCloseDialog(env, 2, 3);
                    }
                    break;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == GARNON_ID) {
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
            case GUXOREN_THE_EVASIVE_ID:
                if (qs.getQuestVarById(1) < 1) {
                    qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
                    updateQuestStatus(env);
                    return true;
                }
                break;
            case SANSTORM_THE_PERSISTENT_ID:
                if (qs.getQuestVarById(1) < 2) {
                    qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
                    updateQuestStatus(env);
                    return true;
                }
                break;
            case DULSINA_THE_TYPHOON_ID:
                if (qs.getQuestVarById(2) < 3) {
                    qs.setQuestVar(2);
                    updateQuestStatus(env);
                    return true;
                }
                break;
        }
        return false;
    }

}
