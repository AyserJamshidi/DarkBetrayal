/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.altgard;

import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_USE_OBJECT;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Ritsu
 */
public class _2252ChasingtheLegend extends QuestHandler {

    private final static int questId = 2252;
    private final static int[] mob_ids = {210634}; // Minusha's Spirit

    public _2252ChasingtheLegend() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203646).addOnQuestStart(questId); // Sinood
        qe.registerQuestNpc(203646).addOnTalkEvent(questId);
        qe.registerQuestNpc(700060).addOnTalkEvent(questId); // Bone of Minusha
        for (int mob_id : mob_ids) {
            qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
        }
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }
        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        switch (targetId) {
            case 210634: // Minusha's Spirit
                if (var == 0) {
                    qs.setQuestVarById(0, var + 1);
                    updateQuestStatus(env);
                    return true;
                }
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = 0;
        @SuppressWarnings("unused")
        Npc npc = null;
        if (env.getVisibleObject() instanceof Npc) {
            npc = (Npc) env.getVisibleObject();
        }
        targetId = ((Npc) env.getVisibleObject()).getNpcId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203646) {// Sinood
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 203646) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 1) {
                            qs.setQuestVar(2);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 1352);
                        }
                    case SELECT_REWARD:
                        if (var == 2) {
                            return sendQuestEndDialog(env);
                        }
                }
            }
            if (targetId == 700060) {
                switch (dialog) {
                    case USE_OBJECT:
                        if (var == 0) {
                            final int targetObjectId = env.getVisibleObject().getObjectId();
                            player.sendPck(new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
                            PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    Npc npc = (Npc) player.getTarget();
                                    if (npc == null || npc.getObjectId() != targetObjectId) {
                                        return;
                                    }
                                    QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 210634, npc.getX(), npc.getY(), npc.getZ(),
                                        npc.getHeading()); // Minusha's Spirit
                                    if (player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId) {
                                        return;
                                    }
                                    player.sendPck(new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
                                    PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
                                    ((Npc) player.getTarget()).getController().onDie(null);
                                }
                            }, 3000);
                        }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            return sendQuestEndDialog(env);
        }
        return false;
    }
}
