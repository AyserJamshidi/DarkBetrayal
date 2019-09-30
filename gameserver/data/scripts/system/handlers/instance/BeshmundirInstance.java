/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import java.util.Map;

import com.ne.commons.utils.Rnd;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticDoor;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionServerPacket;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;

@InstanceID(300170000)
public class BeshmundirInstance extends GeneralInstanceHandler {

    private int macunbello = 0;
	private int movie = 0;
    private int kills;
    private Map<Integer, StaticDoor> doors;

    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
            case 216238:
                openDoor(470);
                spawn(216159, 1357.0598f, 388.6637f, 249.26372f, (byte) 90);
                break;
            case 216175:
                if (Rnd.get(100) > 10) {
                    spawn(216764, 1437.2672f, 1579.4656f, 305.82492f, (byte) 97);
                    sendMsg("Mystery Box spawned");
                }
                break;
            case 216179:
            case 216181:
            case 216177:
                int chance = Rnd.get(100);

                if (chance > 90) {
                    switch (npc.getNpcId()) {
                        case 216179:
                            spawn(216764, 1625.5829f, 1493.408f, 329.94492f, (byte) 67);
                            break;
                        case 216181:
                            spawn(216764, 1633.7206f, 1429.6768f, 305.83493f, (byte) 59);
                            break;
                        case 216177:
                            spawn(216764, 1500.8236f, 1586.5652f, 329.94492f, (byte) 88);
                            break;
                    }
                    sendMsg("Congratulation: Mystery Box spawned!\nChance: " + chance);
                } else {
                    sendMsg("Chance: " + chance);
                    switch (npc.getObjectTemplate().getTemplateId()) {
                        case 216179: // Narma
                            spawn(216173, 1546.5916f, 1471.214f, 300.33008f, (byte) 84);
                            sendMsg("Gatekeeper Rhapsharr spawned");
                            break;
                        case 216181: // Kramaka
                            spawn(216171, 1403.51f, 1475.79f, 307.793f, (byte) 98);
                            sendMsg("Gatekeeper Kutarrun spawned");
                            break;
                        case 216177: // Dinata
                            spawn(216170, 1499.78f, 1507.1f, 300.33f, (byte) 0);
                            sendMsg("Gatekeeper Darfall spawned");
                    }
                }
                break;
            case 216583:
                spawn(799518, 936.0029f, 441.51712f, 220.5029f, (byte) 28);
                break;
            case 216584:
                spawn(799519, 791.0439f, 439.79608f, 220.3506f, (byte) 28);
                break;
            case 216585:
                spawn(799520, 820.70624f, 278.828f, 220.19385f, (byte) 55);
                break;
            case 216586:
				sendMsg(1400469);
                if (macunbello < 12) {
                    spawn(216735, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // strongest macunbello
                } else if (macunbello < 14) {
                    spawn(216734, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // 2th strongest macunbello
                } else if (macunbello < 21) {
                    spawn(216737, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // 2th weakest macunbello
                } else {
                    spawn(216245, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // weakest macunbello
                }
                macunbello = 0;
                sendPacket(new SM_QUEST_ACTION(0, 0));
                openDoor(467);
				sendPacket(new SM_PLAY_MOVIE(0, 445));
                break;
            case 799342:
                sendPacket(new SM_PLAY_MOVIE(0, 447));
                break;
            case 216246:
                openDoor(473);
                break;
            case 216739:
            case 216740:
                kills++;
                if (kills < 10) {
                    sendMsg(1400465);
                } else if (kills == 10) {
                    sendMsg(1400470);
                    spawn(216158, 1356.5719f, 147.76418f, 246.27373f, (byte) 91);
                }
                break;
            case 216158:
                openDoor(471);
                break;
            case 216263:
                // this is a safety Mechanism
                // super boss
                spawn(216264, 558.306f, 1369.02f, 224.795f, (byte) 70);
                // gate
                sendMsg(1400480);
                sendPacket(new SM_PLAY_MOVIE(0, 440));
                spawn(730275, 1611.1266f, 1604.6935f, 311.00503f, (byte) (byte) 0, 426);
                break;
			case 216264:
                spawn(730287, 557.306f, 1330.02f, 223.795f, (byte) 42);
                break;
            case 216250:  // Dorakiki the Bold
                sendMsg(1400471);
                spawn(216527, 1161.859985f, 1213.859985f, 284.057007f, (byte) 110); // Lupukin: cat trader
                break;
            case 216206:
            case 216207:
            case 216208:
            case 216209:
            case 216210:
            case 216211:
            case 216212:
            case 216213:
                macunbello++;
                switch (macunbello) {
                    case 12:
                        sendMsg(1400466);
                        break;
                    case 14:
                        sendMsg(1400467);
                        break;
                    case 21:
                        sendMsg(1400468);
                        break;
                }
                break;
        }
    }
	
    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("PRISON_OF_ICE_300170000")) {
			if (movie == 0) {
				movie++;
				sendPacket(new SM_PLAY_MOVIE(0, 444));
			}
        }
    }

    private void sendMsg(final String str) { // to do system message
        instance.doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendMsg(str);
            }

        });
    }

    private void sendPacket(final AionServerPacket packet) {
        instance.doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(packet);
            }

        });
    }

    @Override
    public void onPlayMovieEnd(Player player, int movieId) {
        switch (movieId) {
            case 443:
                player.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_IDCatacombs_BigOrb_Spawn);
                break;
        }
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
        doors.get(535).setOpen(true);
    }

    private void openDoor(int doorId) {
        StaticDoor door = doors.get(doorId);
        if (door != null) {
            door.setOpen(true);
        }
    }

    @Override
    public void onInstanceDestroy() {
        doors.clear();
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }

    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 1477.5f, 248.5f, 244f, (byte) 29);
        return true;
    }
}
