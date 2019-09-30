/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.kromedes_trial;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestActionType;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Rolandas
 */
public class _28604RecoveringRotan extends QuestHandler {

    private final static int questId = 28604;

    public _28604RecoveringRotan() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZone(ZoneName.get("GRAND_CAVERN_300230000"), questId);
        qe.registerQuestNpc(700961).addOnTalkEvent(questId); // Grave Robber's Corpse
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        if (env.getTargetId() == 700961) {
            if (env.getDialog() == QuestDialog.USE_OBJECT) {
                if (qs.getStatus() == QuestStatus.START) {
                    env.setQuestId(questId);
                    qs.setStatus(QuestStatus.REWARD);
                    updateQuestStatus(env);
                    return QuestService.finishQuest(env);
                }
                if (this.checkItemExistence(env, 164000141, 1, false)) {
                    env.setQuestId(0);
                    return sendQuestDialog(env, 0);
                } else {
					env.setQuestId(0);
					giveQuestItem(env, 164000141, 1);
                    return sendQuestDialog(env, 0);
                }
            }
        }

        return false;
    }

    @Override
    public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
        // Allow to use body even when quest was completed
        return env.getTargetId() == 700961 && questEventType == QuestActionType.ACTION_ITEM_USE;
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        if (zoneName != ZoneName.get("GRAND_CAVERN_300230000")) {
            return false;
        }

        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            env.setQuestId(questId);
            return QuestService.startQuest(env, QuestStatus.START);
        }

        return false;
    }

}
