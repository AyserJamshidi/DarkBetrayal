/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.altgard;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Ritsu
 */
public class _2247TheGergersDisguise extends QuestHandler {

    private final static int questId = 2247;

    public _2247TheGergersDisguise()

    {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203645).addOnQuestStart(questId);
        qe.registerQuestNpc(203645).addOnTalkEvent(questId);
        qe.registerQuestNpc(798039).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 203645) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 798039) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1352);
                    case STEP_TO_1:
                        if (var == 0) {
                            qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                            updateQuestStatus(env);
                            if (!giveQuestItem(env, 182203231, 1)) {
                                return true;
                            }
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                        }
                }
            }
            if (targetId == 203645) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 1) {
                            qs.setQuestVar(2);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 2375);
                        }
                    case SELECT_REWARD:
                        if (var == 2) {
                            removeQuestItem(env, 182203231, 1);
                            return sendQuestEndDialog(env);
                        }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203645) {
                removeQuestItem(env, 182203231, 1);
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
