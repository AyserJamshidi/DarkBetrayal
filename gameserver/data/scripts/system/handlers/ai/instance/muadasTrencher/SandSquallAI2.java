/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.muadasTrencher;

import java.util.concurrent.Future;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("sand_squall")
public class SandSquallAI2 extends AggressiveNpcAI2 {

    private Future<?> lifeTask;

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        startLifeTask();
        castSkillTask(19896, 500);
        castSkillTask(19894, 500);
        castSkillTask(19894, 2500);
        castSkillTask(20444, 4500);
        castSkillTask(19894, 6500);
        castSkillTask(19894, 8500);
        castSkillTask(19894, 10500);
        castSkillTask(20444, 12500);
        castSkillTask(19894, 14500);
        castSkillTask(19894, 16500);
        castSkillTask(19895, 18500);
    }

    @Override
    protected void handleDespawned() {
        cancelLifeTask();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        cancelLifeTask();
        super.handleDied();
        AI2Actions.deleteOwner(this);
    }

    private void castSkillTask(final int skill, int time) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    WorldPosition p = getPosition();
                    if (p != null) {
                        WorldMapInstance instance = p.getWorldMapInstance();
                        if (instance != null) {
                            SkillEngine.getInstance().getSkill(getOwner(), skill, 60, getOwner()).useNoAnimationSkill();
                        }
                    }
                }
            }

        }, time);
    }

    private void startLifeTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    AI2Actions.deleteOwner(SandSquallAI2.this);
                }
            }

        }, 20000);
    }

    private void cancelLifeTask() {
        if (lifeTask != null && !lifeTask.isDone()) {
            lifeTask.cancel(true);
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
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }
}
