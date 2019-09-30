/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.verteron;

import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author Balthazar
 */
public class _1162AltenosWeddingRing extends QuestHandler {

    private final static int questId = 1162;

    public _1162AltenosWeddingRing() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203095).addOnQuestStart(questId);
        qe.registerQuestNpc(203095).addOnTalkEvent(questId);
        qe.registerQuestNpc(203093).addOnTalkEvent(questId);
        qe.registerQuestNpc(700005).addOnTalkEvent(questId);
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
            if (targetId == 203095) {
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

        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 700005: {
                    switch (env.getDialog()) {
                        case USE_OBJECT: {
                            if (player.getInventory().getItemCountByItemId(182200563) == 0) {
                                if (!giveQuestItem(env, 182200563, 1)) {
                                    return true;
                                }
                            }
                            qs = player.getQuestStateList().getQuestState(questId);
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            PacketSendUtility.broadcastPacket(player.getTarget(), new SM_EMOTION((Creature) player.getTarget(), EmotionType.DIE, 128, 0)); // wtf?
                            return true;
                        }
                    }
                }
                case 203093:
                case 203095: {
                    if (qs.getQuestVarById(0) == 1) {
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        removeQuestItem(env, 182200563, 1);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    }
                }
                return false;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203095) {
                if (env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
