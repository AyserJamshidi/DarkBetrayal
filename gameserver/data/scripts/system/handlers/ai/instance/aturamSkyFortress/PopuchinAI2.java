/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.aturamSkyFortress;

import java.util.concurrent.Future;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.services.NpcShoutsService;

/**
 * @author xTz
 */
@AIName("popuchin")
public class PopuchinAI2 extends AggressiveNpcAI2 {

    private boolean isHome = true;
    private Future<?> bombTask;

    private void startBombTask() {
        if (!isAlreadyDead() && !isHome) {
            bombTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (!isAlreadyDead() && !isHome) {
                        VisibleObject target = getTarget();
                        if (target != null && target instanceof Player) {
							sendMsg(1500585);
                            SkillEngine.getInstance().getSkill(getOwner(), 19413, 49, target).useNoAnimationSkill();
                        }
                        ThreadPoolManager.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                if (!isAlreadyDead() && !isHome) {
									sendMsg(1500587);
                                    SkillEngine.getInstance().getSkill(getOwner(), 19412, 49, getOwner()).useNoAnimationSkill();
                                    ThreadPoolManager.getInstance().schedule(new Runnable() {

                                        @Override
                                        public void run() {
                                            if (!isAlreadyDead() && !isHome && getOwner().isSpawned()) {
                                                if (getLifeStats().getHpPercentage() > 50) {
                                                    WorldPosition p = getPosition();
													sendMsg(1500589);
                                                    if (p != null && p.getWorldMapInstance() != null) {
                                                        spawn(217374, p.getX(), p.getY(), p.getZ(), p.getH());
                                                        spawn(217374, p.getX(), p.getY(), p.getZ(), p.getH());
                                                        startBombTask();
                                                    }
                                                } else {
                                                    spawnRndBombs();
                                                    startBombTask();
                                                }
                                            }
                                        }

                                    }, 1500);
                                }
                            }

                        }, 3000);
                    }
                }

            }, 15500);
        }
    }
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome) {
            isHome = false;
            getPosition().getWorldMapInstance().getDoors().get(68).setOpen(false);
            startBombTask();
			sendMsg(1500584);
        }
    }

    private void rndSpawnInRange(int npcId, float distance) {
        float direction = Rnd.get(0, 199) / 100f;
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        spawn(npcId, getPosition().getX() + x1, getPosition().getY() + y1, getPosition().getZ(), (byte) 0);
    }

    @Override
    protected void handleBackHome() {
        isHome = true;
        super.handleBackHome();
        getPosition().getWorldMapInstance().getDoors().get(68).setOpen(true);
        if (bombTask != null && !bombTask.isDone()) {
            bombTask.cancel(true);
        }
    }

    private void spawnRndBombs() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead() && !isHome) {
                    for (int i = 0; i < 10; i++) {
                        rndSpawnInRange(217375, Rnd.get(1, 12));
                    }
                }
            }

        }, 1500);

    }

    @Override
    public AIAnswer ask(AIQuestion question) {
        switch (question) {
            case CAN_RESIST_ABNORMAL:
                return AIAnswers.POSITIVE;
            default:
                return AIAnswers.NEGATIVE;
        }
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        getPosition().getWorldMapInstance().getDoors().get(68).setOpen(true);
    }

}
