/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2014, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.raksang;

import java.util.concurrent.Future;

import ai.NoActionAI2;
import com.ne.gs.ai2.AIName;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.commons.network.util.ThreadPoolManager;

@AIName("blaze")
public class BlazeAI2 extends NoActionAI2 {

    private Future<?> skillTask;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        startpower();
    }

    private void startpower() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				useSkill(19927);
			}	
			
		}, 2000, 3500);	
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getOwner()).useSkill();
	}
	
	private void cancelskillTask() {
        if (skillTask != null && !skillTask.isCancelled()) {
            skillTask.cancel(true);
        }
    }

    @Override
    protected void handleDied() {
        cancelskillTask();
        super.handleDied();
    }

    @Override
    protected void handleDespawned() {
        cancelskillTask();
        super.handleDespawned();
    }

}
