/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.aturam_sky_fortress;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author Luzien
 */
public class _18302FirstPriority extends QuestHandler {

    private final static int questId = 18302;
    private final int[] mobIds = new int[]{700981, 700982, 700983, 700984, 700985};

    public _18302FirstPriority() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799530).addOnQuestStart(questId);
        qe.registerQuestNpc(799530).addOnTalkEvent(questId);
        qe.registerQuestNpc(730375).addOnTalkEvent(questId);
        for (int id : mobIds) {
            qe.registerQuestNpc(id).addOnKillEvent(questId);
        }
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
            if (targetId == 799530) {
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

        int var = qs.getQuestVarById(0);

        if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 730375) {
                if (var == 5) {
                    switch (env.getDialog()) {
                        case USE_OBJECT:
                            return sendQuestDialog(env, 1352);
                        case SET_REWARD:
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return closeDialogWindow(env);
                        default:
                            return sendQuestDialog(env, 2716);
                    }
                }
            } else if (targetId == 799530) {
                return sendQuestDialog(env, 1004);
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799530) {
                if (env.getDialog().equals(QuestDialog.SELECT_REWARD)) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }

        int targetId = 0;
        int var = qs.getQuestVarById(0);

        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        if (var > 4) {
            return false;
        }

        for (int id : mobIds) {
            if (targetId == id) {
                qs.setQuestVarById(0, var + 1);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}
