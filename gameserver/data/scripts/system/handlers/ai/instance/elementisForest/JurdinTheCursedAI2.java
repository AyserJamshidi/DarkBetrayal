/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.elementisForest;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.SummonerAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.spawnengine.SpawnEngine;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.services.NpcShoutsService;

@AIName("jurdin")
public class JurdinTheCursedAI2 extends SummonerAI2 {

    private boolean isStart;
    private Future<?> task;
    private int curentPercent = 100;
	private final List<Integer> percents = new ArrayList<>();
    private final AtomicBoolean isHome = new AtomicBoolean(true);

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            sendMsg(1500449);
        }
        if (getLifeStats().getHpPercentage() <= 90 && !isStart) {
            isStart = true;
            startTask();
        }
		 checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDespawned() {
        cancelTask();
		percents.clear();
        super.handleDespawned();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(282190));
				deleteNpcs(instance.getNpcs(282191));
				deleteNpcs(instance.getNpcs(282194));
				deleteNpcs(instance.getNpcs(282195));
				deleteNpcs(instance.getNpcs(282193));
				deleteNpcs(instance.getNpcs(282440));
				deleteNpcs(instance.getNpcs(282197));
				deleteNpcs(instance.getNpcs(282201));
            }
        }
    }

    @Override
    public void handleBackHome() {
        super.handleBackHome();
        isHome.set(true);
        isStart = false;
        cancelTask();
		addPercent();
		curentPercent = 100;
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(282190));
				deleteNpcs(instance.getNpcs(282191));
				deleteNpcs(instance.getNpcs(282194));
				deleteNpcs(instance.getNpcs(282195));
				deleteNpcs(instance.getNpcs(282193));
				deleteNpcs(instance.getNpcs(282440));
				deleteNpcs(instance.getNpcs(282197));
				deleteNpcs(instance.getNpcs(282201));
            }
        }
    }

    @Override
    public void handleDied() {
        super.handleDied();
        sendMsg(1500450);
        cancelTask();
		percents.clear();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(282190));
				deleteNpcs(instance.getNpcs(282191));
				deleteNpcs(instance.getNpcs(282194));
				deleteNpcs(instance.getNpcs(282195));
				deleteNpcs(instance.getNpcs(282193));
				deleteNpcs(instance.getNpcs(282440));
				deleteNpcs(instance.getNpcs(282197));
				deleteNpcs(instance.getNpcs(282201));
            }
        }
    }
	
    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 80, 60, 40, 20);
    }

    private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
	
	private synchronized void checkPercentage(int hpPercentage) {
        curentPercent = hpPercentage;
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 80:
						randomSpawnHelpers(282190);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								randomSpawnHelpers(282190);
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									public void run() {
										randomSpawnHelpers(282190);
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											public void run() {
												randomSpawnHelpers(282190);
												ThreadPoolManager.getInstance().schedule(new Runnable() {
													public void run() {
														randomSpawnHelpers2(282191);
													}
												}, 4000);
											}
										}, 4000);
									}
								}, 4000);
							}
						}, 4000);
                        break;
                    case 60:
						randomSpawnHelpers(282194);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								randomSpawnHelpers(282194);
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									public void run() {
										randomSpawnHelpers(282194);
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											public void run() {
												randomSpawnHelpers(282194);
												ThreadPoolManager.getInstance().schedule(new Runnable() {
													public void run() {
														randomSpawnHelpers2(282195);
													}
												}, 4000);
											}
										}, 4000);
									}
								}, 4000);
							}
						}, 4000);
                        break;
                    case 40:
						randomSpawnHelpers(282190);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								randomSpawnHelpers(282190);
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									public void run() {
										randomSpawnHelpers(282190);
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											public void run() {
												randomSpawnHelpers(282190);
												ThreadPoolManager.getInstance().schedule(new Runnable() {
													public void run() {
														randomSpawnHelpers2(282193);
													}
												}, 4000);
											}
										}, 4000);
									}
								}, 4000);
							}
						}, 4000);
                        break;
                    case 20:
						randomSpawnHelpers(282195);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								randomSpawnHelpers(282195);
								ThreadPoolManager.getInstance().schedule(new Runnable() {
									public void run() {
										randomSpawnHelpers(282195);
										ThreadPoolManager.getInstance().schedule(new Runnable() {
											public void run() {
												randomSpawnHelpers(282195);
												ThreadPoolManager.getInstance().schedule(new Runnable() {
													public void run() {
														randomSpawnHelpers2(282197);
													}
												}, 4000);
											}
										}, 4000);
									}
								}, 4000);
							}
						}, 4000);
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }
	
    private void startTask() {
        task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelTask();
                } else {
                    spawnShadows();
                }
            }
        }, 0, 60000);
    }

    private void spawnShadows() {
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(282440));
            }
        }
        spawnFlowers();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead() || !isStart) {
                    return;
                }
                WorldPosition p = getPosition();
                if (p != null) {
                    WorldMapInstance instance = p.getWorldMapInstance();
                    if (instance != null) {
                        for (int i = 0; i < 6; i++) {
                            SpawnTemplate temp = rndSpawnInRange(282201, 10);
                            VisibleObject o = SpawnEngine.spawnObject(temp, getPosition().getInstanceId());
                            addHelpersSpawn(o.getObjectId());
                        }
                    }
                }
            }
        }, 5000);
    }

    private void cancelTask() {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }
	
	private void randomSpawnHelpers2(int npcId) {
		switch (Rnd.get(1, 3)) {
			case 1:
				attackPlayer((Npc) spawn(npcId, 450 + Rnd.get(-7, 7), 774 + Rnd.get(-7, 7), 134f, (byte) 0));
				break;
			case 2:
				attackPlayer((Npc) spawn(npcId, 507 + Rnd.get(-7, 7), 786 + Rnd.get(-7, 7), 133f, (byte) 0));
				break;
			case 3:
				attackPlayer((Npc) spawn(npcId, 458 + Rnd.get(-7, 7), 834 + Rnd.get(-7, 7), 135f, (byte) 0));
				break;
		}
    }

    private void randomSpawnHelpers(int npcId) {
		switch (Rnd.get(1, 3)) {
			case 1:
				attackPlayer((Npc) spawn(npcId, 450 + Rnd.get(-7, 7), 774 + Rnd.get(-7, 7), 134f, (byte) 0));
				attackPlayer((Npc) spawn(npcId, 450 + Rnd.get(-7, 7), 774 + Rnd.get(-7, 7), 134f, (byte) 0));
				attackPlayer((Npc) spawn(npcId, 450 + Rnd.get(-7, 7), 774 + Rnd.get(-7, 7), 134f, (byte) 0));
				break;
			case 2:
				attackPlayer((Npc) spawn(npcId, 507 + Rnd.get(-7, 7), 786 + Rnd.get(-7, 7), 133f, (byte) 0));
				attackPlayer((Npc) spawn(npcId, 507 + Rnd.get(-7, 7), 786 + Rnd.get(-7, 7), 133f, (byte) 0));
				attackPlayer((Npc) spawn(npcId, 507 + Rnd.get(-7, 7), 786 + Rnd.get(-7, 7), 133f, (byte) 0));
				break;
			case 3:
				attackPlayer((Npc) spawn(npcId, 458 + Rnd.get(-7, 7), 834 + Rnd.get(-7, 7), 135f, (byte) 0));
				attackPlayer((Npc) spawn(npcId, 458 + Rnd.get(-7, 7), 834 + Rnd.get(-7, 7), 135f, (byte) 0));
				attackPlayer((Npc) spawn(npcId, 458 + Rnd.get(-7, 7), 834 + Rnd.get(-7, 7), 135f, (byte) 0));
				break;
		}
    }
	
    private void attackPlayer(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                npc.setTarget(getTarget());
                ((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
                npc.setState(1);
                npc.getMoveController().moveToTargetObject();
                PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
            }
        }, 1000);
    }

    private void spawnFlowers() {
        WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                spawn(282440, 460.795f, 801.471f, 130.759f, (byte) 0);
                spawn(282440, 485.118f, 790.683f, 129.668f, (byte) 0);
                spawn(282440, 474.227f, 777.928f, 128.875f, (byte) 0);
                spawn(282440, 475.009f, 783.226f, 128.875f, (byte) 0);
                spawn(282440, 459.194f, 797.047f, 130.491f, (byte) 0);
                spawn(282440, 484.584f, 791.999f, 129.750f, (byte) 0);
                spawn(282440, 485.502f, 803.050f, 130.472f, (byte) 0);
                spawn(282440, 486.006f, 804.645f, 130.540f, (byte) 0);
                spawn(282440, 412.354f, 800.439f, 131.176f, (byte) 0);
                spawn(282440, 474.676f, 816.744f, 131.281f, (byte) 0);
                spawn(282440, 412.354f, 800.439f, 131.176f, (byte) 0);
                spawn(282440, 467.355f, 811.444f, 131.261f, (byte) 0);
                spawn(282440, 463.336f, 798.282f, 130.309f, (byte) 0);
            }
        }
    }
	
    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

}
