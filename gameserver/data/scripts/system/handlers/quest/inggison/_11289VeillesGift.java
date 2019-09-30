/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.inggison;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author zhkchi
 */
public class _11289VeillesGift extends QuestHandler {

    private final static int questId = 11289;

    public _11289VeillesGift() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799038).addOnTalkEvent(questId);
        qe.registerQuestItem(182213147, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (dialog == QuestDialog.START_DIALOG) {
                return sendQuestDialog(env, 4);
            } else {
                return sendQuestStartDialog(env);
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 799038) {
                if (dialog == QuestDialog.START_DIALOG) {
                    if (checkItemExistence(env, 182213147, 1, true)) {
                        changeQuestStep(env, 0, 0, true);
                        return sendQuestDialog(env, 2375);
                    }
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799038) {
                switch (dialog) {
                    case USE_OBJECT: {
                        return sendQuestDialog(env, 2375);
                    }
                    case SELECT_REWARD: {
                        return sendQuestDialog(env, 5);
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
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        Player player = env.getPlayer();
        int id = item.getItemTemplate().getTemplateId();
        int itemObjId = item.getObjectId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (id != 182213147) {
            return HandlerResult.UNKNOWN;
        }
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 20, 1, 0), true);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            sendQuestDialog(env, 4);
        }
        return HandlerResult.SUCCESS;
    }
}
