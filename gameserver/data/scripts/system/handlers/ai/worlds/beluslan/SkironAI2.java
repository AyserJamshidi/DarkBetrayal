/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.beluslan;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;


@AIName("skiron")
public class SkironAI2 extends AggressiveNpcAI2 {

    private int curentPercent = 100;
	private final List<Integer> percents = new ArrayList<>();
	private Future<?> random;
	private Future<?> basicSkillTask;

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
		 checkPercentage(getLifeStats().getHpPercentage());
    }
	
    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }
	
    @Override
    public void handleBackHome() {
        super.handleBackHome();
		addPercent();
		curentPercent = 100;
		cancelSkillTask();
		cancelRandomTask();
    }
	
    @Override
    protected void handleDespawned() {
		percents.clear();
        super.handleDespawned();
		cancelSkillTask();
		cancelRandomTask();
    }
	
    @Override
    public void handleDied() {
        super.handleDied();
		percents.clear();
		cancelSkillTask();
		cancelRandomTask();
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 90, 25);
    }
	
	private synchronized void checkPercentage(int hpPercentage) {
        curentPercent = hpPercentage;
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 90:
						startRandomSkillTask();
                        break;
                    case 25:
						cancelRandomTask();
						startSkillTask();
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }
	
	private void startRandomSkillTask() {
        random = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelRandomTask();
                } else {
                    switch (Rnd.get(1, 3)) {
                        case 1:
								useSkill(17896);
                            break;
                        case 2:
								useSkill(17895);
                            break;
                        case 3:
								useSkill(17830);
                            break;
                    } 
                }
            }

        }, 15000, 15000);
    }
	
	private void startSkillTask() {
        basicSkillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelSkillTask();
                } else {
					useSkill(17830);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							useSkill(17896);
						}
					}, 1500);
                }
            }

        }, 15000, 15000);
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getTarget()).useSkill();
	}
	
	private void cancelRandomTask() {
        if (random != null && !random.isCancelled()) {
            random.cancel(true);
        }
    }

    private void cancelSkillTask() {
        if (basicSkillTask != null && !basicSkillTask.isCancelled()) {
            basicSkillTask.cancel(true);
        }
    }
}
