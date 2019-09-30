/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.ishalgen;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Rhys2002
 * @modified Hellboy
 */
public class _2136TheLostAxe extends QuestHandler {

    private final static int questId = 2136;
    private final static int[] npc_ids = {700146, 790009};

    public _2136TheLostAxe() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestItem(182203130, questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (env.getDialogId() == 1002) {
                QuestService.startQuest(env);
                player.sendPck(new SM_DIALOG_WINDOW(0, 0));
                return true;
            } else {
                player.sendPck(new SM_DIALOG_WINDOW(0, 0));
            }
        }

        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 790009) {
                final Npc npc = (Npc) env.getVisibleObject();
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        npc.getController().onDelete();
                    }
                }, 10000);
                return sendQuestEndDialog(env);
            }
        } else if (qs.getStatus() != QuestStatus.START) {
            return false;
        }

        if (targetId == 790009) {
            switch (env.getDialog()) {
                case START_DIALOG:
                    if (var == 1) {
                        return sendQuestDialog(env, 1011);
                    }
                case STEP_TO_1:
                    if (var == 1) {
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        removeQuestItem(env, 182203130, 1);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                        return sendQuestDialog(env, 6);
                    }
                case STEP_TO_2:
                    if (var == 1) {
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        removeQuestItem(env, 182203130, 1);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                        return sendQuestDialog(env, 5);
                    }
            }
        } else if (targetId == 700146) {
            switch (env.getDialog()) {
                case USE_OBJECT:
                    if (var == 0) {
                        playQuestMovie(env, 59);
                        qs.setQuestVarById(0, 1);
                        updateQuestStatus(env);
                        QuestService.addNewSpawn(220010000, player.getInstanceId(), 790009, 1088.5f, 2371.8f, 258.375f, (byte) 87);
                        return true;
                    }
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        Player player = env.getPlayer();
        int id = item.getItemTemplate().getTemplateId();
        int itemObjId = item.getObjectId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (id != 182203130) {
            return HandlerResult.UNKNOWN;
        }
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 20, 1, 0), true);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            sendQuestDialog(env, 4);
        }
        return HandlerResult.SUCCESS;
    }
}
