/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.daevation;

import com.ne.gs.model.PlayerClass;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Rhys2002
 */
public class _2989CeremonyOfTheWise extends QuestHandler {

    private final static int questId = 2989;

    public _2989CeremonyOfTheWise() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204056).addOnQuestStart(questId);
        qe.registerQuestNpc(204057).addOnQuestStart(questId);
        qe.registerQuestNpc(204058).addOnQuestStart(questId);
        qe.registerQuestNpc(204059).addOnQuestStart(questId);
        qe.registerQuestNpc(204146).addOnQuestStart(questId);
        qe.registerQuestNpc(204146).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204146) {
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

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.START) {
            PlayerClass playerClass = player.getCommonData().getPlayerClass();
            switch (targetId) {
                case 204056:// Traufnir
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR) {
                                return sendQuestDialog(env, 1352);
                            } else {
                                return sendQuestDialog(env, 1438);
                            }
                        case STEP_TO_1:
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                    }
                case 204057:// Sigyn
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER) {
                                return sendQuestDialog(env, 1693);
                            } else {
                                return sendQuestDialog(env, 1779);
                            }
                        case STEP_TO_1:
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                    }
                case 204058:// Sif
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (playerClass == PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER) {
                                return sendQuestDialog(env, 2034);
                            } else {
                                return sendQuestDialog(env, 2120);
                            }
                        case STEP_TO_1:
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                    }
                case 204059:// Freyr
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (playerClass == PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER) {
                                return sendQuestDialog(env, 2375);
                            } else {
                                return sendQuestDialog(env, 2461);
                            }
                        case STEP_TO_1:
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                    }
                case 204146:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 1) {
                                return sendQuestDialog(env, 2716);
                            } else if (var == 2) {
                                return sendQuestDialog(env, 3057);
                            } else if (var == 3) {
                                if (player.getLifeStats().getCurrentDp() < 4000) {
                                    return sendQuestDialog(env, 3484);
                                } else {
                                    return sendQuestDialog(env, 3398);
                                }
                            } else if (var == 4) {
                                if (player.getLifeStats().getCurrentDp() < 4000) {
                                    return sendQuestDialog(env, 3825);
                                } else {
                                    return sendQuestDialog(env, 3739);
                                }
                            }
                        case SELECT_REWARD:
                            if (var == 3) {
                                playQuestMovie(env, 137);
                                player.getLifeStats().setCurrentDp(0);
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                return sendQuestDialog(env, 5);
                            } else if (var == 4) {
                                playQuestMovie(env, 137);
                                player.getLifeStats().setCurrentDp(0);
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                return sendQuestDialog(env, 5);
                            } else {
                                return this.sendQuestEndDialog(env);
                            }
                        case STEP_TO_2:
                            qs.setQuestVarById(0, var + 1);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 3057);
                        case STEP_TO_4:
                            qs.setQuestVarById(0, 3);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        case STEP_TO_5:
                            qs.setQuestVarById(0, 4);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                    }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204146) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}