/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.heiron;

import com.ne.gs.model.gameobjects.Item;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.HandlerResult;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Ritsu
 */
public class _1573SomeTastyMushrooms extends QuestHandler {

    private final static int questId = 1573;

    public _1573SomeTastyMushrooms() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestItem(182201784, questId);
        qe.registerQuestNpc(730025).addOnQuestStart(questId);
        qe.registerQuestNpc(730025).addOnTalkEvent(questId);
        qe.registerQuestNpc(700194).addOnTalkEvent(questId);
    }

    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.isInsideZone(ZoneName.get("LF3_ITEMUSEAREA_Q1573"))) {
                removeQuestItem(env, 182201784, 1);
                return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false, 182201735, 1, 0));
            }
        }
        return HandlerResult.SUCCESS;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 730025) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 700194) {
                return true;
            }
            if (targetId == 730025) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1011);
                        }
                        if (var == 2) {
                            removeQuestItem(env, 182201735, 1);
                            qs.setStatus(QuestStatus.REWARD);
                            updateQuestStatus(env);
                            return sendQuestDialog(env, 10002);
                        }
                    case CHECK_COLLECTED_ITEMS:
                        return checkQuestItems(env, 0, 1, false, 0, 10001, 182201784, 1);
                    case SELECT_REWARD:
                        return sendQuestEndDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 730025) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
