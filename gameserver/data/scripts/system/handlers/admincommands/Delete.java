/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.io.IOException;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.ne.gs.utils.chathandlers.ChatCommand;

/**
 * @author Luno
 */
public class Delete extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {

        VisibleObject cre = player.getTarget();
        if (!(cre instanceof Npc)) {
            player.sendMsg("Wrong target");
            return;
        }
        Npc npc = (Npc) cre;
        SpawnTemplate template = npc.getSpawn();
        if (template.hasPool()) {
            player.sendMsg("Can't delete pooled spawn template");
            return;
        }
        if (template instanceof SiegeSpawnTemplate) {
            player.sendMsg("Can't delete siege spawn template");
            return;
        }

        npc.getController().delete();
        try {
            DataManager.SPAWNS_DATA2.saveSpawn(player, npc, true);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMsg("Could not remove spawn");
            return;
        }
        player.sendMsg("Spawn removed");
    }
}
