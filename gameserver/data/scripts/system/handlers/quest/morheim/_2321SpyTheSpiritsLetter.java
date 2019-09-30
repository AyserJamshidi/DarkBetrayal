/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.morheim;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
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
 * @author MrPoke remod by Nephis
 */
public class _2321SpyTheSpiritsLetter extends QuestHandler {

    private final static int questId = 2321;

    public _2321SpyTheSpiritsLetter() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204225).addOnTalkEvent(questId);
        qe.registerQuestNpc(790018).addOnTalkEvent(questId);
        qe.registerQuestItem(182204242, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (env.getDialogId() == 1002) {
                QuestService.startQuest(env);
                player.sendPck(new SM_DIALOG_WINDOW(0, 0));
                return true;
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 204225) {
                if (qs != null) {
                    if (env.getDialog() == QuestDialog.START_DIALOG) {
                        return sendQuestDialog(env, 1352);
                    } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                        qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                        updateQuestStatus(env);
                        removeQuestItem(env, 182204242, 1);
                        player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                    } else {
                        return sendQuestStartDialog(env);
                    }
                }
            } else if (targetId == 790018) {
                if (qs != null) {
                    if (env.getDialog() == QuestDialog.START_DIALOG && qs.getStatus() == QuestStatus.START) {
                        return sendQuestDialog(env, 2375);
                    } else if (env.getDialogId() == 1009) {
                        qs.setQuestVar(1);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestEndDialog(env);
                    } else {
                        return sendQuestEndDialog(env);
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD && targetId == 790018) {
            return sendQuestEndDialog(env);
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (id != 182204242) {
            return HandlerResult.UNKNOWN;
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
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
        return HandlerResult.UNKNOWN;
    }
}