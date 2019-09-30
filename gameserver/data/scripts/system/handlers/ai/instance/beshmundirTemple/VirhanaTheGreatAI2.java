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
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.services.NpcShoutsService;

@AIName("virhana")
public class VirhanaTheGreatAI2 extends AggressiveNpcAI2 {

    private boolean isStart;
    private int count;

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (!isStart) {
            isStart = true;
            scheduleRage();
			sendMsg(1500064);
        }
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        isStart = false;
    }

    private void scheduleRage() {
        if (isAlreadyDead() || !isStart) {
            return;
        }
        AI2Actions.useSkill(this, 19121);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				if (isAlreadyDead() || !isStart) {
					return;
				}
				sendMsg(1500065);
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						sendMsg(1500066);
						startRage();
					}

				}, 30000);
			}
		
		}, 30000);
    }

    private void startRage() {
        if (isAlreadyDead() || !isStart) {
            return;
        }
        if (count < 12) {
            AI2Actions.useSkill(this, 18897);
            count++;

            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    startRage();
                }

            }, 10000);
        } else { // restart after a douzen casts
            count = 0;
            scheduleRage();
        }
    }
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
}
