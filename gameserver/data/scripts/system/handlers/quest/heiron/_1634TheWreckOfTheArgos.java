/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Balthazar
 */

public class _1634TheWreckOfTheArgos extends QuestHandler {

    private final static int questId = 1634;

    public _1634TheWreckOfTheArgos() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204547).addOnQuestStart(questId);
        qe.registerQuestNpc(204547).addOnTalkEvent(questId);
        qe.registerQuestNpc(204540).addOnTalkEvent(questId);
        qe.registerQuestNpc(790018).addOnTalkEvent(questId);
        qe.registerQuestNpc(204541).addOnTalkEvent(questId);
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
            if (targetId == 204547) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
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
                case 204547: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            long itemCount1 = player.getInventory().getItemCountByItemId(182201760);
                            if (qs.getQuestVarById(0) == 0 && itemCount1 >= 3) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case SELECT_ACTION_4763: {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            return true;
                        }
                    }
                }
                case 204540: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1693);
                        }
                        case SELECT_ACTION_1694: {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            removeQuestItem(env, 182201760, 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            return true;
                        }
                    }
                }
                case 790018: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 2034);
                        }
                        case SELECT_ACTION_2035: {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            removeQuestItem(env, 182201760, 1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                            return true;
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204541) {
                switch (env.getDialog()) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 2375);
                    }
                    case SELECT_REWARD: {
                        return sendQuestDialog(env, 5);
                    }
                    case SELECT_NO_REWARD: {
                        removeQuestItem(env, 182201760, 1);
                    }
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
