/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.silentera_canyon;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;

/**
 * @author zhkchi
 */
public class _30051BanishingtheShadowborn extends QuestHandler {

    private final static int questId = 30051;

    public _30051BanishingtheShadowborn() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799381).addOnQuestStart(questId);
        qe.registerQuestNpc(799381).addOnTalkEvent(questId);
        qe.registerOnKillInWorld(600010000, questId);
    }

    @Override
    public boolean onKillInWorldEvent(QuestEnv env) {
        if (env.getVisibleObject() instanceof Player) {
            Player killed = ((Player) env.getVisibleObject());
            if ((killed.getLevel() + 9) >= env.getPlayer().getLevel() || (killed.getLevel() - 5) <= env.getPlayer().getLevel()) {
                return defaultOnKillRankedEvent(env, 0, 7, true);
            }
        }
        return false;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (env.getTargetId() == 799381) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1352);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

}
