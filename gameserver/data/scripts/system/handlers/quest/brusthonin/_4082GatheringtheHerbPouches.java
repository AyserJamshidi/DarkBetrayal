/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.brusthonin;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.ne.gs.questEngine.handlers.HandlerResult;
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
public class _4082GatheringtheHerbPouches extends QuestHandler {

    private final static int questId = 4082;

    public _4082GatheringtheHerbPouches() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(205190).addOnQuestStart(questId);
        qe.registerQuestNpc(205190).addOnTalkEvent(questId);
        qe.registerQuestNpc(700430).addOnTalkEvent(questId);
        qe.registerQuestNpc(700431).addOnTalkEvent(questId);
        qe.registerQuestNpc(700432).addOnTalkEvent(questId);
        qe.registerQuestItem(182209058, questId);
    }

    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();

        if (id != 182209058 || qs == null) {
            return HandlerResult.UNKNOWN;
        }

        if (qs.getQuestVarById(0) != 0) {
            return HandlerResult.FAILED;
        }

        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
            }
        }, 3000);
        return HandlerResult.SUCCESS;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (targetId == 205190) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialogId() == 1002) {
                    if (giveQuestItem(env, 182209058, 1)) {
                        return sendQuestStartDialog(env);
                    }
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }

            if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 2375);
                } else if (env.getDialogId() == 34) {
                    if (QuestService.collectItemCheck(env, true)) {
                        removeQuestItem(env, 182209058, 1);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    } else {
                        return sendQuestDialog(env, 2716);
                    }
                } else {
                    return sendQuestEndDialog(env);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                return sendQuestEndDialog(env);
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 700430:
                case 700431:
                case 700432: {
                    if (qs.getQuestVarById(0) == 0 && env.getDialog() == QuestDialog.USE_OBJECT) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
