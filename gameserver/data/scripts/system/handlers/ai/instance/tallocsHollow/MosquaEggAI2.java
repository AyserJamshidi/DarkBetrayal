/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.tallocsHollow;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author xTz
 */
@AIName("mosquaegg")
public class MosquaEggAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleSpawned() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                checkSpawn();
            }
        }, 17000);
    }

    private void checkSpawn() {
        if (getPosition().isSpawned()) {
            // spawn - Spawned Supraklaw
            spawn(217132, getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getH());
            AI2Actions.deleteOwner(this);
        }
    }
}
