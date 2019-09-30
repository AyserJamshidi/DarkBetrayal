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

/**
 * @author xTz
 */
public class Ring extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        double direction = Math.toRadians(admin.getHeading()) - 0.5f;
        if (direction < 0) {
            direction += 2f;
        }
        float x1 = (float) (Math.cos(Math.PI * direction) * 6);
        float y1 = (float) (Math.sin(Math.PI * direction) * 6);
        admin.sendMsg("center:" + admin.getX() + " " + admin.getY() + " " + admin.getZ());
        admin.sendMsg("p1:" + admin.getX() + " " + admin.getY() + " " + (admin.getZ() + 6f));
        admin.sendMsg("p2:" + (admin.getX() + x1) + " " + (admin.getY() + y1) + " " + admin.getZ());
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax; //ring");
    }

}
