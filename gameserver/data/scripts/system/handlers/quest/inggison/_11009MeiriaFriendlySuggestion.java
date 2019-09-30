/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.inggison;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author dta3000
 */

public class _11009MeiriaFriendlySuggestion extends QuestHandler {

    private final static int questId = 11009;

    public _11009MeiriaFriendlySuggestion() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(798945).addOnQuestStart(questId);
        qe.registerQuestNpc(798945).addOnTalkEvent(questId);
        qe.registerQuestNpc(799008).addOnTalkEvent(questId);
        qe.registerQuestNpc(799017).addOnTalkEvent(questId);
        qe.registerQuestNpc(798941).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (targetId == 798945) {
            if (qs == null) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialogId() == 1002) {
                    if (giveQuestItem(env, 182206711, 2)) {
                        return sendQuestStartDialog(env);
                    } else {
                        return true;
                    }
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
                case 799008: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1352);
                        }
                        case STEP_TO_1: {
                            removeQuestItem(env, 182206711, 1);
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                }
                case 799017: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 1693);
                        }
                        case STEP_TO_2: {
                            removeQuestItem(env, 182206711, 1);
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            if (giveQuestItem(env, 182206712, 1)) {
                                return true;
                            }
                        }
                    }
                }
                case 798941: {
                    switch (env.getDialog()) {
                        case START_DIALOG: {
                            return sendQuestDialog(env, 2034);
                        }
                        case STEP_TO_3: {
                            removeQuestItem(env, 182206712, 1);
                            qs.setQuestVar(3);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798945) {
                if (env.getDialog() == QuestDialog.USE_OBJECT) {
                    return sendQuestDialog(env, 2375);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
