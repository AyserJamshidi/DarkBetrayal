/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.padmarashkasCave;

import ai.AggressiveNpcAI2;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.skillengine.effect.AbnormalState;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

@AIName("padmarashka")
public class PadmarashkaAI2 extends AggressiveNpcAI2 {

    private boolean isStart = false;
    private boolean canThink = false;
	protected List<Integer> percents = new ArrayList<>();
	private Future<?> spawnGuard;
    private Future<?> mainSkillTask;
	private Future<?> startStage2;
	private Future<?> startStage3;
	private Future<?> skillStage5;
	private Future<?> finalStage;
	private int stage = 0;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        putToSleep();
		addPercent();
    }

    @Override
    protected void handleAttack(Creature creature) {
		checkDirection();
        if (!this.getEffectController().hasAbnormalEffect(19186) && !isStart) {
            wakeUp();
        }
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }
	

    @Override
    protected void handleCreatureAggro(Creature creature) {
        if (!this.getEffectController().hasAbnormalEffect(19186) && !isStart) {
            wakeUp();
        }
        super.handleCreatureAggro(creature);
    }

    private void wakeUp() {
        canThink = true;
        isStart = true;
        startMainSkillTask();
    }
	
    private void putToSleep() {
        SkillEngine.getInstance().getSkill(getOwner(), 19186, 55, getOwner()).useNoAnimationSkill();
        this.getEffectController().setAbnormal(AbnormalState.SLEEP.getId());
        canThink = false;
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getTarget()).useSkill();
	}
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
	
	private void scheduleSpawnEntrance() {
        spawnGuard = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1500105);
				getOwner().clearAttackedCount();
				int count = Rnd.get(2, 3);
				for (int i = 0; i < count; i++) {
					int npcId = 282792 + Rnd.get(5);
					attackPlayer((Npc) spawn(npcId, 546.465f + Rnd.get(-5, 5), 297.921f + Rnd.get(-5, 5), 67.1f, (byte) 96));
				}
			}
        }, 60000, 60000);
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
	
	private void rndSpawn(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(5, 25);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
    }
	
	private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 95, 85, 75, 60, 55, 50, 45, 40, 35, 30, 25, 10);
    }

    private void checkPercentage(int hpPercentage) {
        for (Iterator<Integer> it = percents.iterator(); it.hasNext(); ) {
            int percent = it.next();
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 95:
						scheduleSpawnEntrance();
                        break;
                    case 85:
						stage2();
						cancelTask();
                        break;
                    case 75:
						stage3();
						cancelStage2();
                        break;
                    case 60:
					case 55:
					case 50:
					case 45:
						NpcShoutsService.getInstance().sendMsg(getOwner(), 1400526);
						cancelStage3();
						stage4(this);
                        break;
					case 40:
					case 35:
					case 30:
						SkillEngine.getInstance().getSkill(getOwner(), 19199, 55, getOwner()).useSkill();
						NpcShoutsService.getInstance().sendMsg(getOwner(), 1500106);
						break;
                    case 25:
					case 20:
					case 15:
						stage5();
						NpcShoutsService.getInstance().sendMsg(getOwner(), 1401215);
                        break;
					case 10:
						finalStage();
                        break;
                }
                it.remove();
                break;
            }
        }
    }
	
	private void randomStandartSkill() {
		switch (Rnd.get(1, 3)) {
			case 1:
					useSkill(20401);
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1500107);
				break;
			case 2:
					useSkill(20093);
					NpcShoutsService.getInstance().sendMsg(getOwner(), 1500108);
				break;
			case 3:
					useSkill(20098);
				break;
		}
	}
	
	private void collapse() {
		switch (Rnd.get(1, 3)) {
				case 1:
						rndSpawn(282140);
						rndSpawn(282140);
					break;
				case 2:
						rndSpawn(282140);
					break;
				case 3:
						rndSpawn(282140);
						rndSpawn(282140);
						rndSpawn(282140);
					break;
		}
	}
	
	private void spawnRandomMobs() {
		switch (Rnd.get(1, 10)) {
			case 1:
				spawn(282613, 575.064f, 163.573f, 66.01f, (byte) 0);
				break;
			case 2:
				spawn(282613, 587.361f, 165.187f, 66.01f, (byte) 0);
				break;
			case 3:
				spawn(282613, 577.463f, 155.553f, 66.01f, (byte) 0);
				break;
			case 4:
				spawn(282613, 582.064f, 160.785f, 66.01f, (byte) 0);
				break;
			case 5:
				spawn(282613, 586.160f, 157.214f, 66.01f, (byte) 0);
				break;
			case 6:
				spawn(282613, 571.436f, 150.558f, 66.01f, (byte) 0);
				break;
			case 7:
				spawn(282613, 577.574f, 150.415f, 66.01f, (byte) 0);
				break;
			case 8:
				spawn(282613, 581.354f, 151.640f, 66.01f, (byte) 0);
				break;
			case 9:
				spawn(282613, 589.382f, 149.758f, 66.01f, (byte) 0);
				break;
			case 10:
				spawn(282613, 583.469f, 145.809f, 66.01f, (byte) 0);
				break;
		}
		if (getPosition().getWorldMapInstance().getNpc(218675) == null) {
			spawn(218675, 573.413f, 179.518f, 66.220f, (byte) 30);
			NpcShoutsService.getInstance().sendMsg(getOwner(), 1400712);
			
		}
		else if (getPosition().getWorldMapInstance().getNpc(218676) == null) {
			spawn(218676, 581.827f, 179.587f, 66.250f, (byte) 30);
			NpcShoutsService.getInstance().sendMsg(getOwner(), 1400712);
		}
		final WorldMapInstance instance = getPosition().getWorldMapInstance();
		final Npc egg = instance.getNpc(282613);
		SkillEngine.getInstance().getSkill(getOwner(), 18727, 55, egg).useNoAnimationSkill();	
	}
	
    private void startMainSkillTask() {
        mainSkillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				}else {
					randomStandartSkill();
				}
			}
        }, 5000, 25000);
    }

	private void stage2() {
		startStage2 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 19179, 55, getOwner()).useSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					public void run() {
						rndSpawn(282140);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								if (isAlreadyDead()) {
									cancelTask();
								}else {
									randomStandartSkill();
								}
							}
						}, 5000);
					}
				}, 5000);
			}
		}, 0, 25000);
	}
	
	private void stage3() {
		startStage3 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			public void run() {
				useSkill(18722);
				SkillEngine.getInstance().getSkill(getOwner(), 19179, 55, getOwner()).useSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {	

					public void run() {
						if (isAlreadyDead()) {
							cancelTask();
						}else { 
							collapse();
						}
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								randomStandartSkill();
							}
						}, 5000);
					}
				}, 5000);
			}
		}, 0, 25000);
	}
	
	private void stage4(final NpcAI2 ai) {
		useSkill(19183);
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500116);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			public void run() {
				//getSpawnTemplate().setWalkerId("3201500001");
				//WalkManager.startWalking(ai);
				//getOwner().setState(1);
				//PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					public void run() {
						SkillEngine.getInstance().getSkill(getOwner(), 19177, 55, getOwner()).useSkill();
						//getSpawnTemplate().setWalkerId(null);
						//WalkManager.stopWalking(ai);
						
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {// призыв яиц и воспитателей
								if (!isAlreadyDead()) {
									spawnRandomMobs();
								}
							}
						}, 1000);
					}
				}, 10000);
			}
		}, 2500);
	}
	
	private void stage5() {
		useSkill(18722);
		SkillEngine.getInstance().getSkill(getOwner(), 19179, 55, getOwner()).useNoAnimationSkill();
		skillStage5 = ThreadPoolManager.getInstance().schedule(new Runnable() {

			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				}else {
					collapse();
				}
				skillStage5 = ThreadPoolManager.getInstance().schedule(new Runnable() {
					
					public void run() {
						useSkill(19183);
						skillStage5 = ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								useSkill(19177);
									skillStage5 = ThreadPoolManager.getInstance().schedule(new Runnable() {
										public void run() {// призыв яиц и воспитателей
											if (!isAlreadyDead()) {
												spawnRandomMobs();
											}
											skillStage5 = ThreadPoolManager.getInstance().schedule(new Runnable() {
												public void run() {
													SkillEngine.getInstance().getSkill(getOwner(), 19179, 55, getOwner()).useNoAnimationSkill();
													skillStage5 = ThreadPoolManager.getInstance().schedule(new Runnable() {
														public void run() {
															if (isAlreadyDead()) {
																cancelTask();
															}else {
																collapse();
															}
														}
													}, 5000);
												}
											}, 2000);
										}
									}, 1000);
							}
						}, 2500);
					}
				}, 5000);	
			}
		}, 5000);
	}
	
	private void finalStage(){
		useSkill(20101);
		finalStage = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			public void run() {
				useSkill(18722);
				SkillEngine.getInstance().getSkill(getOwner(), 19179, 55, getOwner()).useNoAnimationSkill();
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					public void run() {
						rndSpawn(282140);
						rndSpawn(282140);
						rndSpawn(282140);
					}
				}, 5000);
			}
		}, 5000, 15000);
		
	}
	
	
	private void checkDirection(){
		List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281937);
		if(npcs != null){
			for (Npc npc : npcs) {
				if(MathUtil.getDistance(getOwner(), npc) <= 25){
					stage++;
					if (stage == 1) {
						isStart = false;
						cancelTask();
						cancelStage2();
						cancelStage3();
						cancelStage5();
						cancelFinalStage();
						cancelGuardTask();
						deSpawnNpcs();
						stage = 0;
						despawnNpcs(216580);
						spawn(216580, 565.413f, 241.518f, 66.9f, (byte) 43);
						spawnPadmaProtector();
					}
				}
			}
		}
	}
	
    private void spawnPadmaProtector() {
        spawn(218670, 533.107f, 247.685f, 66.761f, (byte) 45);
        spawn(218671, 581.092f, 265.049f, 66.843f, (byte) 15);
        spawn(218673, 547.838f, 264.624f, 66.875f, (byte) 45);
        spawn(218674, 595.748f, 248.045f, 66.250f, (byte) 15);
    }

    private void deSpawnNpcs() {
		despawnNpcs(282616);
        // Despawn Stage 3
        despawnNpcs(282140);
        // Despawn Stage 2
        despawnNpcs(282614);
        despawnNpcs(282712);
        despawnNpcs(282620);
        // Despawn Stage 1
        despawnNpcs(282613);
        despawnNpcs(218675);
        despawnNpcs(218676);
        despawnNpcs(282715);
        despawnNpcs(282716);
        // Despawn Entrance mobs
        despawnNpcs(282792);
        despawnNpcs(282793);
        despawnNpcs(282794);
        despawnNpcs(282795);
        despawnNpcs(282796);
        // Despawn Padma Protector
        despawnNpcs(218670);
        despawnNpcs(218671);
        despawnNpcs(218673);
        despawnNpcs(218674);
    }

    private void despawnNpcs(int npcId) {
        List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }
	
	private void cancelGuardTask() {
        if (spawnGuard != null && !spawnGuard.isCancelled()) {
            spawnGuard.cancel(true);
        }
    }

    private void cancelTask() {
        if (mainSkillTask != null && !mainSkillTask.isDone())
            mainSkillTask.cancel(true);
    }
	
	private void cancelStage2() {
        if (startStage2 != null && !startStage2.isCancelled()) {
            startStage2.cancel(true);
        }
    }

	private void cancelStage3() {
        if (startStage3 != null && !startStage3.isCancelled()) {
            startStage3.cancel(true);
        }
    }
	
	private void cancelStage5() {
        if (skillStage5 != null && !skillStage5.isCancelled()) {
            skillStage5.cancel(true);
        }
    }
	
	private void cancelFinalStage() {
        if (finalStage != null && !finalStage.isCancelled()) {
            finalStage.cancel(true);
        }
    }
	
    @Override
    protected void handleDespawned() {
        super.handleDespawned();
		isStart = false;
        cancelTask();
		cancelStage2();
		cancelStage3();
		cancelStage5();
		cancelFinalStage();
		cancelGuardTask();
        deSpawnNpcs();
		stage = 0;
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		addPercent();
        isStart = false;
        cancelTask();
		cancelStage2();
		cancelStage3();
		cancelStage5();
		cancelFinalStage();
		cancelGuardTask();
        deSpawnNpcs();
        putToSleep();
		spawnPadmaProtector();
    }

    @Override
    protected void handleDied() {
        super.handleDied();
		addPercent();
        isStart = false;
        cancelTask();
		cancelStage2();
		cancelStage3();
		cancelStage5();
		cancelFinalStage();
		cancelGuardTask();
        deSpawnNpcs();
		NpcShoutsService.getInstance().sendMsg(getOwner(), 1500112);
    }

    @Override
    public boolean canThink() {
        return canThink;
    }
}
