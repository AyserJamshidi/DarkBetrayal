/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.silentera_canyon;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.QuestService;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Ritsu
 */
public class _30157VilisMind extends QuestHandler {

    private final static int questId = 30157;

    public _30157VilisMind() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204304).addOnQuestStart(questId); // Vili
        qe.registerQuestNpc(204304).addOnTalkEvent(questId); // Vili
        qe.registerQuestNpc(799234).addOnTalkEvent(questId); // Nep
        qe.registerQuestNpc(700570).addOnTalkEvent(questId); // Statue Sinigalla
        qe.registerQuestNpc(799339).addOnTalkEvent(questId); // Sinigalla
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        QuestDialog dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204304) {
                if (dialog == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 1011);
                } else if (dialog == QuestDialog.ACCEPT_QUEST) {
                    if (!giveQuestItem(env, 182209254, 1)) {
                        return true;
                    }
                    return sendQuestStartDialog(env);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799234) {
                switch (dialog) {
                    case USE_OBJECT:
                        return sendQuestDialog(env, 2375);
                    case SELECT_REWARD:
                        return sendQuestEndDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 700570) {
                switch (dialog) {
                    case USE_OBJECT:
                        if (var == 0) {
                            QuestService.addNewSpawn(600010000, 1, 799339, (float) 545.3877, (float) 1232.0298, (float) 304.3357, (byte) 76);
                            return useQuestObject(env, 0, 0, false, 0, 0, 0, 182209254, 1);
                        }
                }
            }
            if (targetId == 799339) {
                switch (dialog) {
                    case START_DIALOG:
                        if (var == 0) {
                            return sendQuestDialog(env, 1352);
                        }
                    case STEP_TO_1:
                        if (var == 0) {
                            defaultCloseDialog(env, 0, 0, true, false);
                            final Npc npc = (Npc) env.getVisibleObject();
                            ThreadPoolManager.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    npc.getController().onDelete();
                                }
                            }, 40000);
                            return true;
                        }
                }
            }
        }
        return false;
    }
}
