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
import mw.engines.geo.GeoHelper;

/**
 * @author GoodT
 */
public class Gps extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        admin.sendMsg("== Coordinates ==");
        admin.sendMsg("X = " + admin.getX());
        admin.sendMsg("Y = " + admin.getY());
        admin.sendMsg("Z = " + admin.getZ());
        admin.sendMsg("GeoZ = " + GeoHelper.getZ(admin));
        admin.sendMsg("H = " + admin.getHeading());
        admin.sendMsg("World = " + admin.getWorldId());
        admin.sendMsg(String.format("Channel: id = '%d' ownerId = '%d'", admin.getInstanceId(), admin.getPosition().getWorldMapInstance().getOwnerId()));
    }

    @Override
    public void onError(Player player, Exception e) {
        // TODO Auto-generated method stub
    }
}
