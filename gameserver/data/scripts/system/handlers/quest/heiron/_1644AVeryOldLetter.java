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
 * @author Balthazar
 * @reworked & modified Gigi
 */
public class _1644AVeryOldLetter extends QuestHandler {

    private final static int questId = 1644;

    public _1644AVeryOldLetter() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204545).addOnTalkEvent(questId);
        qe.registerQuestNpc(204537).addOnTalkEvent(questId);
        qe.registerQuestNpc(204546).addOnTalkEvent(questId);
        qe.registerQuestItem(182201765, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (targetId == 0) {
            if (env.getDialogId() == 1002) {
                QuestService.startQuest(env);
                player.sendPck(new SM_DIALOG_WINDOW(0, 0));
                return true;
            }
        }

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 204545: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            if (qs.getQuestVarById(0) == 0) {
                                return sendQuestDialog(env, 1352);
                            } else if (qs.getQuestVarById(0) == 2) {
                                return sendQuestDialog(env, 2034);
                            }
                        }
                        case STEP_TO_1: {
                            return defaultCloseDialog(env, 0, 1);
                        }
                        case STEP_TO_3: {
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return defaultCloseDialog(env, 2, 3);
                        }
                    }
                }
                case 204537: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1693);
                        }
                        case STEP_TO_2: {
                            return defaultCloseDialog(env, 1, 2, 0, 0, 182201765, 1);
                        }
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204546) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 2375);
                    }
                    default: {
                        return sendQuestEndDialog(env);
                    }
                }
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final int itemObjId = item.getObjectId();
        final int id = item.getItemTemplate().getTemplateId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (id != 182201765) {
            return HandlerResult.UNKNOWN;
        }

        if (qs != null) {
            if (qs.getStatus() == QuestStatus.COMPLETE) {
                removeQuestItem(env, 182201765, 1);
                return HandlerResult.FAILED;
            }
        }

        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0, 0), true);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0, 1, 0), true);
                sendQuestDialog(env, 4);
            }
        }, 3000);
        return HandlerResult.SUCCESS;
    }
}
