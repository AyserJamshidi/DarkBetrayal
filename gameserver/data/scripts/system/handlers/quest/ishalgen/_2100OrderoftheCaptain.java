/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package quest.ishalgen;

import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.questEngine.QuestEngine;
import com.ne.gs.questEngine.handlers.QuestHandler;
import com.ne.gs.questEngine.model.QuestDialog;
import com.ne.gs.questEngine.model.QuestEnv;
import com.ne.gs.questEngine.model.QuestState;
import com.ne.gs.questEngine.model.QuestStatus;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author MrPoke
 */
public class _2100OrderoftheCaptain extends QuestHandler {

    private final static int questId = 2100;

    public _2100OrderoftheCaptain() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(203516).addOnTalkEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("ALDELLE_VILLAGE_220010000"), questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        player.sendMsg("onDialog.");
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            player.sendMsg("1");
            return false;
        }

        int targetId = 0;
        player.sendMsg("2");
        if (env.getVisibleObject() instanceof Npc) {
            player.sendMsg("3");
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }
        if (targetId != 203516) {
            player.sendMsg("4");
            return false;
        }
        if (qs.getStatus() == QuestStatus.START) {
            player.sendMsg("5");
            if (env.getDialog() == QuestDialog.START_DIALOG) {
                player.sendMsg("6");
                qs.setQuestVar(1);
                player.sendMsg("7");
                qs.setStatus(QuestStatus.REWARD);
                player.sendMsg("8");
                updateQuestStatus(env);
                player.sendMsg("9");
                return sendQuestDialog(env, 1011);
            } else {
                player.sendMsg("10");
                return sendQuestStartDialog(env);
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            player.sendMsg("11");
            if (env.getDialogId() == 18) {
                player.sendMsg("12");
                int[] ids = {2001, 2002, 2003, 2004, 2005, 2006, 2007};
                player.sendMsg("13");
                for (int id : ids) {
                    player.sendMsg("for loop");
                    QuestEngine.getInstance().onEnterZoneMissionEnd(new QuestEnv(env.getVisibleObject(), env.getPlayer(), id, env.getDialogId()));
                }
            }
            player.sendMsg("14");
            return sendQuestEndDialog(env);
        }
        player.sendMsg("15");
        return false;
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        return defaultOnEnterZoneEvent(env, zoneName, ZoneName.get("ALDELLE_VILLAGE_220010000"));
    }
}
