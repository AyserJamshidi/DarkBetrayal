/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.eltnen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.state.CreatureState;
import com.ne.gs.model.EmotionType;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;


public class _1393NewFlightPath extends QuestHandler {

    private final static int questId = 1393;

    public _1393NewFlightPath() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204041).addOnQuestStart(questId);
        qe.registerQuestNpc(204041).addOnTalkEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("LF2_LIMITAREA_J"), questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204041) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1011);
				} else if (env.getDialogId() == 1012) {
					return sendQuestDialog(env, 1012);
				}  else {
					return sendQuestStartDialog(env);
				}
            }
        }
        if (qs == null) {
            return false;
        }
		if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 204041) {
				if (env.getDialog() == QuestDialog.START_DIALOG) {
					return sendQuestDialog(env, 1013);
				} else if (env.getDialogId() == 1013) {
					player.setState(CreatureState.FLIGHT_TELEPORT);
					player.unsetState(CreatureState.ACTIVE);
					player.setFlightTeleportId(21002);
					player.sendPck(new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 21002, 0));
					qs.setStatus(QuestStatus.REWARD);
				} else if (env.getDialogId() == 1003) {
					player.setState(CreatureState.FLIGHT_TELEPORT);
					player.unsetState(CreatureState.ACTIVE);
					player.setFlightTeleportId(21002);
					player.sendPck(new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 21002, 0));
					qs.setStatus(QuestStatus.REWARD);
				} else {
					return sendQuestStartDialog(env);
				}
            }
        }
	
        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 204041) {
                switch (env.getDialog()) {
                    case SELECT_REWARD: {
                        return sendQuestDialog(env, 5);
                    }
                    case SELECT_NO_REWARD: {
                        qs.setQuestVar(1);
                        qs.setStatus(QuestStatus.COMPLETE);
                        updateQuestStatus(env);
                        return true;
                    }
                    default:
                        return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        if (zoneName != ZoneName.get("LF2_LIMITAREA_J")) {
            return false;
        }
        Player player = env.getPlayer();
        if (player == null) {
            return false;
        }
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null) {
            return false;
        }

        if (qs.getQuestVarById(0) == 0) {
            qs.setStatus(QuestStatus.REWARD);
            updateQuestStatus(env);
            return true;
        }
        return false;
    }
}
