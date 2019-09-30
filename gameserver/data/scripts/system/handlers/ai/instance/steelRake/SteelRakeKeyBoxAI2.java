/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.steelRake;

import ai.ChestAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;

/**
 * @author xTz
 */
@AIName("steel_rake_key_box")
public class SteelRakeKeyBoxAI2 extends ChestAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead() && getOwner().isSpawned()) {
                    AI2Actions.deleteOwner(SteelRakeKeyBoxAI2.this);
                }
            }

        }, 180000);
    }
}
