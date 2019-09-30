/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.ishalgen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author Mr. Poke
 */
public class _2114TheInsectProblem extends QuestHandler {

    private final static int questId = 2114;

    public _2114TheInsectProblem() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203533).addOnQuestStart(questId);
        qe.registerQuestNpc(203533).addOnTalkEvent(questId);
        qe.registerQuestNpc(210734).addOnKillEvent(questId);
        qe.registerQuestNpc(210380).addOnKillEvent(questId);
        qe.registerQuestNpc(210381).addOnKillEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (targetId == 203533) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case STEP_TO_1:
                        if (QuestService.startQuest(env)) {
                            qs = player.getQuestStateList().getQuestState(questId);
                            qs.setQuestVar(1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    case STEP_TO_2:
                        if (QuestService.startQuest(env)) {
                            qs = player.getQuestStateList().getQuestState(questId);
                            qs.setQuestVar(11);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                }
            } else if (qs.getStatus() == QuestStatus.REWARD) {
                int var = qs.getQuestVarById(0);
                switch (env.getDialog()) {
                    case USE_OBJECT:
                        if (var == 10) {
                            return sendQuestDialog(env, 5);
                        } else if (var == 20) {
                            return sendQuestDialog(env, 6);
                        }
                    case SELECT_NO_REWARD:
                        if (QuestService.finishQuest(env, var / 10 - 1)) {
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
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
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs.getStatus() != QuestStatus.START) {
            return false;
        }
        switch (targetId) {
            case 210734:
                if (var >= 1 && var < 10) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    return true;
                } else if (var == 10) {
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            case 210380:
            case 210381:
                if (var >= 11 && var < 20) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    return true;
                } else if (var == 20) {
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
        }
        return false;
    }
}
