/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.event_quests;

import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.EventService;
import com.ne.gs.services.QuestService;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Rolandas
 */

public class _80033EventAvertingTheGaze extends QuestHandler {

    private final static int questId = 80033;

    public _80033EventAvertingTheGaze() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799781).addOnQuestStart(questId);
        qe.registerQuestNpc(799781).addOnTalkEvent(questId);
        qe.registerQuestItem(188051133, questId); // [Event] Charm Card
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            return false;
        }

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 799781) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialog() == QuestDialog.ACCEPT_QUEST) {
                    Storage storage = player.getInventory();
                    if (storage.getItemCountByItemId(164002015) > 0) {
                        return sendQuestDialog(env, 2375);
                    } else {
                        return sendQuestDialog(env, 2716);
                    }
                } else if (env.getDialog() == QuestDialog.SELECT_REWARD) {
                    if (qs.getQuestVarById(0) == 0) {
                        defaultCloseDialog(env, 0, 1, true, true, 0, 0, 164002015, 1);
                    }
                    return sendQuestDialog(env, 5);
                } else if (env.getDialog() == QuestDialog.SELECT_NO_REWARD) {
                    return sendQuestRewardDialog(env, 799781, 5);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        return sendQuestRewardDialog(env, 799781, 0);
    }

    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        // check if the parent quest is active (you get Charm Cards)
        if (!EventService.getInstance().checkQuestIsActive(80032)) {
            return HandlerResult.FAILED;
        }

        final Player player = env.getPlayer();

        // the same item registered for elyos quests, return UNKNOWN for them
        // to not start asmodians quests
        if (item.getItemId() == 188051133 && player.getCommonData().getRace().equals(Race.ASMODIANS)) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    Storage storage = player.getInventory();
                    QuestStatus status = QuestStatus.NONE;

                    if (storage.getItemCountByItemId(164002015) > 0) {
                        status = getQuestUpdateStatus(player, questId);
                        // got a Beritra's Gaze, then start me
                        QuestService.startEventQuest(new QuestEnv(null, player, questId, 0), status);
                    }
                    if (storage.getItemCountByItemId(164002016) > 9) // Israphel's Glory
                    {
                        status = getQuestUpdateStatus(player, 80037);
                        QuestService.startEventQuest(new QuestEnv(null, player, 80037, 0), status);
                    }
                    if (storage.getItemCountByItemId(164002017) > 4) // Siel's Gift
                    {
                        status = getQuestUpdateStatus(player, 80038);
                        QuestService.startEventQuest(new QuestEnv(null, player, 80038, 0), status);
                    }
                    if (storage.getItemCountByItemId(164002018) > 0) // Aion's Grace
                    {
                        status = getQuestUpdateStatus(player, 80039);
                        QuestService.startEventQuest(new QuestEnv(null, player, 80039, 0), status);
                    }
                }
            }, 10000);
            return HandlerResult.SUCCESS;
        }
        return HandlerResult.UNKNOWN;
    }

    final QuestStatus getQuestUpdateStatus(Player player, int questid) {
        QuestState qs = player.getQuestStateList().getQuestState(questid);
        QuestStatus status = qs == null ? QuestStatus.START : qs.getStatus();

        if (qs != null && questid != questId && status == QuestStatus.COMPLETE) {
            status = QuestStatus.START;
        }
        return status;
    }

}
