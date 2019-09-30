/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.poeta;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;

/**
 * @author MrPoke
 */
public class _1122DeliveringPernossRobe extends QuestHandler {

    private final static int questId = 1122;

    public _1122DeliveringPernossRobe() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203060).addOnQuestStart(questId);
        qe.registerQuestNpc(203060).addOnTalkEvent(questId);
        qe.registerQuestNpc(790001).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (targetId == 203060) {
            if (qs == null) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialogId() == 1002) {
                    if (giveQuestItem(env, 182200216, 1)) {
                        return sendQuestStartDialog(env);
                    } else {
                        return true;
                    }
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (targetId == 790001) {
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                long itemCount;
                switch (env.getDialog()) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1352);
                    case STEP_TO_1:
                        itemCount = player.getInventory().getItemCountByItemId(182200218);
                        if (itemCount > 0) {
                            qs.setQuestVar(1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            removeQuestItem(env, 182200218, 1);
                            removeQuestItem(env, 182200216, 1);
                            return sendQuestDialog(env, 1523);
                        } else {
                            return sendQuestDialog(env, 1608);
                        }

                    case STEP_TO_2:
                        itemCount = player.getInventory().getItemCountByItemId(182200219);
                        if (itemCount > 0) {
                            qs.setQuestVar(2);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            removeQuestItem(env, 182200219, 1);
                            removeQuestItem(env, 182200216, 1);
                            return sendQuestDialog(env, 1438);
                        } else {
                            return sendQuestDialog(env, 1608);
                        }
                    case STEP_TO_3:
                        itemCount = player.getInventory().getItemCountByItemId(182200220);
                        if (itemCount > 0) {
                            qs.setQuestVar(3);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            removeQuestItem(env, 182200220, 1);
                            removeQuestItem(env, 182200216, 1);
                            return sendQuestDialog(env, 1353);
                        } else {
                            return sendQuestDialog(env, 1608);
                        }
                    default:
                        return sendQuestStartDialog(env);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                if (env.getDialog() == QuestDialog.USE_OBJECT || env.getDialogId() == 1009) {
                    return sendQuestDialog(env, 4 + qs.getQuestVars().getQuestVars());
                } else if (env.getDialogId() == 18) {
                    QuestService.finishQuest(env, qs.getQuestVars().getQuestVars() - 1);
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            }
        }
        return false;
    }
}
