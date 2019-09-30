/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2014, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.sarpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import com.ne.commons.network.util.ThreadPoolManager;

import ai.AggressiveNpcAI2;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.services.NpcShoutsService;


@AIName("debarim")
public class DebarimTheOmnipotentAI2 extends AggressiveNpcAI2 {

	private boolean think = true;
    private int start = 0;
    private Future<?> skillStart;
    private Future<?> skillAoe;
    private Future<?> skillCircle;
    protected List<Integer> percents = new ArrayList<>();

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
		start++;
			if (start == 1) {
				getPosition().getWorldMapInstance().getDoors().get(480).setOpen(true);
				sendMsg(1500320);
			}
    }

    @Override
    public void handleCreatureAggro(Creature creature) {
        super.handleCreatureAggro(creature);
    }

    private void checkPercentage(int hpPercentage) {
        for (Iterator<Integer> it = percents.iterator(); it.hasNext(); ) {
            int percent = it.next();
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 99:
						startAttack();
                        startSkill();
                        break;
                    case 75:
                        cancelTask();
                        startAoe();
						useSkill(20047);
                        break;
                    case 50:
                        cancelAoe();
                        startSkill();
						useSkill(20047);
                        break;
                    case 25:
                        skillstartCircle();
						useSkill(20047);
                        break;
                }
                it.remove();
                break;
            }
        }
    }
	
	private void startAttack() {
		useSkill(20005);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            public void run() {
				getPosition().getWorldMapInstance().getDoors().get(860).setOpen(true);
			}
        }, 3500);
    }

    private void startSkill() {
        skillStart = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				}else {
					switch (Rnd.get(1, 2)) {
							case 1:
									useSkill(20002);
									sendMsg(1500321);
								break;
							case 2:
									useSkill(20000);
									sendMsg(1500323);
								break;
					} 
				}
			}
        }, 10000, 12000);
    }

    private void startAoe() {
        skillAoe = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				if (isAlreadyDead()) {
					cancelAoe();
				}else {
					skillstartAoe();
				}
			}
        }, 10000, 24000);
    }

    private void skillstartAoe() {
		think = false;
		EmoteManager.emoteStopAttacking(getOwner());
		startThinkTask();
		sendMsg(1500322);
		SkillEngine.getInstance().getSkill(getOwner(), 20003, 55, getOwner()).useNoAnimationSkill();
        ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
            public void run() {
				useSkill(20004);
				sendMsg(1500324);
			}
        }, 6500);
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
                        getOwner().getGameStats().renewLastAttackTime();
                        getOwner().getGameStats().renewLastAttackedTime();
                        getOwner().getGameStats().renewLastChangeTargetTime();
                        getOwner().getGameStats().renewLastSkillTime();
                        setStateIfNot(AIState.FIGHT);
						EmoteManager.emoteStartAttacking(getOwner());
						think();
                    }
                }
            }

        }, 22000);
    }

    private void skillstartCircle() {
        skillCircle = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				if (isAlreadyDead()) {
					cancelCircle();
				}else {
					circleSkill();
				}
			}
        }, 10000, 30000);
    }

    private void circleSkill() {
        useSkill(20005);
        ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
            public void run() {
				despawnNpcs(282612);
				rndspawn();
				}
        }, 3000);
    }

    private void rndspawn() {
		switch (Rnd.get(1, 4)) {
			case 1:
				spawn(282612, 697.12497f, 2962.17401f, 407.625f, (byte) 0);
				spawn(282612, 687.12497f, 2955.17401f, 407.625f, (byte) 0);
				break;
			case 2:
				spawn(282612, 679.12497f, 2970.17401f, 407.625f, (byte) 0);
				spawn(282612, 674.12497f, 2961.17401f, 407.625f, (byte) 0);
				break;
			case 3:
				spawn(282612, 688.12497f, 2967.17401f, 407.625f, (byte) 0);
				spawn(282612, 670.12497f, 2952.17401f, 407.625f, (byte) 0);
				break;
			case 4:
				spawn(282612, 688.12497f, 2967.17401f, 407.625f, (byte) 0);
				spawn(282612, 688.12497f, 2951.17401f, 407.625f, (byte) 0);
				break;
		} 
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getTarget()).useSkill();
	}
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
	
	private void despawnNpcs(int npcId) {
        List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(npcId);
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 99, 75, 50, 25);
    }
	
	private void cancelTask() {
        if (skillStart != null && !skillStart.isCancelled()) {
            skillStart.cancel(true);
        }
    }
	
	private void cancelAoe() {
        if (skillAoe != null && !skillAoe.isCancelled()) {
            skillAoe.cancel(true);
        }
    }
	
	private void cancelCircle() {
        if (skillCircle != null && !skillCircle.isCancelled()) {
            skillCircle.cancel(true);
        }
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }

    @Override
    public void handleDied() {
        super.handleDied();
        getPosition().getWorldMapInstance().getDoors().get(480).setOpen(false);
        getPosition().getWorldMapInstance().getDoors().get(860).setOpen(false);
        despawnNpcs(282612);
        cancelTask();
        cancelAoe();
        cancelCircle();
		sendMsg(1500324);
    }

    @Override
    public void handleBackHome() {
        super.handleBackHome();
        getPosition().getWorldMapInstance().getDoors().get(480).setOpen(false);
        getPosition().getWorldMapInstance().getDoors().get(860).setOpen(false);
        despawnNpcs(282612);
        cancelTask();
        cancelAoe();
        cancelCircle();
		start = 0;
		addPercent();
		sendMsg(1500510);
    }
	
    @Override
    public boolean canThink() {
        return think;
    }
}
