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
 * @author Rolandas, Bobobear, hex1r0
 */
public class _28821YourButlerGift extends QuestHandler {

    private static final int QUEST_ID = 28821;
    private static final Set<Integer> BUTLERS = ImmutableSet.of(810022, 810023, 810024, 810025, 810026);

    public _28821YourButlerGift() {
        super(QUEST_ID);
    }

    @Override
    public void register() {
        for (Integer butlerId : BUTLERS) {
            qe.registerQuestNpc(butlerId).addOnQuestStart(QUEST_ID);
            qe.registerQuestNpc(butlerId).addOnTalkEvent(QUEST_ID);
        }
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();

        if (!BUTLERS.contains(targetId)) {
            return false;
        }
        if (!HouseInfo.of(player).managerIs(targetId)) {
            return false;
        }

        QuestDialog dialog = env.getDialog();
        QuestState qs = player.getQuestStateList().getQuestState(QUEST_ID);

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            switch (dialog) {
                case START_DIALOG:
                    return sendQuestDialog(env, 1011);
                case ACCEPT_QUEST:
                    return sendQuestStartDialog(env);
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            switch (dialog) {
                case START_DIALOG:
                    return sendQuestDialog(env, 2375);
                case SELECT_REWARD:
                    changeQuestStep(env, 0, 0, true);
                    return sendQuestDialog(env, 5);
                case SELECT_NO_REWARD:
                    sendQuestEndDialog(env);
                    return true;
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            switch (dialog) {
                case USE_OBJECT:
                    return sendQuestDialog(env, 5);
                case SELECT_NO_REWARD:
                    return sendQuestEndDialog(env);
            }
        }

        return false;
    }

}
