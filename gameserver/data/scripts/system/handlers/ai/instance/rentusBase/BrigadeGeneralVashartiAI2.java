/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.world.WorldMapInstance;

@AIName("brigade_general_vasharti")
public class BrigadeGeneralVashartiAI2 extends AggressiveNpcAI2 {

	protected List<Integer> percents = new ArrayList<Integer>();
    private final AtomicBoolean isHome = new AtomicBoolean(true);
    private boolean canThink = true;
	private Future<?> flameBuffTask;
	private Future<?> flameSmashTask;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            getPosition().getWorldMapInstance().getDoors().get(70).setOpen(false);
			startFlameBuffEvent();
			sendMsg(1500406);
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

	@Override
    public boolean canThink() {
        return canThink;
    }

	private synchronized void checkPercentage(int hpPercentage){
		for (Integer percent : percents){
			if (hpPercentage <= percent){
				switch(percent){
					case 85:
						nSpawn(283010);
						nSpawn(283002);
						Flame(this);
						start();
						break;
					case 70:
						nSpawn(283011);
						nSpawn(283003);
						Flame(this);
						start();
						break;
					case 55:
						nSpawn(283011);
						nSpawn(283004);
						Flame(this);
						start();
						break;
					case 40:
						nSpawn(283012);
						nSpawn(283004);
						Flame(this);
						start();
						break;
					case 20:
						nSpawn(283012);
						nSpawn(283006);
						Flame(this);
						start();
						break;

				}
				percents.remove(percent);
				break;
			}

		}
	}

	private void nSpawn(int npcId) {
		sendMsg(1500407);
        spawn(npcId, 188.16568f, 414.03534f, 260.75488f, (byte) 0);
    }

