/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2014, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.abyssal_splinter;

import java.util.concurrent.Future;

import ai.AggressiveNpcAI2;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldMapInstance;

@AIName("yamennes")
public class YamenesAI2 extends AggressiveNpcAI2 {

    private Future<?> portalTask;
	private Future<?> failTask;
	private int stage = 0;

    @Override
    protected void handleSpawned() {
		sendMsg(1500005);
        super.handleSpawned();
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        stage++;
		if (stage == 1) {
            startTasks();
        }
    }

    private void startTasks() {
        failTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			public void run() {
				if (isAlreadyDead()) {
					EmoteManager.emoteStopAttacking(getOwner());
					SkillEngine.getInstance().getSkill(getOwner(), 19098, 55, getOwner()).useSkill();
					AI2Actions.deleteOwner(YamenesAI2.this);
				}
			}
        }, 700000);

        portalTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			public void run() {
				if (isAlreadyDead()) {
					cancelTask();
				} else {
				despawn(282131);
				despawn(282015);
				despawn(282014);
				switch (Rnd.get(1, 2)) {
					case 1:
					spawn(282014, 303.69f, 736.35f, 198.7f, (byte) 0);
					spawn(282015, 335.19f, 708.92f, 198.9f, (byte) 35);
					spawn(282131, 360.23f, 741.07f, 198.7f, (byte) 0);
					sendMsg(1400637);
					sendMsg(1500008);
						break;
					case 2:
					spawn(282014, 288.10f, 741.95f, 216.81f, (byte) 3);
					spawn(282015, 375.05f, 750.67f, 216.82f, (byte) 59);
					spawn(282131, 341.33f, 699.38f, 216.86f, (byte) 59);
					sendMsg(1400637);
					sendMsg(1500008);
						break;
				}
				
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					
					public void run() {
						WorldMapInstance instance = getPosition().getWorldMapInstance();
						despawn(282107);
						Npc boss = getOwner();
						EmoteManager.emoteStopAttacking(getOwner());
						SkillEngine.getInstance().getSkill(boss, 19282, 55, getOwner()).useSkill();
						spawn(282107, boss.getX() + 10, boss.getY() - 10, boss.getZ(), (byte) 0);
						spawn(282107, boss.getX() - 10, boss.getY() + 10, boss.getZ(), (byte) 0);
						spawn(282107, boss.getX() + 10, boss.getY() + 10, boss.getZ(), (byte) 0);
						boss.clearAttackedCount();
						sendMsg(1400729);
					}
				}, 3000);
				}
			}
        }, 60000, 60000);
    }
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }


    private void cancelTask() {
        if (portalTask != null && !portalTask.isDone()) {
            portalTask.cancel(true);
        }
    }
	
	private void cancelFailTask() {
        if (failTask != null && !failTask.isDone()) {
            failTask.cancel(true);
        }
    }

    @Override
    protected void handleBackHome() {
		stage = 0;
        cancelTask();
		cancelFailTask();
		despawn(282131);
		despawn(282015);
		despawn(282014);
		despawn(282107);
		despawn(281903);
        super.handleBackHome();
    }

    @Override
    protected void handleDespawned() {
		stage = 0;
        cancelTask();
		cancelFailTask();
		despawn(282131);
		despawn(282015);
		despawn(282014);
		despawn(282107);
		despawn(281903);
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
		sendMsg(1500016);
		stage = 0;
        cancelTask();
		cancelFailTask();
		despawn(282131);
		despawn(282015);
		despawn(282014);
		despawn(282107);
		despawn(281903);
        super.handleDied();
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}
}
