/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package zone;

import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;
import com.ne.gs.world.zone.handler.ZoneHandler;
import com.ne.gs.world.zone.handler.ZoneNameAnnotation;

/**
 * @author MrPoke
 */
@ZoneNameAnnotation("ASMODIANS_BASE_400010000 ELYOS_BASE_400010000")
public class AbyssBaseShield implements ZoneHandler {

    @Override
    public void onEnterZone(Creature creature, ZoneInstance zone) {
        Creature actingCreature = creature.getActingCreature();
        if (actingCreature instanceof Player && !((Player) actingCreature).isGM()) {
            ZoneName currZone = zone.getZoneTemplate().getName();
            if (currZone == ZoneName.get("ASMODIANS_BASE_400010000")) {
                if (actingCreature.getRace() == Race.ELYOS) {
                    creature.getController().die();
                }
            } else if (currZone == ZoneName.get("ELYOS_BASE_400010000")) {
                if (actingCreature.getRace() == Race.ASMODIANS) {
                    creature.getController().die();
                }
            }
        }
    }

    @Override
    public void onLeaveZone(Creature player, ZoneInstance zone) {
    }

}
