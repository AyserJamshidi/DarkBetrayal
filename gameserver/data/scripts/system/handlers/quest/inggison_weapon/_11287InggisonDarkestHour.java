/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.inggison_weapon;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;


public class _11287InggisonDarkestHour extends QuestHandler {

    private final static int questId = 11287;

    public _11287InggisonDarkestHour() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799038).addOnQuestStart(questId);
        qe.registerQuestNpc(799038).addOnTalkEvent(questId);
		qe.registerQuestNpc(296907).addOnTalkEvent(questId);
		qe.registerQuestNpc(296908).addOnTalkEvent(questId);
		qe.registerQuestNpc(799094).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();
        if (qs == null || qs.canRepeat()) {
            if (targetId == 799038) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 296907 || targetId == 296908) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return useQuestObject(env, 0, 0, true, 0); // 1
				}
			}
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799094) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
