/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.greater_stigma;

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
 * @author Gigi
 */
public class _30317GroupSpiritsandStigmaSlots extends QuestHandler {

    private final static int questId = 30317;

    public _30317GroupSpiritsandStigmaSlots() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799208).addOnQuestStart(questId);
        qe.registerQuestNpc(799208).addOnTalkEvent(questId);
        qe.registerQuestNpc(799506).addOnTalkEvent(questId);
        qe.registerQuestNpc(799322).addOnTalkEvent(questId);
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
            if (targetId == 799208) {
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
            switch (targetId) {
                case 799322: {
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            return sendQuestDialog(env, 1011);
                        case STEP_TO_1:
                            QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 799506, player.getX(), player.getY(), player.getZ(),
                                player.getHeading());
                            qs.setQuestVarById(0, 1);
                            updateQuestStatus(env);
                            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                            return true;
                    }
                }
                case 799208:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            long itemCount1 = player.getInventory().getItemCountByItemId(182209718);
                            long itemCount2 = player.getInventory().getItemCountByItemId(182209719);
                            if (var == 2) {
                                if (itemCount1 > 0 && itemCount2 > 0) {
                                    removeQuestItem(env, 182209718, 1);
                                    removeQuestItem(env, 182209719, 1);
                                    qs.setStatus(QuestStatus.REWARD);
                                    updateQuestStatus(env);
                                    return sendQuestDialog(env, 1693);
                                } else {
                                    return sendQuestDialog(env, 10001);
                                }
                            }
                    }
                case 799506:
                    switch (env.getDialog()) {
                        case START_DIALOG:
                            if (var == 1) {
                                return sendQuestDialog(env, 1352);
                            }
                        case STEP_TO_2:
                            env.getVisibleObject().getController().delete();
                            qs.setQuestVarById(0, 2);
                            updateQuestStatus(env);
                            return true;
                    }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799208) {
                if (env.getDialogId() == 34) {
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
