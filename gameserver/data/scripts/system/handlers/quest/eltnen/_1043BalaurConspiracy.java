/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.eltnen;

import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.instance.InstanceService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldMapType;

/**
 * @author Balthazar
 */

public class _1043BalaurConspiracy extends QuestHandler {

    private final static int questId = 1043;

    public _1043BalaurConspiracy() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestNpc(203901).addOnTalkEvent(questId);
        qe.registerQuestNpc(204020).addOnTalkEvent(questId);
        qe.registerQuestNpc(204044).addOnTalkEvent(questId);
        qe.registerQuestNpc(211629).addOnKillEvent(questId);
        qe.registerQuestNpc(700141).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 203901: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            if (qs.getQuestVarById(0) == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case STEP_TO_1: {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                }
                case 204020: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            if (qs.getQuestVarById(0) == 1) {
                                return sendQuestDialog(env, 1352);
                            }
                        }
                        case STEP_TO_2: {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                }
                case 700141:
                    if (qs.getQuestVarById(0) == 2) {
                        int targetObjectId = env.getVisibleObject().getObjectId();
                        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
                        ThreadPoolManager.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310040000);
                                InstanceService.registerPlayerWithInstance(newInstance, player);
                                TeleportService.teleportTo(player, 310040000, newInstance.getInstanceId(), (float) 270.5, (float) 174.3, (float) 204.3);
                            }
                        }, 3000);
                        return true;
                    }
                case 204044: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            switch (qs.getQuestVarById(0)) {
                                case 2: {
                                    return sendQuestDialog(env, 1693);
                                }
                                case 4: {
                                    return sendQuestDialog(env, 2034);
                                }
                            }
                        }
                        case STEP_TO_3: {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 2);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                        case STEP_TO_4: {
                            qs.setQuestVar(4);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            TeleportService.teleportTo(player, WorldMapType.ELTNEN.getId(), 2502.1948f, 782.9152f, 408.97723f);
                            return true;
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203901) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 2375);
                    }
                    case SELECT_REWARD: {
                        return sendQuestDialog(env, 5);
                    }
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        int[] quests = {1300, 1031, 1032, 1033, 1034, 1036, 1037, 1035, 1038, 1039, 1040, 1041, 1042};
        return defaultOnZoneMissionEndEvent(env, quests);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        int[] quests = {1300, 1031, 1032, 1033, 1034, 1036, 1037, 1035, 1038, 1039, 1040, 1041, 1042};
        return defaultOnLvlUpEvent(env, quests, true);
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVarById(0) != 3) {
            return false;
        }

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (targetId == 211629) {
            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
            updateQuestStatus(env);
            return true;
        }
        return false;
    }
}
