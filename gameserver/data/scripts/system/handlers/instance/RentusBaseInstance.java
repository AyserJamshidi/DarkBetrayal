/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import java.util.List;
import java.util.Map;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.controllers.effect.PlayerEffectController;
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
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
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
@InstanceID(300280000)
public class RentusBaseInstance extends GeneralInstanceHandler {

    private Map<Integer, StaticDoor> doors;
    private boolean isInstanceDestroyed;
    private int umahtasEchoKilled;
    private int karianasEchoKilled;
    private int upadisEchoKilled;
    private int ragnarokKilled;
	private int movie = 0;

    @Override
    public void onDie(final Npc npc) {
        if (isInstanceDestroyed) {
            return;
        }

        switch (npc.getNpcId()) {
            case 282543:
                umahtasEchoKilled++;
                if (umahtasEchoKilled == 2) {
                    spawn(217315, 759.46f, 636.45f, 157f, (byte) 7);
                    sendMsg(1500424, instance.getNpc(799671).getObjectId(), false, 0);
                    despawnNpcs(instance.getNpcs(282546));
                    ThreadPoolManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            if (!isInstanceDestroyed) {
                                spawn(282465, 759.46f, 636.45f, 157f, (byte) 0);
                            }
                        }

                    }, 3000);
                }
                break;
            case 282544:
                karianasEchoKilled++;
                if (karianasEchoKilled == 2) {
                    spawn(217316, 258.09f, 671.51f, 170f, (byte) 17);
                    spawn(282465, 258.09f, 671.51f, 170f, (byte) 0);
                    sendMsg(1500430, instance.getNpc(799672).getObjectId(), false, 0);
                    despawnNpcs(instance.getNpcs(282547));
                    Npc merops = instance.getNpc(799672);
                    merops.getSpawn().setWalkerId("3002800002");
                    WalkManager.startWalking((NpcAI2) merops.getAi2());
                    despawnMerops(instance.getNpc(799672));
                }
                break;
            case 282545:
                upadisEchoKilled++;
                if (upadisEchoKilled == 2) {
                    despawnNpcs(instance.getNpcs(282548));
                }
                break;
            case 217315:
                spawn(282626, 728.910f, 632.190f, 157f, (byte) 0);
                doors.get(82).setOpen(true);
                Npc merops = instance.getNpc(799671);
                despawnMerops(merops);
                sendMsg(1500420, merops.getObjectId(), false, 0);
                Npc ariana1 = instance.getNpc(799668);
                if (ariana1 != null) {
                    SkillEngine.getInstance().getSkill(ariana1, 19921, 60, ariana1).useNoAnimationSkill();
                }
                break;
            case 217307:
                doors.get(236).setOpen(true);
                instance.doOnAllPlayers(new Visitor<Player>() {

                    @Override
                    public void visit(Player player) {
                        removeEffects(player);
                    }

                });
                startZantarazEvent();
                break;
            case 217313:
                spawn(730401, 193.6f, 436.5f, 262f, (byte) 86);
                Npc ariana = (Npc) spawn(799670, 183.736f, 391.392f, 260.571f, (byte) 26);
                NpcShoutsService.getInstance().sendMsg(ariana, 1500417, ariana.getObjectId(), 0, 5000);
                NpcShoutsService.getInstance().sendMsg(ariana, 1500418, ariana.getObjectId(), 0, 8000);
                NpcShoutsService.getInstance().sendMsg(ariana, 1500419, ariana.getObjectId(), 0, 11000);
                spawnEndEvent(800227, "3002800003", 2000);
                spawnEndEvent(800227, "3002800004", 2000);
                spawnEndEvent(800228, "3002800007", 4000);
                spawnEndEvent(800227, "3002800005", 6000);
                spawnEndEvent(800228, "3002800006", 8000);
                spawnEndEvent(800229, "3002800008", 10000);
                spawnEndEvent(800229, "3002800009", 10000);
                spawnEndEvent(800230, "30028000010", 12000);
                spawnEndEvent(800230, "30028000011", 12000);
				sendPacket(new SM_PLAY_MOVIE(0, 481));
                break;
            case 282394:
                spawn(282395, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
                despawnNpc(npc);
                break;
            case 283000:
            case 283001:
                despawnNpc(npc);
                break;
            case 217301:
                ragnarokKilled++;
                if (ragnarokKilled == 4) {
                    ragnarokKilled = 0;
                    if (instance.getNpcs(282382).size() < 50) { // security
                        spawn(282382, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
                    }
                }
                despawnNpc(npc);
                break;
            case 217299:
                final float x = npc.getX();
                final float y = npc.getY();
                final float z = npc.getZ();
                final int h = npc.getHeading();
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (!isInstanceDestroyed) {
                            if (x > 0 && y > 0 && z > 0) {
                                spawn(217300, x, y, z, h);
								spawn(282946, x, y, z, h);
                            }
                        }
                    }

                }, 4000);
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        despawnNpc(npc);
                    }

                }, 2000);
                break;
			case 217292:
                final float a = npc.getX();
                final float b = npc.getY();
                final float c = npc.getZ();
                final int d = npc.getHeading();
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (!isInstanceDestroyed) {
                            if (a > 0 && b > 0 && c > 0) {
                                spawn(217293, a, b, c, d);
								spawn(282946, a, b, c, d);
                            }
                        }
                    }

                }, 4000);
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        despawnNpc(npc);
                    }

                }, 2000);
                break;
        }
    }

    private void startWalk(Npc npc, String walkId) {
        npc.getSpawn().setWalkerId(walkId);
        WalkManager.startWalking((NpcAI2) npc.getAi2());
        npc.setState(1);
        PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
    }

    private void startZantarazEvent() {
        final Npc ariana = (Npc) spawn(799667, 674.73f, 625.42f, 156f, (byte) 0);
        final Npc priest1 = (Npc) spawn(800198, 679.6f, 634.59f, 156f, (byte) 0);
        final Npc priest2 = (Npc) spawn(800198, 683.3f, 623.59f, 156f, (byte) 0);
        final Npc warrior1 = (Npc) spawn(800196, 684f, 632.61f, 156f, (byte) 0);
        final Npc warrior2 = (Npc) spawn(800196, 687.56f, 622.07f, 156f, (byte) 0);
        final Npc ranger1 = (Npc) spawn(800197, 690.47f, 631.58f, 156f, (byte) 0);
        final Npc ranger2 = (Npc) spawn(800197, 683.09f, 618.9f, 156f, (byte) 0);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                startWalk(ariana, "30028000017");
                startWalk(priest1, "30028000018");
                startWalk(priest2, "30028000019");
                startWalk(warrior1, "30028000020");
                startWalk(warrior2, "30028000021");
                startWalk(ranger1, "30028000022");
                startWalk(ranger2, "30028000023");
            }

        }, 1000);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                final Npc invisible = (Npc) spawn(282601, 621.4345f, 617.3071f, 154.125f, (byte) 0);
                invisible.getKnownList().doOnAllPlayers(new Visitor<Player>() {

                    @Override
                    public void visit(Player player) {
                        player.sendPck(new SM_PLAY_MOVIE(0, 0, 482, 65536, invisible.getObjectId()));
                    }

                });
                stopWalk(priest1);
                stopWalk(priest2);
                stopWalk(warrior1);
                stopWalk(warrior2);
                stopWalk(ranger1);
                stopWalk(ranger2);
                CreatureActions.delete(invisible);
                CreatureActions.delete(ariana);
            }

        }, 12000);
    }
	
    private void sendPacket(final AionServerPacket packet) {
        instance.doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(packet);
            }

        });
    }

    private void stopWalk(Npc npc) {
        npc.getSpawn().setWalkerId(null);
        WalkManager.stopWalking((NpcAI2) npc.getAi2());
    }

    private void spawnEndEvent(int npcId, String walkern, int time) {
        sp(npcId, 193.39548f, 435.56158f, 260.57135f, (byte) 86, time, walkern);
    }

    private void despawnMerops(final Npc merops) {
        if (merops != null) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (!isInstanceDestroyed) {
                        if (merops != null) {
                            SkillEngine.getInstance().getSkill(merops, 19358, 60, merops).useNoAnimationSkill();
                            if (merops.getNpcId() == 799671) {
                                despawnNpc(instance.getNpc(701155));
                                sendMsg(1500425, merops.getObjectId(), false, 0);
                                spawn(282465, 744.2147f, 634.5343f, 155.69595f, (byte) 0);
                                despawnNpc(instance.getNpc(282626));
                            }
                            despawnNpc(merops);
                        }
                    }
                }

            }, merops.getNpcId() == 799671 ? 3000 : 5000);

        }
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
            switch (Rnd.get(1, 4)) {
                case 1:
                    spawn(218572, 538.6639f, 477.2887f, 145.82251f, (byte) 90);
                    break;
                case 2:
                    spawn(218572, 377.38275f, 461.9165f, 138.54454f, (byte) 60);
                    break;
                case 3:
                    spawn(218572, 317.74368f, 623.0686f, 150.33286f, (byte) 45);
                    break;
                case 4:
                    spawn(218572, 316.23618f, 726.1624f, 163.5f, (byte) 40);
                    break;
            }
    }

    @Override
    public void onLeaveInstance(Player player) {
        removeEffects(player);
    }

    @Override
    public void onPlayerLogOut(Player player) {
        removeEffects(player);
    }

    private void removeEffects(Player player) {
        PlayerEffectController effectController = player.getEffectController();
        effectController.removeEffect(19376);
        effectController.removeEffect(19350);
        effectController.removeEffect(20031);
        effectController.removeEffect(20037);
    }

    private boolean canUseTank() {
        Npc zantaraz = instance.getNpc(217307);
        if (zantaraz != null && !CreatureActions.isAlreadyDead(zantaraz)) {
            return true;
        }
        return false;
    }

    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
            case 701151:
                SkillEngine.getInstance().getSkill(npc, 19909, 60, npc).useNoAnimationSkill();
                despawnNpc(npc);
                break;
            case 701152:
                SkillEngine.getInstance().getSkill(npc, 19910, 60, npc).useNoAnimationSkill();
                despawnNpc(npc);
                break;
            case 218611:
                if (canUseTank()) {
                    SkillEngine.getInstance().getSkill(npc, 19376, 60, player).useNoAnimationSkill();
                    npc.getController().scheduleRespawn();
                }
                despawnNpc(npc);
                break;
            case 218610:
                if (canUseTank()) {
                    SkillEngine.getInstance().getSkill(npc, 19350, 60, player).useNoAnimationSkill();
                    npc.getController().scheduleRespawn();
                }
                despawnNpc(npc);
                break;
            case 701097:
                despawnNpc(npc);
                break;
            case 701100:
                if (instance.getNpc(799543) == null) {
                    spawn(799543, 506.303f, 613.902f, 158.179f, (byte) 0);
                }
                break;
        }
    }

    private void despawnNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            npc.getController().onDelete();
        }
    }

    private void sp(final int npcId, final float x, final float y, final float z, final byte h, int time, final String walkern) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    Npc npc = (Npc) spawn(npcId, x, y, z, h);
                    npc.getSpawn().setWalkerId(walkern);
                    startEndWalker(npc);
                    unSetEndWalker(npc);
                }
            }

        }, time);
    }

    private void startEndWalker(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    WalkManager.startWalking((NpcAI2) npc.getAi2());
                    npc.setState(1);
                    PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
                }
            }

        }, 3000);
    }

    private void unSetEndWalker(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isInstanceDestroyed) {
                    stopWalk(npc);
                }
            }

        }, 8000);
    }

    private void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("RESIDENTIAL_ZONE_300280000")) {
            removeEffects(player);
        }else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("THE_BRIDGE_300280000")) {
			if (movie == 0) {
				movie++;
				sendPacket(new SM_PLAY_MOVIE(0, 480));
			}
        }
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
        TeleportService.teleportTo(player, mapId, instanceId, 601.0055f, 128.2366f, 46.7f, (byte) 7);
        return true;
    }
}
