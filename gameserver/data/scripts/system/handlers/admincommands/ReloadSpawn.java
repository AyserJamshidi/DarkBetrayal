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
import com.ne.gs.model.gameobjects.Gatherable;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticObject;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.spawnengine.SpawnEngine;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.World;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author Luno
 */
public class ReloadSpawn extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) {
        int worldId = 0;
        if (params.length == 1 && "this".equals(params[0])) {
            worldId = player.getWorldId();
        }

        final int worldIdFinal = worldId;
        // despawn all
        World.getInstance().doOnAllObjects(new Visitor<VisibleObject>() {
            @Override
            public void visit(VisibleObject object) {
                if (worldIdFinal != 0 && object.getWorldId() != worldIdFinal) {
                    return;
                }
                if (object instanceof Npc || object instanceof Gatherable || object instanceof StaticObject) {
                    object.getController().delete();
                }
            }
        });

        if (worldId == 0) {
            SpawnEngine.spawnAll();
        } else {
            SpawnEngine.spawnWorldMap(worldId);
        }
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
