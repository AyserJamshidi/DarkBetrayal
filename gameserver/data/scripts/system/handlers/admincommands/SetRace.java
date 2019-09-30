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
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.WorldMapType;

/**
 * @author Centisgood(Barahime)
 */
public class SetRace extends ChatCommand {

    @Override
    public void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) {
        if (params.length < 1) {
            admin.sendMsg("syntax: //setrace <elyos | asmodians>");
            return;
        }

        VisibleObject visibleobject = admin.getTarget();

        if (visibleobject == null || !(visibleobject instanceof Player)) {
            admin.sendMsg("Wrong select target.");
            return;
        }

        Player target = (Player) visibleobject;
        if (params[0].equalsIgnoreCase("elyos")) {
            target.getCommonData().setRace(Race.ELYOS);
            TeleportService.teleportTo(target, WorldMapType.SANCTUM.getId(), 1322f, 1511f, 568f, 0);
            target.sendMsg("Has been moved to Sanctum.");
        } else if (params[0].equalsIgnoreCase("asmodians")) {
            target.getCommonData().setRace(Race.ASMODIANS);
            TeleportService.teleportTo(target, WorldMapType.PANDAEMONIUM.getId(), 1679f, 1400f, 195f, 0);
            target.sendMsg("Has been moved to Pandaemonium");
        }
        admin.sendMsg(target.getName() + " race has been changed to " + params[0] + ".\n" + target.getName()
            + " has been moved to town.");
    }

    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("syntax: //setrace <elyos | asmodians>");
    }
}
