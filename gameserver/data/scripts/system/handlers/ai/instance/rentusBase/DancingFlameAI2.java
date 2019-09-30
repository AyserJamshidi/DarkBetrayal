/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import java.util.concurrent.Future;
import ai.GeneralNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("dancing_flame")
public class DancingFlameAI2 extends GeneralNpcAI2 {

    private Future<?> task;

    private void startTask() {
        task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelTask();
                } else {
                    if (isPlayerInRange()) {
                        WorldPosition p = getPosition();
                        if (getNpcId() == 282996) {
                            spawn(282998, p.getX(), p.getY(), p.getZ(), p.getH());
                        } else {
                            spawn(282999, p.getX(), p.getY(), p.getZ(), p.getH());
                        }
                    }
                }
            }

        }, 3000, 3000);
    }

    private boolean isPlayerInRange() {
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (isInRange(player, 30)) {
                return true;
            }
        }
        return false;
    }

    private void cancelTask() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        if (getNpcId() == 282996 || getNpcId() == 282997) {
            startTask();
        } else {
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    SkillEngine.getInstance().getSkill(getOwner(), getNpcId() == 282998 ? 20536 : 20535, 60, getOwner()).useNoAnimationSkill();
                }

            }, 500);
            starLifeTask();
        }
    }

    private void starLifeTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                despawn();
            }

        }, 4000);
    }

    private void despawn() {
        if (!isAlreadyDead()) {
            AI2Actions.deleteOwner(this);
        }
    }

    @Override
    protected void handleDespawned() {
        super.handleDespawned();
        cancelTask();
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        cancelTask();
    }

    @Override
    public AIAnswer ask(AIQuestion question) {
        switch (question) {
            case CAN_ATTACK_PLAYER:
                return AIAnswers.POSITIVE;
            default:
                return AIAnswers.NEGATIVE;
        }
    }

}
