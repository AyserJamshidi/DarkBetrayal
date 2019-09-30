/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.elementisForest;

import java.util.concurrent.Future;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;

/**
 * @author xTz
 */
@AIName("tualis_drained_minion")
public class TualisDrainedMinionAI2 extends AggressiveNpcAI2 {

    private Future<?> lifeTask;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        startLifeTask();
    }

    private void startLifeTask() {
        lifeTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    AI2Actions.deleteOwner(TualisDrainedMinionAI2.this);
                }
            }

        }, 30000);
    }

    private void cancelTask() {
        if (lifeTask != null && !lifeTask.isDone()) {
            lifeTask.cancel(true);
        }
    }

    @Override
    protected void handleDied() {
        cancelTask();
        super.handleDied();
    }

    @Override
    protected void handleDespawned() {
        cancelTask();
        super.handleDespawned();
    }

}
