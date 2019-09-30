/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.morheim;

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


@AIName("zugog")
public class ZugogAI2 extends AggressiveNpcAI2 {

    private int curentPercent = 100;
	private final List<Integer> percents = new ArrayList<>();
	private Future<?> random;

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
		cancelRandomTask();
    }
	
    @Override
    protected void handleDespawned() {
		percents.clear();
        super.handleDespawned();
		cancelRandomTask();
    }
	
    @Override
    public void handleDied() {
        super.handleDied();
		percents.clear();
		cancelRandomTask();
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 90, 40);
    }
	
	private synchronized void checkPercentage(int hpPercentage) {
        curentPercent = hpPercentage;
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 90:
						startRandomSkillTask();
                        break;
                    case 40:
						startHealTask();
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
                    switch (Rnd.get(1, 2)) {
                        case 1:
								useSkill(16837);
                            break;
                        case 2:
								useSkill(16915);
                            break;
                    } 
                }
            }

        }, 15000, 15000);
    }
	
	private void startHealTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
					useSkill(16912);
                }
            }

        }, 20000);
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getTarget()).useSkill();
	}
	
	private void cancelRandomTask() {
        if (random != null && !random.isCancelled()) {
            random.cancel(true);
        }
    }
}
