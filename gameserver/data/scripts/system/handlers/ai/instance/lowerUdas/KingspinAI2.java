/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.lowerUdas;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.skillengine.SkillEngine;


@AIName("kingspin")
public class KingspinAI2 extends AggressiveNpcAI2 {

    private int curentPercent = 100;
	private final List<Integer> percents = new ArrayList<>();
	private Future<?> random;
	private Future<?> damage;

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
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 75);
    }
	
    @Override
    public void handleBackHome() {
        super.handleBackHome();
		addPercent();
		curentPercent = 100;
        cancelRandomTask();
		cancelDamageTask();
    }
	
    @Override
    protected void handleDespawned() {
        super.handleDespawned();
		percents.clear();
        cancelRandomTask();
		cancelDamageTask();
    }
	
    @Override
    public void handleDied() {
        super.handleDied();
		percents.clear();
        cancelRandomTask();
		cancelDamageTask();
    }
	
	private synchronized void checkPercentage(int hpPercentage) {
        curentPercent = hpPercentage;
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 75:
						startBasicSkillTask();
						startSkillTask();
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }
	
    private void startSkillTask() {
        random = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelRandomTask();
                } else {
                    switch (Rnd.get(1, 4)) {
                        case 1:
								useSkill(18606);
                            break;
                        case 2:
								useSkill(18608);
                            break;
                        case 3:
								useSkill(18610);
                            break;
						case 4:
								useSkill(18611);
                            break;
                    } 
                }
            }

        }, 10000, 10000);
    }
	
    private void startBasicSkillTask() {
        damage = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelDamageTask();
                } else {
					cancelRandomTask();
					useSkill(18609);
					startSkillTask();
                }
            }

        }, 60000, 60000);
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 53, getTarget()).useSkill();
	}
	
    private void cancelRandomTask() {
        if (random != null && !random.isCancelled()) {
            random.cancel(true);
        }
    }
	
    private void cancelDamageTask() {
        if (damage != null && !damage.isCancelled()) {
            damage.cancel(true);
        }
    }
}
