/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2014, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.tiamaranta.ativasCristalline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import ai.AggressiveNpcAI2;
import com.ne.commons.utils.Rnd;
import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.stats.container.NpcGameStats;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.world.knownlist.Visitor;

@AIName("ativascristalline")
public class AtivasCristallineAI2 extends AggressiveNpcAI2 {

    private boolean think = true;
    private Future<?> skillStart;
	private Future<?> specialSkillTask;
    private List<Integer> percents = new ArrayList<>();
	private int stage = 0;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 99) {
			stage++;
			if (stage == 1) {
				sendMsg(1500504);
				startBasicSkillTask();
			}
        }
    }
	
	private void startBasicSkillTask() {
        skillStart = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelTask();
                } else {
                    switch (Rnd.get(1, 2)) {
                        case 1:
								think = false;
								EmoteManager.emoteStopAttacking(getOwner());
								SmashCrystal();
								startThinkTask();
								sendMsg(1500505);
                            break;
                        case 2:
								sendMsg(1500506);
								BlueCrystal();
                            break;
                    } 
                }
            }

        }, 10000, 40000);
    }

    private void startThinkTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

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
                    }
                }
            }

        }, 11000);
    }
	
    private void startThinkTaskTwo() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

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
                    }
                }
            }

        }, 23000);
    }
	
	private void DrainCrystal() {
		useSkill(20209);
		specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			public void run() {
				deleteNpcs(282741);
				SmashCrystal();
			}
        }, 12000);
    }

    private void SmashCrystal() {
		useSkill(20210);
		specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			public void run() {
				useSkill(20211);
				specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
					public void run() {
						useSkill(20211);
						specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								useSkill(20211);
							}
						}, 2200);
					}
				}, 2200);	
			}
        }, 5200);
    }
	
	private void BlueCrystal() {
		useSkill(20486);
		garnetKomad();
		specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			public void run() {
				useSkill(20202);
				specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
					public void run() {
						think = false;
						EmoteManager.emoteStopAttacking(getOwner());
						DrainCrystal();
						startThinkTaskTwo();
					}
				}, 6000);
			}
        }, 1200);
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useSkill();
	}
	
    private void cancelTask() {
        if (skillStart != null && !skillStart.isCancelled()) {
            skillStart.cancel(true);
        }
    }
	
	private void cancelSpecialSkillTask() {
        if (specialSkillTask != null && !specialSkillTask.isCancelled()) {
            specialSkillTask.cancel(true);
        }
    }

    private void garnetKomad() {
		int size = getPosition().getWorldMapInstance().getNpcs(282741).size();
		for (int i = 0; i < 6; i++) {
			if (size >= 30) {
				break;
			}
			size++;
			rndSpawn(282741);
		}
    }

	private void rndSpawn(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(6, 15);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
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
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

    @Override
    public void handleDied() {
        super.handleDied();
        cancelTask();
		cancelSpecialSkillTask();
		stage = 0;
		deleteNpcs(282741);
		sendMsg(1500507);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        cancelTask();
		cancelSpecialSkillTask();
		stage = 0;
		deleteNpcs(282741);
    }
	
		
    @Override
    public boolean canThink() {
        return think;
    }
}
