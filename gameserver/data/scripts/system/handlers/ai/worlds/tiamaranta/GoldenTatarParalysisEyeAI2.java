/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.tiamaranta;

import java.util.concurrent.Future;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("golden_tatar_paralysis_eye")
public class GoldenTatarParalysisEyeAI2 extends AggressiveNpcAI2 {

    private Future<?> task;
    private int spawnCount;

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        if (getNpcId() == 282744) {
            startSpawnTask();
        } else {
            SkillEngine.getInstance().getSkill(getOwner(), 20213, 60, getOwner()).useNoAnimationSkill();
            task = ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (!isAlreadyDead()) {
                        AI2Actions.deleteOwner(GoldenTatarParalysisEyeAI2.this);
                    }
                }

            }, 2000);

        }
    }

    private void startSpawnTask() {
        task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelTask();
                } else {
                    spawnCount++;
                    WorldPosition p = getPosition();
                    spawn(282745, p.getX(), p.getY(), p.getZ(), p.getH());
                    if (spawnCount >= 10) {
                        cancelTask();
                        AI2Actions.deleteOwner(GoldenTatarParalysisEyeAI2.this);
                    }
                }
            }

        }, 14000, 2000);
    }

    private void cancelTask() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }

    @Override
    public AIAnswer ask(AIQuestion question) {
        switch (question) {
            case CAN_ATTACK_PLAYER:
                return AIAnswers.POSITIVE;
            case CAN_RESIST_ABNORMAL:
                return AIAnswers.POSITIVE;
            default:
                return AIAnswers.NEGATIVE;
        }
    }

    @Override
    protected void handleDespawned() {
        cancelTask();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        cancelTask();
        super.handleDied();
        AI2Actions.deleteOwner(this);
    }

}
