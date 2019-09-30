/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;

import java.util.concurrent.Future;

@AIName("ahbana")
public class AhbanaAI2 extends AggressiveNpcAI2 {

    private boolean isStart;
	private Future<?> skillTask;

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (!isStart) {
            isStart = true;
            startRage();
			sendMsg(1500045);
        }
    }
	
    @Override
    protected void handleDied() {
        super.handleDied();
		sendMsg(1500047);
		cancelSkillTask();
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        isStart = false;
		cancelSkillTask();
    }

    private void startRage() {
		skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), 18892, 55, getTarget()).useSkill();
					sendMsg(1500046);
                }

        }, 10000, 21000);
    }
	
	private void cancelSkillTask() {
        if (skillTask != null && !skillTask.isDone())
            skillTask.cancel(true);
    }
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
}
