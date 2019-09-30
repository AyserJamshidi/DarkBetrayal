/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.pernon;

import java.util.Set;
import com.google.common.collect.ImmutableSet;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.modules.housing.HouseInfo;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi, hex1r0
 */
public class _28830InteriorDecorator extends QuestHandler {

    private static final int QUEST_ID = 28830;
    private static final Set<Integer> BUTLERS = ImmutableSet.of(810022, 810023, 810024, 810025, 810026);

    public _28830InteriorDecorator() {
        super(QUEST_ID);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(830585).addOnQuestStart(QUEST_ID);

        for (Integer butlerId : BUTLERS) {
            qe.registerQuestNpc(butlerId).addOnTalkEvent(QUEST_ID);
        }

        qe.registerQuestNpc(830651).addOnTalkEvent(QUEST_ID);
        qe.registerQuestHouseItem(3425021, QUEST_ID);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestDialog dialog = env.getDialog();
        QuestState qs = player.getQuestStateList().getQuestState(QUEST_ID);
        HouseInfo info = HouseInfo.of(player);

        if (!info.hasHouse()) {
            return false;
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 830585) {
                switch (dialog) {
                    case START_DIALOG:
                        return sendQuestDialog(env, 1011);
                    case ACCEPT_QUEST_SIMPLE:
                        return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START && BUTLERS.contains(targetId) && qs.getQuestVarById(0) == 0) {
            if (!info.managerIs(targetId)) {
                return false;
            }
            switch (dialog) {
                case USE_OBJECT:
                    return sendQuestDialog(env, 1352);
                case STEP_TO_1:
                    return defaultCloseDialog(env, 0, 1);
            }
        } else if (qs.getStatus() == QuestStatus.REWARD && targetId == 830651) {
            switch (dialog) {
                case USE_OBJECT:
                    return sendQuestDialog(env, 2375);
                case SELECT_REWARD:
                    return sendQuestDialog(env, 5);
                case SELECT_NO_REWARD:
                    sendQuestEndDialog(env);
                    return true;
            }
        }

        return false;
    }

    @Override
    public boolean onHouseItemUseEvent(QuestEnv env, int itemId) {
        Player player = env.getPlayer();
        if (itemId == 3425021) {
            QuestState qs = player.getQuestStateList().getQuestState(QUEST_ID);
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
                changeQuestStep(env, 1, 1, true);
            }
        }
        return false;
    }

}
