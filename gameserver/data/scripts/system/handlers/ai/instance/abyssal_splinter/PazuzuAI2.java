/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.abyssal_splinter;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;

@AIName("pazuzu")
public class PazuzuAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isHome = new AtomicBoolean(true);
    private Future<?> task;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
			sendMsg(1500001);
            startTask();
        }
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        cancelTask();
        isHome.set(true);
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        cancelTask();
		switch (Rnd.get(1, 2)) {
			case 1:
					sendMsg(1500003);
				break;
			case 2:
					sendMsg(1500004);
				break;
        }
    }

    private void startTask() {
        task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
				sendMsg(1500002);
				SkillEngine.getInstance().getSkill(getOwner(), 19145, 55, getTarget()).useSkill();
				Npc boss = getPosition().getWorldMapInstance().getNpc(216951);
                if (getPosition().getWorldMapInstance().getNpc(281909) == null) {
                    Npc worms = (Npc) spawn(281909, 651.351990f, 326.425995f, 465.523987f, (byte) 8);
                    Npc worms1 = (Npc) spawn(281909, 666.604980f, 314.497009f, 465.394012f, (byte) 27);
                    Npc worms2 = (Npc) spawn(281909, 685.588989f, 342.955994f, 465.908997f, (byte) 68);
                    Npc worms3 = (Npc) spawn(281909, 651.322021f, 346.554993f, 465.563995f, (byte) 111);
                    Npc worms4 = (Npc) spawn(281909, 665.7373f, 351.2235f, 465.38953f, (byte) 30);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						
						@Override
						public void run() {
							SkillEngine.getInstance().getSkill(worms, 19291, 55, boss).useSkill();
							SkillEngine.getInstance().getSkill(worms1, 19291, 55, boss).useSkill();
							SkillEngine.getInstance().getSkill(worms2, 19291, 55, boss).useSkill();
							SkillEngine.getInstance().getSkill(worms3, 19291, 55, boss).useSkill();
							SkillEngine.getInstance().getSkill(worms4, 19291, 55, boss).useSkill();
						}

					}, 3000);
                }
            }

        }, 10000, 70000);
    }

    private void cancelTask() {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

}