	private void start() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				final Npc buffNpc = (Npc) spawn(283007, 188.33f, 414.61f, 260.61f, (byte) 0);

				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						if (!buffNpc.getLifeStats().isAlreadyDead()) {
							SkillEngine.getInstance().getSkill(buffNpc, 20538, 60, buffNpc).useNoAnimationSkill();
							ThreadPoolManager.getInstance().schedule(new Runnable() {

								@Override
								public void run() {
									buffNpc.getController().onDelete();
								}

							}, 4000);
						}
					}

				}, 1000);

				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						cancelFlameSmashTask();
						cancelAirEvent();
						Creature creature = getAggroList().getMostHated();
						if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
							setStateIfNot(AIState.FIGHT);
							think();
						} else {
							getMoveController().abortMove();
							getOwner().setTarget(creature);
							getOwner().getGameStats().renewLastAttackTime();
							getOwner().getGameStats().renewLastAttackedTime();
							getOwner().getGameStats().renewLastChangeTargetTime();
							getOwner().getGameStats().renewLastSkillTime();
							setStateIfNot(AIState.FIGHT);
							handleMoveValidate();
							startFlameBuffEvent();
						}
					}

				}, 47000);

			}
		}, 4000);
	}

    private void Flame(final NpcAI2 ai) {
		SkillEngine.getInstance().getSkill(getOwner(), 20532, 60, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), 20533, 60, getOwner()).useNoAnimationSkill();
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							sendMsg(1500408);
							SkillEngine.getInstance().getSkill(getOwner(), 20534, 60, getOwner()).useNoAnimationSkill();
							canThink = false;
							EmoteManager.emoteStopAttacking(getOwner());
							getSpawnTemplate().setWalkerId("30028000012");
							WalkManager.startWalking(ai);
							getOwner().setState(1);
							PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
							cancelFlameBuffEvent();

							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									getSpawnTemplate().setWalkerId(null);
									WalkManager.stopWalking(ai);
									startFlameSmashEvent();
									sendMsg(1500409);
								}
							}, 7000);
						}

					}, 1500);
			}

		}, 1500);
	}

    private void startFlameBuffEvent() {
		flameBuffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelFlameBuffEvent();
                } else {
                    WorldMapInstance instance = getPosition().getWorldMapInstance();
					if (instance == null) {
						cancelFlameBuffEvent();
                    }
                    if (instance != null) {
                        SkillEngine.getInstance().getSkill(getOwner(), Rnd.get(0, 1) == 0 ? 20530 : 20531, 60, getOwner()).useNoAnimationSkill();
                        if (instance.getNpc(283000) == null && instance.getNpc(283001) == null) {
                            VisibleObject ice = spawn(283001, 205.280f, 410.53f, 261f, (byte) 56);
                            VisibleObject fire = spawn(283000, 171.330f, 417.57f, 261f, (byte) 116);
                            if (ice != null) {
                                useKissBuff((Npc) ice);
                            }
                            if (fire != null) {
                                useKissBuff((Npc) fire);
                            }
                        }
                    }
                }
            }
        }, 0, 60000);
    }

    private void useKissBuff(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (npc != null && !npc.getLifeStats().isAlreadyDead()) {
                    SkillEngine.getInstance().getSkill(npc, npc.getNpcId() == 283001 ? 19346 : 19345, 60, npc).useNoAnimationSkill();
                }
            }

        }, 1000);
    }

	private void startFlameSmashEvent() {

        flameSmashTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
				WorldMapInstance instance = getPosition().getWorldMapInstance();
                Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelFlameSmashTask();
                } else {
                    if (instance != null) {
						rndSpawn(283008);
						rndSpawn(283009);
						rndSpawn(283008);
						rndSpawn(283009);
						rndSpawn(283008);
						rndSpawn(283009);
						rndSpawn(283008);
						rndSpawn(283009);
						rndSpawn(283008);
						rndSpawn(283009);
						rndSpawn(283008);
						rndSpawn(283009);
                    }
                }
            }

        }, 3000, 3000);
    }

	private void rndSpawn(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(1, 32);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
    }

    private void cancelAirEvent() {
        canThink = true;
        if (!isAlreadyDead()) {
            getOwner().getEffectController().removeEffect(20534);
        }
        if (getPosition() != null) {
            WorldMapInstance instance = getPosition().getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(283002));
                deleteNpcs(instance.getNpcs(283003));
                deleteNpcs(instance.getNpcs(283004));
                deleteNpcs(instance.getNpcs(283006));
                deleteNpcs(instance.getNpcs(283007));
                deleteNpcs(instance.getNpcs(283010));
                deleteNpcs(instance.getNpcs(283011));
                deleteNpcs(instance.getNpcs(283012));
                deleteNpcs(instance.getNpcs(283000));
                deleteNpcs(instance.getNpcs(283001));
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

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{85, 70, 55, 40, 20});
	}

	private void cancelFlameSmashTask() {
		if (flameSmashTask != null) {
			flameSmashTask.cancel(true);
		}
	}

    private void cancelFlameBuffEvent() {
        if (flameBuffTask != null) {
			flameBuffTask.cancel(true);
        }
        if (!isAlreadyDead()) {
            SkillEngine.getInstance().getSkill(getOwner(), 20532, 60, getOwner()).useNoAnimationSkill();
        }
    }

	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

	@Override
	protected void handleSpawned() {
		addPercent();
		sendMsg(1500405);
		super.handleSpawned();
	}

    @Override
    protected void handleBackHome() {
		addPercent();
		canThink = true;
        isHome.set(true);
		cancelFlameBuffEvent();
		cancelFlameSmashTask();
		cancelAirEvent();
        getPosition().getWorldMapInstance().getDoors().get(70).setOpen(true);
        super.handleBackHome();
    }

    @Override
    protected void handleDied() {
		sendMsg(1500410);
		percents.clear();
		cancelFlameBuffEvent();
		cancelFlameSmashTask();
		cancelAirEvent();
		getPosition().getWorldMapInstance().getDoors().get(70).setOpen(true);
        super.handleDied();
    }

}
