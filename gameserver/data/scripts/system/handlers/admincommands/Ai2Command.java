/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import org.slf4j.LoggerFactory;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.ai2.AI2Engine;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.ai2.event.AIEventLog;
import com.ne.gs.ai2.event.AIEventType;
import com.ne.gs.configs.main.AIConfig;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;

/**
 * @author ATracer
 */
public class Ai2Command extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        /**
         * Non target commands
         */
        String param0 = params[0];

        if (param0.equals("createlog")) {
            boolean oldValue = AIConfig.ONCREATE_DEBUG;
            AIConfig.ONCREATE_DEBUG = !oldValue;
            String msg = "New createlog value: " + !oldValue;
            player.sendMsg(msg);
            return;
        }

        if (param0.equals("eventlog")) {
            boolean oldValue = AIConfig.EVENT_DEBUG;
            AIConfig.EVENT_DEBUG = !oldValue;
            String msg = "New eventlog value: " + !oldValue;
            player.sendMsg(msg);
            return;
        }

        if (param0.equals("movelog")) {
            boolean oldValue = AIConfig.MOVE_DEBUG;
            AIConfig.MOVE_DEBUG = !oldValue;
            String msg = "New movelog value: " + !oldValue;
            player.sendMsg(msg);
            return;
        }

        if (param0.equals("say")) {
            LoggerFactory.getLogger(Ai2Command.class).info("[AI2] marker: " + params[1]);
        }

        /**
         * Target commands
         */
        VisibleObject target = player.getTarget();

        if (target == null || !(target instanceof Npc)) {
            player.sendMsg("Select target first (Npc only)");
            return;
        }
        Npc npc = (Npc) target;

        if (param0.equals("info")) {
            player.sendMsg("Ai name: " + npc.getAi2().getName());
            player.sendMsg("Ai state: " + npc.getAi2().getState());
            player.sendMsg("Ai substate: " + npc.getAi2().getSubState());
            return;
        }

        if (param0.equals("log")) {
            boolean oldValue = npc.getAi2().isLogging();
            ((AbstractAI) npc.getAi2()).setLogging(!oldValue);
            String msg = "New log value: " + !oldValue;
            player.sendMsg(msg);
            return;
        }

        if (param0.equals("print")) {
            AIEventLog eventLog = ((AbstractAI) npc.getAi2()).getEventLog();
            for (AIEventType anEventLog : eventLog) {
                player.sendMsg("EVENT: " + anEventLog.name());
            }
            return;
        }

        String param1 = params[1];
        if (param0.equals("set")) {
            AI2Engine.getInstance().setupAI(param1, npc);
        } else if (param0.equals("event")) {
            AIEventType eventType = AIEventType.valueOf(param1.toUpperCase());
            if (eventType != null) {
                npc.getAi2().onGeneralEvent(eventType);
            }
        } else if (param0.equals("event2")) {
            AIEventType eventType = AIEventType.valueOf(param1.toUpperCase());
            Creature creature = (Creature) World.getInstance().findVisibleObject(Integer.valueOf(params[2]));
            if (eventType != null) {
                npc.getAi2().onCreatureEvent(eventType, creature);
            }
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax //ai2 <set|event|event2|info|log|print|createlog|eventlog|movelog>");
    }

}
