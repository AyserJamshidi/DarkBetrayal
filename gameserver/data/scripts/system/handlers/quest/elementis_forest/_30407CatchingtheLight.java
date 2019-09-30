/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.elementis_forest;

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
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Ritsu
 */
public class _30407CatchingtheLight extends QuestHandler {

    private static final int questId = 30407;

    public _30407CatchingtheLight() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799551).addOnQuestStart(questId);
        qe.registerQuestNpc(799551).addOnTalkEvent(questId);
        qe.registerQuestNpc(205575).addOnTalkEvent(questId);
        qe.registerQuestItem(182213014, questId);
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();
        final Npc npc = (Npc) player.getTarget();
        if (((Npc) player.getTarget()).getNpcId() != 217262) {
            return HandlerResult.UNKNOWN;
        }
        if (!MathUtil.isIn3dRange(player, npc, 12.5f)) {
            return HandlerResult.UNKNOWN;
        }
        if (id != 182213014) {
            return HandlerResult.UNKNOWN;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return HandlerResult.FAILED;
        }
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);

                npc.getController().scheduleRespawn();
                npc.getController().onDelete();
                player.getInventory().decreaseByObjectId(itemObjId, 1);
                giveQuestItem(env, 182213015, 1);
            }
        }, 3000);
        return HandlerResult.SUCCESS;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 799551) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                }
                if (dialog == QuestDialog.ACCEPT_QUEST_SIMPLE) {
                    if (giveQuestItem(env, 182213014, 1)) {
                        return sendQuestStartDialog(env);
                    } else {
                        return true;
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            switch (targetId) {
                case 799551: {
                    switch (dialog) {
                        case START_DIALOG: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case CHECK_COLLECTED_ITEMS_SIMPLE: {
                            if (QuestService.collectItemCheck(env, true)) {
                                changeQuestStep(env, 0, 0, true);
                                return sendQuestDialog(env, 10002);
                            } else {
                                return closeDialogWindow(env);
                            }
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799551) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
