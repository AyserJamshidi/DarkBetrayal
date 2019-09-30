/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.argentManor;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.GeneralNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("cadellas_hetgolem")
public class CadellasHetgolemAI2 extends GeneralNpcAI2 {

    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        setStateIfNot(AIState.FOLLOWING);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    SkillEngine.getInstance().getSkill(getOwner(), 19571, 55, getOwner()).useNoAnimationSkill();
                }
            }

        }, 1000);
    }

    @Override
    protected void handleMoveArrived() {
        super.handleMoveArrived();
        getMoveController().abortMove();
        if (isDestroyed.compareAndSet(false, true)) {
            NpcShoutsService.getInstance().sendMsg(getOwner(), 1500462, getObjectId(), 0, 0);
            SkillEngine.getInstance().getSkill(getOwner(), getHealSkill(), 60, getOwner()).useNoAnimationSkill();
            startDespawnTask();
        }
    }

    private void startDespawnTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                AI2Actions.deleteOwner(CadellasHetgolemAI2.this);
            }

        }, 4000);
    }

    private int getHealSkill() {
        switch (getNpcId()) {
            case 282345:
                return 19525;
            case 282346:
                return 19526;
            case 282347:
                return 19527;
            case 282348:
                return 19528;
            case 282349:
                return 19529;
            default:
                return 0;
        }
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
