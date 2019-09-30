/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.beluslan;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Ritsu
 */
public class _2533BeritrasCurse extends QuestHandler {

    private final static int questId = 2533;

    public _2533BeritrasCurse() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204801).addOnQuestStart(questId); // Gigrite
        qe.registerQuestNpc(204801).addOnTalkEvent(questId);
        qe.registerQuestItem(182204425, questId);// Empty Durable Potion Bottle
        qe.registerOnQuestTimerEnd(questId);
    }

    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {

        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.isInsideZone(ZoneName.get("BERITRAS_WEAPON_220040000"))) {
                QuestService.questTimerStart(env, 300);
                return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, false, 182204426, 1, 0));
            }
        }
        return HandlerResult.SUCCESS; // ??
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
            if (targetId == 204801) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else if (dialog == QuestDialog.ACCEPT_QUEST) {
                    if (!giveQuestItem(env, 182204425, 1)) {
                        return true;
                    }
                    return sendQuestStartDialog(env);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 204801) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 1) {
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 1352);
                        }
                    case SELECT_REWARD: {
                        QuestService.questTimerEnd(env);
                        return sendQuestDialog(env, 5);
                    }
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204801) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onQuestTimerEndEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            removeQuestItem(env, 182204426, 1);
            QuestService.abandonQuest(player, questId);
            player.getController().updateNearbyQuests();
            return true;
        }
        return false;
    }
}
