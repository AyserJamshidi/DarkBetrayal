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
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.DescId;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Gatherable;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticDoor;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.instance.InstanceScoreType;
import com.ne.gs.model.instance.instancereward.DarkPoetaReward;
import com.ne.gs.model.team2.group.PlayerGroup;
import com.ne.gs.network.aion.AionServerPacket;
import com.ne.gs.network.aion.serverpackets.*;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.knownlist.Visitor;

@InstanceID(300040000)
public class DarkPoetaInstance extends GeneralInstanceHandler {

    private Map<Integer, StaticDoor> doors;
    private final AtomicInteger specNpcKilled = new AtomicInteger();
    private Future<?> instanceTimer;
    private long startTime;
    private DarkPoetaReward instanceReward;
    private boolean isInstanceDestroyed;
	
    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        instanceReward = new DarkPoetaReward(mapId, instanceId);
        instanceReward.setInstanceScoreType(InstanceScoreType.PREPARING);
        doors = instance.getDoors();
    }
	
    @Override
    public void onEnterInstance(Player player) {
        if (instanceTimer == null) {
            startTime = System.currentTimeMillis();
            instanceTimer = ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    instanceReward.setInstanceScoreType(InstanceScoreType.START_PROGRESS);
                    sendPacket(0, 0);
                    openDoors();
                }

            }, 121000);
        }
        sendPacket(0, 0);
    }
	
    private void sendPacket(final int nameId, final int point) {
        instance.doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                if (nameId != 0) {
                    player.sendPck(new SM_SYSTEM_MESSAGE(1400237, DescId.of(nameId * 2 + 1), point));
                }
                player.sendPck(new SM_INSTANCE_SCORE(getTime(), instanceReward));
            }

        });
    }
	
    private void openDoors() {
        for (StaticDoor door : doors.values()) {
            if (door != null) {
                door.setOpen(true);
            }
        }
    }

    @Override
    public void onDie(Npc npc) {
        int npcId = npc.getNpcId();

        Creature master = npc.getMaster();
        if (master instanceof Player) {
            return;
        }

        switch (npcId) {
            case 700443:
            case 700444:
            case 700442:
            case 700446:
            case 700447:
            case 700445:
            case 700440:
            case 700441:
            case 700439:
                toScheduleMarbataController(npcId);
                return;
        }

        int points = calculatePointsReward(npc);
        if (instanceReward.getInstanceScoreType().isStartProgress()) {
            instanceReward.addNpcKill();
            instanceReward.addPoints(points);
            sendPacket(npc.getObjectTemplate().getNameId(), points);
        }

        switch (npcId) {
            case 214894:
                sendPacket(new SM_PLAY_MOVIE(0, 426));
                break;
            case 214895:
            case 214896:
            case 214897:
                int killedCount = specNpcKilled.incrementAndGet();
                if (killedCount == 3) {
                    spawn(214904, 275.34537f, 323.02072f, 130.9302f, (byte) 52);
                    sendPacket(new SM_PLAY_MOVIE(0, 427));
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        public void run() {
                            despawnNpcs( instance.getNpcs( 281090 ) );
                            despawnNpcs( instance.getNpcs( 281091 ) );
                            despawnNpcs( instance.getNpcs( 281092 ) );
                        }
                    }, 4000);
                }
                break;
            case 281178:
                npc.getController().onDelete();
                break;
            case 214904:
                instanceReward.setInstanceScoreType(InstanceScoreType.END_PROGRESS);
                instanceReward.setRank(checkRank(instanceReward.getPoints()));
                sendPacket(npc.getObjectTemplate().getNameId(), points);
                break;
			case 215280:
			case 215281:
			case 215282:
			case 215283:			
            case 215284:
                spawn(730211, 1179.966187f, 1222.426880f, 145.40439f, (byte) 25, 223);
                break;
        }
    }

    private int calculatePointsReward(Npc npc) {
        int pointsReward = 0;

        // Usually calculated by npcRank
        switch (npc.getObjectTemplate().getRating()) {
            case HERO:
                switch (npc.getObjectTemplate().getHpGauge()) {
                    case 21:
                        pointsReward = 786;
                        break;

                    default:
                        pointsReward = 300;
                }
                break;
            default:
                if (npc.getObjectTemplate().getRace() == null) {
                    break;
                }

                if (npc.getObjectTemplate().getAi().equalsIgnoreCase("homing")
                    || npc.getObjectTemplate().getAi().equalsIgnoreCase("trap")
                    || npc.getObjectTemplate().getAi().equalsIgnoreCase("servant")
                    || npc.getObjectTemplate().getAi().equalsIgnoreCase("skillarea")
                    || npc.getObjectTemplate().getAi().equalsIgnoreCase("dummy")
                    || npc.getObjectTemplate().getAi().equalsIgnoreCase("groupgate")) {
                    break;
                }

                switch (npc.getObjectTemplate().getRace().getRaceId()) {
                    case 22: // UNDEAD
                        pointsReward = 12;
                        break;
                    case 9: // BROWNIE
                        pointsReward = 18;
                        break;
                    case 6: // LIZARDMAN
                        pointsReward = 24;
                        break;
                    case 8: // NAGA
                    case 18: // DRAGON
                    case 24: // MAGICALnpc
                        pointsReward = 30;
                        break;
                    default:
                        pointsReward = 11;
                        break;
                }
        }

        // Special npcs
        switch (npc.getObjectTemplate().getTemplateId()) {
			// Dukaki 
				case 215433:
				case 214878:
                pointsReward = 173;
                break;            
			// Drana
            case 700520:
                pointsReward = 52;
                break;
            // Walls
            case 700518:
            case 700558:
                pointsReward = 157;
                break;
            // Mutated Fungie
            case 214885:
                pointsReward = 21;
                break;
            // Named1
            case 214841:
                pointsReward = 208;
                break;			
            case 215431:
                pointsReward = 164;
                break;
            // Named2
            case 214842:
                pointsReward = 357;
                break;		
            case 215432:
                pointsReward = 164;
                break;						
            case 215429:
            case 215430:
                pointsReward = 190;
                break;
            // Spasenie Izmuch Ska
            case 214871:
                pointsReward = 1241;
                break;
            case 215386:
                pointsReward = 409;
                break;
            case 215428:
                pointsReward = 208;
                break;
            // Marabata
            case 214849:
            case 214850:
            case 214851:
                pointsReward = 319;
                break;
            // Generators
            case 214895:
            case 214896:
                pointsReward = 377;
                break;
            case 214897:
                pointsReward = 330;
                break;				
            // Atmach
            case 214843:
                pointsReward = 456;
                break;
            // Boss
            case 214864:
            case 214880:
            case 214894:
            case 215387:
            case 215388:
            case 215389:
                pointsReward = 789;
                break;
            case 214904:
                pointsReward = 954;
                break;
        }
        PlayerGroup group = instance.getRegisteredGroup();
        if (group != null) {
            if (group.getLeaderObject().getAbyssRank().getRank().getId() >= 10) {
                pointsReward = Math.round(pointsReward * 1.1f);
            }
        }
        return pointsReward;
    }

    @Override
    public void onGather(Player player, Gatherable gatherable) {
        instanceReward.addGather();
        sendPacket(0, instanceId);
    }

    private void toScheduleMarbataController(final int npcId) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                Npc boss = null;
                switch (npcId) {
                    case 700443:
                    case 700444:
                    case 700442:
                        boss = getNpc(214850);
                        break;
                    case 700446:
                    case 700447:
                    case 700445:
                        boss = getNpc(214851);
                        break;
                    case 700440:
                    case 700441:
                    case 700439:
                        boss = getNpc(214849);
                }
                if (!isInstanceDestroyed && boss != null && !boss.getLifeStats().isAlreadyDead()) {
                    switch (npcId) {
                        case 700443:
                            spawn(npcId, 676.257019f, 319.649994f, 99.375000f, (byte) 4);
                            break;
                        case 700444:
                            spawn(npcId, 655.851013f, 292.710999f, 99.375000f, (byte) 90);
                            break;
                        case 700442:
                            spawn(npcId, 636.117981f, 325.536987f, 99.375000f, (byte) 49);
                            break;
                        case 700446:
                            spawn(npcId, 598.706000f, 345.978000f, 99.375000f, (byte) 98);
                            break;
                        case 700447:
                            spawn(npcId, 567.775024f, 366.207001f, 99.375000f, (byte) 59);
                            break;
                        case 700445:
                            spawn(npcId, 605.625000f, 380.479004f, 99.375000f, (byte) 14);
                            break;
                        case 700440:
                            spawn(npcId, 681.851013f, 408.625000f, 100.472000f, (byte) 13);
                            break;
                        case 700441:
                            spawn(npcId, 646.549988f, 406.088013f, 99.375000f, (byte) 49);
                            break;
                        case 700439:
                            spawn(npcId, 665.37400f, 372.75100f, 99.375000f, (byte) 90);
                            break;
                    }
                }
            }

        }, 28000);
    }
	
    private int getTime() {
        long result = System.currentTimeMillis() - startTime;
        if (result < 120000) {
            return (int) (120000 - result);
        } else if (result < 14520000) {
            return (int) (14400000 - (result - 120000));
        }
        return 0;
    }

    private int checkRank(int totalPoints) {
        int timeRemain = getTime();
        int rank = 0;
        if (timeRemain > 7200000 && totalPoints >= 17817) {
            spawn(215280, 1176f, 1227f, 145f, (byte) 14);
            rank = 1;
        } else if (timeRemain > 5400000 && totalPoints >= 15219) {
            spawn(215281, 1176f, 1227f, 145f, (byte) 14);
            rank = 2;
        } else if (timeRemain > 3600000 && totalPoints > 10913) {
            spawn(215282, 1176f, 1227f, 145f, (byte) 14);
            rank = 3;
        } else if (timeRemain > 1800000 && totalPoints > 6656) {
            spawn(215283, 1176f, 1227f, 145f, (byte) 14);
            rank = 4;
        } else if (timeRemain > 1) {
            spawn(215284, 1176f, 1227f, 145f, (byte) 14);
            rank = 5;
        } else {
            rank = 8;
        }
        spawn(700478, 298.24423f, 316.21954f, 133.29759f, (byte) 56);

        return rank;
    }
	
    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
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
        if (instanceTimer != null) {
            instanceTimer.cancel(false);
        }
        isInstanceDestroyed = true;
        doors.clear();
    }

    private void despawnNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            npc.getController().onDelete();
        }
    }

    @Override
    public void onExitInstance(Player player) {
        if (instanceReward.getInstanceScoreType().isEndProgress()) {
            TeleportService.moveToInstanceExit(player, mapId, player.getRace());
        }
    }

}
