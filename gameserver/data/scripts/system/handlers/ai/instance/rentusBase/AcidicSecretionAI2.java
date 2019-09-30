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
import com.ne.gs.ai2.AIName;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("acidic_secretion")
public class AcidicSecretionAI2 extends GeneralNpcAI2 {

    private Future<?> eventTask;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        startEventTask();
    }

    @Override
    protected void handleDied() {
        cancelEventTask();
        super.handleDied();
    }

    @Override
    protected void handleDespawned() {
        cancelEventTask();
        super.handleDespawned();
    }

    private void cancelEventTask() {
        if (eventTask != null && !eventTask.isDone()) {
            eventTask.cancel(true);
        }
    }

    private void startEventTask() {
        eventTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelEventTask();
                } else {
                    SkillEngine.getInstance().getSkill(getOwner(), 19651, 60, getOwner()).useNoAnimationSkill();
                }
            }

        }, 1000, 3000);

    }

}
