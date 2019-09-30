/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.esoterrace;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _18410PursuingthePrisoners extends QuestHandler {

    private final static int questId = 18410;

    public _18410PursuingthePrisoners() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799584).addOnQuestStart(questId);
        qe.registerQuestNpc(799553).addOnQuestStart(questId);
        qe.registerQuestNpc(799563).addOnTalkEvent(questId);
        qe.registerQuestNpc(799553).addOnTalkEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("DRANA_PRODUCTION_LAB_300250000"), questId);
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        Player player = env.getPlayer();
        if (player == null) {
            return false;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (zoneName != ZoneName.get("DRANA_PRODUCTION_LAB_300250000")) {
            return false;
        }
        if (qs == null || qs.getQuestVars().getQuestVars() != 1) {
            return false;
        }
        if (qs.getStatus() != QuestStatus.START) {
            return false;
        }
        qs.setStatus(QuestStatus.REWARD);
        updateQuestStatus(env);
        return true;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 799553 || targetId == 799584) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 799563) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (env.getDialog() == QuestDialog.STEP_TO_1) {
                    return defaultCloseDialog(env, 0, 1);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799553) {
                switch (dialog) {
                    case START_DIALOG: {
                        return sendQuestDialog(env, 10002);
                    }
                    default: {
                        return sendQuestEndDialog(env);
                    }
                }
            }
        }
        return false;
    }
}
