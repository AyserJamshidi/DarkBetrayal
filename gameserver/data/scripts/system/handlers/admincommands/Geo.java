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
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.chathandlers.ChatCommand;
import mw.engines.geo.GeoEngine;
import mw.engines.geo.collision.CollidableType;
import mw.engines.geo.collision.CollisionResult;

/**
 * @author MrPoke
 */
public class Geo extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.commons.utils.AbstractCommand#runImpl(java.lang.Object, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player player, @NotNull String alias, @NotNull String... params) throws Exception {

        if(params[0].startsWith("check")){

            if(player.getTarget() == null){
                player.sendMsg("No target to check collision to");
                return;
            }

            CollisionResult result = GeoEngine.getObjectsCollision(player, player.getTarget(), CollidableType.PHYSICAL.getId());
            if(result == null)
                player.sendMsg("No collision detected");
            else
                player.sendMsg("Collision detected. Contact point:\n[X:" +result.getContactPoint().x + "; Y:" + result.getContactPoint().y + "; Z:" + result.getContactPoint().z + "]");

        }
        else if(params[0].equalsIgnoreCase("z")){
            float z = GeoEngine.getZ(player.getPosition().getMapId(), player.getPosition().getInstanceId(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
            player.sendMsg("[RealZ:" + player.getMoveController().beginZ() +"][GeoZ:"  + z +"]");
        }
    }
}
