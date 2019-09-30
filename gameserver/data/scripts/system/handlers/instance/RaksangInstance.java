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

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticDoor;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionServerPacket;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;

/**
 * @author xTz
 */
@InstanceID(300310000)
public class RaksangInstance extends GeneralInstanceHandler {

    private Map<Integer, StaticDoor> doors;
    private int generatorKilled;
    private int ashulagenKilled;
    private int gargoyleKilled;
    private int rakshaHelpersKilled;
    private boolean isInstanceDestroyed;
	private int movie = 0;
	private int movie2 = 0;

    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
			case 217388:
				doors.get(104).setOpen(true);
				break;
            case 730453:
            case 730454:
            case 730455:
            case 730456:
                generatorKilled++;
                if (generatorKilled == 1) {
                    sendMsg(1401133);
                    doors.get(87).setOpen(true);
                } else if (generatorKilled == 2) {
                    sendMsg(1401133);
                    doors.get(167).setOpen(true);
                } else if (generatorKilled == 3) {
                    sendMsg(1401133);
                    doors.get(114).setOpen(true);
                } else if (generatorKilled == 4) {
                    sendMsg(1401134);
                    doors.get(165).setOpen(true);
					spawn(217392, 607.081f, 683.660f, 1184.571f, (byte) 13);
                }
                despawnNpc(npc);
                break;
            case 217392:
                doors.get(103).setOpen(true);
                break;
            case 217469:
                doors.get(107).setOpen(true);
                break;
            case 217471:
            case 217472:
                gargoyleKilled++;
                if (gargoyleKilled == 2) {
                    Npc magic = instance.getNpc(217473);
                    if (magic != null) {
                        sendMsg(1401159);
                        magic.getEffectController().removeEffect(19126);
                    }
                }
                despawnNpc(npc);
                break;
            case 217473:
                despawnNpc(npc);
                final Npc dust = (Npc) spawn(701075, 1068.630f, 967.205f, 138.785f, (byte) 0, 323);
                doors.get(105).setOpen(true);
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (!isInstanceDestroyed && dust != null && !CreatureActions.isAlreadyDead(dust)) {
                            CreatureActions.delete(dust);
                        }
                    }

                }, 4000);
                break;
            case 217455:
                ashulagenKilled++;
                if (ashulagenKilled == 1 || ashulagenKilled == 2 || ashulagenKilled == 3) {
                    sendMsg(1401160);
                } else if (ashulagenKilled == 4) {
                    spawn(217456, 615.081f, 640.660f, 524.195f, (byte) 0);
                    sendMsg(1401135);
                }
                break;
            case 217425:
				sendPacket(new SM_PLAY_MOVIE(0, 478));
            case 217451:
            case 217456:
                rakshaHelpersKilled++;
                if (rakshaHelpersKilled < 3) {
                    sendMsg(1401161);
                } else if (rakshaHelpersKilled == 3) {
                    sendMsg(1401162);
                }
                break;
            case 217647:
            case 217475:
                rakshaHelpersKilled = 4;
                break;
            case 217764:
				spawn(217472, 1048.490f, 956.380f, 138.744f, (byte) 10);
				spawn(217471, 1086.940f, 953.550f, 138.744f, (byte) 47);
				spawn(217473, 1067.019f, 946.022f, 138.744f, (byte) 29);
				sendPacket(new SM_PLAY_MOVIE(0, 479));
                break;
        }
    }
	
    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("TERRORS_VAULT_300310000")) {
			if (movie == 0) {
				movie++;
				sendPacket(new SM_PLAY_MOVIE(0, 477));
			}
        }
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
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
        doors.clear();
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
    }

    private void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
	
    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 846.655f, 945.6366f, 1207.1f, (byte) 72);
        return true;
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }

}
