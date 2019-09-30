/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.ascension;

import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.model.PlayerClass;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.services.teleport.TeleportService;

/**
 * @author MrPoke
 */
public class _2009ACeremonyinPandaemonium extends QuestHandler {

    private final static int questId = 2009;

    public _2009ACeremonyinPandaemonium() {
        super(questId);
    }

    @Override
    public void register() {
        if (CustomConfig.ENABLE_SIMPLE_2NDCLASS) {
            return;
        }
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(203550).addOnTalkEvent(questId);
        qe.registerQuestNpc(204182).addOnTalkEvent(questId);
        qe.registerQuestNpc(204075).addOnTalkEvent(questId);
        qe.registerQuestNpc(204080).addOnTalkEvent(questId);
        qe.registerQuestNpc(204081).addOnTalkEvent(questId);
        qe.registerQuestNpc(204082).addOnTalkEvent(questId);
        qe.registerQuestNpc(204083).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVars().getQuestVars();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 203550) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            qs.setQuestVar(1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            TeleportService.teleportTo(player, 120010000, 1685f, 1400f, 195f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
                            return true;
                        }
                }
            } else if (targetId == 204182) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 1) {
                            return sendQuestDialog(env, 1352);
                        }
                    case SELECT_ACTION_1353:
                        if (var == 1) {
                            playQuestMovie(env, 121);
                            return false;
                        }
                    case STEP_TO_2:
                        return defaultCloseDialog(env, 1, 2); // 2
                }
            } else if (targetId == 204075) {
                switch (env.getDialog()) {
                    case START_DIALOG:
                        if (var == 2) {
                            return sendQuestDialog(env, 1693);
                        }
                    case SELECT_ACTION_1694:
                        if (var == 2) {
                            playQuestMovie(env, 122);
                            return false;
                        }
                    case STEP_TO_3:
                        if (var == 2) {
                            PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
                            if (playerClass == PlayerClass.WARRIOR) {
                                qs.setQuestVar(10);
                            } else if (playerClass == PlayerClass.SCOUT) {
                                qs.setQuestVar(20);
                            } else if (playerClass == PlayerClass.MAGE) {
                                qs.setQuestVar(30);
                            } else if (playerClass == PlayerClass.PRIEST) {
                                qs.setQuestVar(40);
                            }
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestSelectionDialog(env);
                        }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204080 && var == 10) {
                switch (env.getDialogId()) {
                    case -1:
                        return sendQuestDialog(env, 2034);
                    case 1009:
                        return sendQuestDialog(env, 5);
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 18:
                        if (QuestService.finishQuest(env, 0)) {
                            return sendQuestSelectionDialog(env);
                        }
                }
            } else if (targetId == 204081 && var == 20) {
                switch (env.getDialogId()) {
                    case -1:
                        return sendQuestDialog(env, 2375);
                    case 1009:
                        return sendQuestDialog(env, 6);
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 18:
                        if (QuestService.finishQuest(env, 1)) {
                            return sendQuestSelectionDialog(env);
                        }
                }
            } else if (targetId == 204082 && var == 30) {
                switch (env.getDialogId()) {
                    case -1:
                        return sendQuestDialog(env, 2716);
                    case 1009:
                        return sendQuestDialog(env, 7);
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 18:
                        if (QuestService.finishQuest(env, 2)) {
                            return sendQuestSelectionDialog(env);
                        }
                }
            } else if (targetId == 204083 && var == 40) {
                switch (env.getDialogId()) {
                    case -1:
                        return sendQuestDialog(env, 3057);
                    case 1009:
                        return sendQuestDialog(env, 8);
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 18:
                        if (QuestService.finishQuest(env, 3)) {
                            return sendQuestSelectionDialog(env);
                        }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 2008);
    }
}
