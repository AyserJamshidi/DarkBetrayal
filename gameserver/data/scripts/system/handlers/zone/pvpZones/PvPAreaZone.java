/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package zone.pvpZones;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.world.zone.ZoneName;
import com.ne.gs.world.zone.handler.ZoneNameAnnotation;

/**
 * @author MrPoke
 */
@ZoneNameAnnotation(value = "LC1_PVP_SUB_C DC1_PVP_ZONE")
public class PvPAreaZone extends PvPZone {

    @Override
    protected void doTeleport(Player player, ZoneName zoneName) {
        if (zoneName == ZoneName.get("LC1_PVP_SUB_C")) {
            TeleportService.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f);
        } else if (zoneName == ZoneName.get("DC1_PVP_ZONE")) {
            TeleportService.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f);
        }
    }
}
