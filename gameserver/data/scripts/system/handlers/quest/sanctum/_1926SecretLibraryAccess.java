/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.sanctum;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.WorldMapType;

/**
 * @author xaerolt, Rolandas
 */
public class _1926SecretLibraryAccess extends QuestHandler {

    private final static int questId = 1926;
    private final static int[] npc_ids = {203894, 203098};// 203894 - Latri(start and finish), 203098 - Spatalos(for
    // recomendation)

    public _1926SecretLibraryAccess() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203894).addOnQuestStart(questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
    }

    // self explanatory
    private boolean AreVerteronQuestsFinished(Player player) {
        QuestState qs = player.getQuestStateList().getQuestState(1020);// last quest in Verteron state
        return ((qs == null) || (qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE)) ? false : true;
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (targetId == 203894) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            } else if (qs.getStatus() == QuestStatus.REWARD && qs.getQuestVarById(0) == 0 || qs.getStatus() == QuestStatus.COMPLETE) {
                if (env.getDialog() == QuestDialog.USE_OBJECT && qs.getStatus() == QuestStatus.REWARD) {
                    return sendQuestDialog(env, 10002);
                } else if (env.getDialogId() == 18) {
                    removeQuestItem(env, 182206022, 1);
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                    return sendQuestEndDialog(env);
                } else if (env.getDialogId() == 1009) {
                    return sendQuestEndDialog(env);
                }
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        TeleportService.teleportTo(player, WorldMapType.SANCTUM.getId(), 2032.9f, 1473.1f, 592.2f, (byte) 195);
                    }
                }, 3000);
            }
        } else if (targetId == 203098) {
            if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
                if (env.getDialog() == QuestDialog.START_DIALOG) {
                    if (AreVerteronQuestsFinished(player)) {
                        return sendQuestDialog(env, 1011);
                    } else {
                        return sendQuestDialog(env, 1097);
                    }
                } else if (env.getDialogId() == 10255) {
                    if (giveQuestItem(env, 182206022, 1)) {
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                    }
                    player.sendPck(new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                    return true;
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        return false;
    }
}
