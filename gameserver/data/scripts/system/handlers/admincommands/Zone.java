/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package admincommands;

import java.io.FileWriter;
import java.util.List;

import com.ne.commons.annotations.NotNull;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.zone.ZoneType;
import com.ne.gs.utils.chathandlers.ChatCommand;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author ATracer
 */
public class Zone extends ChatCommand {

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#runImpl(com.ne.gs.model.gameobjects.player.Player, java.lang.String, java.lang.String[])
     */
    @Override
    protected void runImpl(@NotNull Player admin, @NotNull String alias, @NotNull String... params) throws Exception {
        Creature target;
        if (admin.getTarget() == null || !(admin.getTarget() instanceof Creature)) {
            target = admin;
        } else {
            target = (Creature) admin.getTarget();
        }
        if (params.length == 0) {
            List<ZoneInstance> zones = target.getPosition().getMapRegion().getZones(target);
            if (zones.isEmpty()) {
                admin.sendMsg(target.getName() + " are out of any zone");
            } else {
                admin.sendMsg(target.getName() + " are in zone: ");
                admin.sendMsg("Registered zones:");
                if (admin.isInsideZoneType(ZoneType.DAMAGE)) {
                    admin.sendMsg("DAMAGE");
                }
                if (admin.isInsideZoneType(ZoneType.FLY)) {
                    admin.sendMsg("FLY");
                }
                if (admin.isInsideZoneType(ZoneType.PVP)) {
                    admin.sendMsg("PVP");
                }
                if (admin.isInsideZoneType(ZoneType.SIEGE)) {
                    admin.sendMsg("CASTLE");
                }
                if (admin.isInsideZoneType(ZoneType.WATER)) {
                    admin.sendMsg("WATER");
                }
                for (ZoneInstance zone : zones) {
                    admin.sendMsg(zone.getAreaTemplate().getZoneName().name());
                    admin.sendMsg("Fly: " + zone.canFly() + "; Glide: " + zone.canGlide());
                    admin.sendMsg("Ride: " + zone.canRide() + "; Fly-ride: " + zone.canFlyRide());
                    admin.sendMsg("Kisk: " + zone.canPutKisk() + "; Racall: " + zone.canRecall());
                    admin.sendMsg("Same race duels: " + zone.isSameRaceDuelsAllowed() + "; Other race duels: " + zone
                            .isOtherRaceDuelsAllowed());
                    admin.sendMsg("PvP: " + zone.isPvpAllowed());
                }
            }
        } else if ("?".equalsIgnoreCase(params[0])) {
            throw new Exception();
        } else if ("refresh".equalsIgnoreCase(params[0])) {
            admin.revalidateZones();
        } else if ("dumpn".equalsIgnoreCase(params[0])) {
            admin.sendMsg("Neutral zones: ");
            FileWriter fw = new FileWriter("zones-neutral.txt", true);
            for (ZoneInstance zone : target.getPosition().getMapRegion().getZones(target)) {
                if (zone.isInsideCreature(target)) {
                    admin.sendMsg(zone.getAreaTemplate().getZoneName().name());
                    fw.write(target.getWorldId() + " \t " + zone.getAreaTemplate().getZoneName().name() + "\n");
                }
            }
            fw.close();
        } else if ("dumpp".equalsIgnoreCase(params[0])) {
            admin.sendMsg("Pvp zones: ");
            FileWriter fw = new FileWriter("zones-pvp.txt", true);
            for (ZoneInstance zone : target.getPosition().getMapRegion().getZones(target)) {
                if (zone.isInsideCreature(target)) {
                    admin.sendMsg(zone.getAreaTemplate().getZoneName().name());
                    fw.write(target.getWorldId() + " \t " + zone.getAreaTemplate().getZoneName().name() + "\n");
                }
            }
            fw.close();
        } else if ("inside".equalsIgnoreCase(params[0])) {
            try {
                ZoneName name = ZoneName.get(params[1]);
                admin.sendMsg("isInsideZone: " + admin.isInsideZone(name));
            } catch (Exception e) {
                admin.sendMsg("Zone name missing!");
                admin.sendMsg("Syntax: //zone inside <zone name> ");
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.ne.gs.utils.chathandlers.ChatCommand#onError(com.ne.gs.model.gameobjects.player.Player, java.lang.Exception)
     */
    @Override
    public void onError(Player player, Exception e) {
        player.sendMsg("Syntax: //zone refresh | inside");
    }
}
