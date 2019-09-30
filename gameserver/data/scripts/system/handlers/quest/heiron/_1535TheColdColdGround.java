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
 * @author Rolandas
 */
public class _1535TheColdColdGround extends QuestHandler {

    private final static int questId = 1535;

    public _1535TheColdColdGround() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204580).addOnQuestStart(questId);
        qe.registerQuestNpc(204580).addOnTalkEvent(questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (targetId != 204580) {
            return false;
        }

        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (env.getDialog() == QuestDialog.START_DIALOG) {
                return sendQuestDialog(env, 4762);
            } else {
                return sendQuestStartDialog(env);
            }
        }

        if (qs.getStatus() == QuestStatus.START) {
            boolean abexSkins = player.getInventory().getItemCountByItemId(182201818) > 4;
            boolean worgSkins = player.getInventory().getItemCountByItemId(182201819) > 2;
            boolean karnifSkins = player.getInventory().getItemCountByItemId(182201820) > 0;

            switch (env.getDialog()) {
                case USE_OBJECT:
                case START_DIALOG:
                    if (abexSkins || worgSkins || karnifSkins) {
                        return sendQuestDialog(env, 1352);
                    }
                case STEP_TO_1:
                    if (abexSkins) {
                        qs.setQuestVarById(0, 1);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 5);
                    }
                    break;
                case STEP_TO_2:
                    if (worgSkins) {
                        qs.setQuestVarById(0, 2);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 6);
                    }
                    break;
                case STEP_TO_3:
                    if (karnifSkins) {
                        qs.setQuestVarById(0, 3);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 7);
                    }
                    break;
            }
            return sendQuestDialog(env, 1693);
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            int var = qs.getQuestVarById(0);
            if (var == 1) {
                removeQuestItem(env, 182201818, 5);
                return sendQuestEndDialog(env);
            } else if (var == 2) {
                // add Greater Mana Potion x 5
                if (!giveQuestItem(env, 162000010, 5)) {
                    // check later
                    qs.setStatus(QuestStatus.START);
                    updateQuestStatus(env);
                } else {
                    removeQuestItem(env, 182201819, 3);
                }
                sendQuestEndDialog(env);
                return true;
            } else if (var == 3) {
                // add Greater Life Serum x 5
                if (!giveQuestItem(env, 162000015, 5)) {
                    // check later
                    qs.setStatus(QuestStatus.START);
                    updateQuestStatus(env);
                } else {
                    removeQuestItem(env, 182201820, 1);
                }
                sendQuestEndDialog(env);
                return true;
            }
            player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
        }
        return false;
    }
}
