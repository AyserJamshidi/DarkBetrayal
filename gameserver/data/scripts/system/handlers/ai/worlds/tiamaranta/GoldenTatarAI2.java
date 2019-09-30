/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.tiamaranta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.world.knownlist.Visitor;

@AIName("golden_tatar")
public class GoldenTatarAI2 extends AggressiveNpcAI2 {

    private final List<Integer> percents = new ArrayList<>();
    private Future<?> phaseTask;
    private Future<?> thinkTask;
    private Future<?> specialSkillTask;
	private int stage = 0;
    private boolean think = true;
    private int curentPercent = 100;

    @Override
    public boolean canThink() {
        return think;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
			stage++;
			if (stage == 1) {
				startSpecialSkillTask();
				sendMsg(1500499);
			}
        checkPercentage(getLifeStats().getHpPercentage());
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 90, 80, 75, 70, 67, 63, 59, 53, 47, 44, 39, 35, 30, 26, 23, 16, 11, 6);
    }

    private synchronized void checkPercentage(int hpPercentage) {
        curentPercent = hpPercentage;
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 90:
                    case 70:
                    case 44:
                    case 23:
                        cancelspecialSkillTask();
                        think = false;
                        EmoteManager.emoteStopAttacking(getOwner());
						useSkill(20483);
                        sendMsg(1500501);
                        ThreadPoolManager.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                if (!isAlreadyDead()) {
									useSkill(20216);
                                    startThinkTask();
                                    for (int i = 0; i < 6; i++) {
                                        rndSpawn(282746);
                                    }
                                }
                            }

                        }, 3500);
                        break;
                    case 80:
                    case 75:
                    case 67:
                    case 63:
                    case 59:
                    case 53:
                    case 47:
                    case 39:
                    case 35:
                    case 30:
                    case 26:
                    case 16:
                    case 11:
                    case 6:
                        startPhaseTask();
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }

    private void startThinkTask() {
        thinkTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    think = true;
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
                        cancelspecialSkillTask();
                        startSpecialSkillTask();
                    }
                }
            }

        }, 20000);
    }

    private void startPhaseTask() {
		useSkill(20481);
        sendMsg(1500500);
        phaseTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    deleteNpcs(282743);
                    for (int i = 0; i < 6; i++) {
                        rndSpawn(282743);
                    }
                    cancelspecialSkillTask();
                    startSpecialSkillTask();
                }
            }

        }, 4000);
    }

    private void startSpecialSkillTask() {
		specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				useSkill(20223);
				specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						useSkill(20224);
						specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								useSkill(20224);
								if (curentPercent <= 63) {
									specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

										@Override
										public void run() {
											useSkill(20480);
											sendMsg(1500502);
											ThreadPoolManager.getInstance().schedule(new Runnable() {

												@Override
												public void run() {
													if (!isAlreadyDead()) {
														deleteNpcs(282744);
														rndSpawn(282744);
														rndSpawn(282744);
													}
												}

											}, 2000);
										}

									}, 21000);
								}
							}

						}, 3500);
					}

				}, 1500);
			}

		}, 12000);
    }
	
	private void useSkill(int skillId){
		if (!isAlreadyDead()) {
			SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useSkill();
		}
	}
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), false, 0, 0);
    }

    private void deleteNpcs(final int npcId) {
        if (getKnownList() != null) {
            getKnownList().doOnAllNpcs(new Visitor<Npc>() {

                @Override
                public void visit(Npc npc) {
                    if (npc.getNpcId() == npcId) {
                        CreatureActions.delete(npc);
                    }
                }

            });
        }
    }

    private void cancelspecialSkillTask() {
        if (specialSkillTask != null && !specialSkillTask.isDone()) {
            specialSkillTask.cancel(true);
        }
    }

    private void cancelPhaseTask() {
        if (phaseTask != null && !phaseTask.isDone()) {
            phaseTask.cancel(true);
        }
    }

    private void cancelThinkTask() {
        if (thinkTask != null && !thinkTask.isDone()) {
            thinkTask.cancel(true);
        }
    }

    private void rndSpawn(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(4, 18);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
		spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), p.getH());
    }

    @Override
    protected void handleDespawned() {
        cancelspecialSkillTask();
        cancelThinkTask();
        cancelPhaseTask();
        percents.clear();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        sendMsg(1500503);
        cancelspecialSkillTask();
        cancelThinkTask();
        cancelPhaseTask();
        percents.clear();
        deleteNpcs(282746);
        deleteNpcs(282743);
        deleteNpcs(282744);
        super.handleDied();
    }

    @Override
    protected void handleBackHome() {
        think = true;
        cancelspecialSkillTask();
        cancelThinkTask();
        cancelPhaseTask();
        addPercent();
        curentPercent = 100;
        deleteNpcs(282746);
        deleteNpcs(282743);
        deleteNpcs(282744);
        super.handleBackHome();
		stage = 0;
    }
}
