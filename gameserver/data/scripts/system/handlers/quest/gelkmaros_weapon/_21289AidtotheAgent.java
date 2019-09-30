/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.inggison_armor;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;


public class _21289AidtotheAgent extends QuestHandler {

    private final static int questId = 21289;

    public _21289AidtotheAgent() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(799340).addOnQuestStart(questId);
        qe.registerQuestNpc(799340).addOnTalkEvent(questId);
		qe.registerQuestNpc(296913).addOnTalkEvent(questId);
		qe.registerQuestNpc(296914).addOnTalkEvent(questId);
		qe.registerQuestNpc(799095).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();
        int targetId = env.getTargetId();
        if (qs == null || qs.canRepeat()) {
            if (targetId == 799340) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 296913 || targetId == 296914) {
				if (dialog == QuestDialog.USE_OBJECT) {
					return useQuestObject(env, 0, 0, true, 0); // 1
				}
			}
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799095) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
