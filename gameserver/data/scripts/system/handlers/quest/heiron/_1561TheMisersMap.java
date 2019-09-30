/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

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
 * Find the place marked on the map (182201728).
 *
 * @author Balthazar
 * @reworked vlog
 */
public class _1561TheMisersMap extends QuestHandler {

    private final static int questId = 1561;

    public _1561TheMisersMap() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(700188).addOnTalkEvent(questId);
        qe.registerQuestItem(182201728, questId);
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        env.setQuestId(questId);
        final Player player = env.getPlayer();
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
                    removeQuestItem(env, id, 1);
                    QuestService.startQuest(env);
                    // sendQuestDialog(env, 4);
                }
            }, 3000);
            return HandlerResult.SUCCESS;
        }
        return HandlerResult.UNKNOWN;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
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

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 700188) { // Jewel Box
                if (var == 0) {
                    if (env.getDialog() == QuestDialog.START_DIALOG || env.getDialog() == QuestDialog.USE_OBJECT) {
                        return sendQuestDialog(env, 2375);
                    } else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
                        return defaultCloseDialog(env, 0, 0, true, true);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 700188) { // Jewel Box
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
