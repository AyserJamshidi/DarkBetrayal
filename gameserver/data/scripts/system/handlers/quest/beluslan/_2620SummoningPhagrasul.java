/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.beluslan;

import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
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
 * @author Nephis
 */
public class _2620SummoningPhagrasul extends QuestHandler {

    private final static int questId = 2620;
    private final static int[] mob_ids = {213109, 213111};

    public _2620SummoningPhagrasul() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204787).addOnQuestStart(questId); // Chieftain Akagitan
        qe.registerQuestNpc(204787).addOnTalkEvent(questId);
        qe.registerQuestNpc(204824).addOnTalkEvent(questId); // Gigantic Phagrasul
        qe.registerQuestNpc(700323).addOnTalkEvent(questId); // Huge Mamut Skull
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

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        switch (targetId) {
            case 213109:
                if (qs.getQuestVarById(1) < 5 && qs.getQuestVarById(0) == 1) {
                    qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
                    updateQuestStatus(env);
                    return true;
                }
                break;

            case 213111:
                if (qs.getQuestVarById(2) < 5 && qs.getQuestVarById(0) == 1) {
                    qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
                    updateQuestStatus(env);
                    return true;
                }
        }

        return false;
    }

    @Override
    public boolean onDialogEvent(final QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204787) {// Chieftain Akagitan
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else if (dialog == QuestDialog.ACCEPT_QUEST) {
                    if (!giveQuestItem(env, 182204498, 1)) {
                        return true;
                    }
                    return sendQuestStartDialog(env);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 204824) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            final Npc npc = (Npc) env.getVisibleObject();
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    npc.getController().onDelete();
                                }
                            }, 40000);
                            return true;
                        }
                }
            }
            if (targetId == 700323) {// Hugh mamut skull
                switch (dialog) {
                    case USE_OBJECT:
                        if (var == 0) {
                            final int targetObjectId = env.getVisibleObject().getObjectId();
                            player.sendPck(new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
                            PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    @SuppressWarnings("unused") QuestState qs = player.getQuestStateList().getQuestState(questId);
                                    removeQuestItem(env, 182204498, 1);
                                    if (player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId) {
                                        return;
                                    }
                                    player.sendPck(new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
                                    PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId), true);
                                    QuestService.addNewSpawn(220040000, 1, 204824, (float) 2851.698, (float) 160.88698, (float) 301.78537, (byte) 93);
                                }
                            }, 3000);
                        }
                }
            }
            if (targetId == 204787) {// Chieftain Akagitan
                switch (dialog) {
                    case USE_OBJECT:
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 10002);
                    case SELECT_REWARD:
                        return sendQuestDialog(env, 5);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204787) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
