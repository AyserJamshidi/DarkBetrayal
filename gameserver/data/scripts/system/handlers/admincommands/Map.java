/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.ai2.event.AIEventType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.taskmanager.tasks.MovementNotifyTask;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.WorldMapInstance;

/**
 * @author Rolandas
 */
public class Map extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.commons.utils.AbstractCommand#runImpl(java.lang.Object, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
        WorldMapInstance instance = admin.getPosition().getWorldMapInstance();
        if ("freeze".equalsIgnoreCase(params[0])) {
            for (Npc npc : instance.getNpcs()) {
                npc.getAi2().onGeneralEvent(AIEventType.FREEZE);
            }
            admin.sendMsg("World map is frozen!");
        } else if ("unfreeze".equalsIgnoreCase(params[0])) {
            for (Npc npc : instance.getNpcs()) {
                npc.getAi2().onGeneralEvent(AIEventType.UNFREEZE);
            }
            admin.sendMsg("World map is unfrozen!");
        } else if ("stats".equalsIgnoreCase(params[0])) {
            for (String line : MovementNotifyTask.getInstance().dumpBroadcastStats()) {
                admin.sendMsg(line);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("usage: //map freeze | unfreeze | stats");
    }
}
