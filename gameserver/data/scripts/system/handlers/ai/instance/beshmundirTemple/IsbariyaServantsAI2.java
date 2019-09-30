/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;

/**
 * @author Luzien
 *         TODO: Random aggro switch
 */
@AIName("isbariyaServants")
public class IsbariyaServantsAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        int lifetime = (getNpcId() == 281659 ? 20000 : 10000);
        toDespawn(lifetime);
    }

    private void toDespawn(int delay) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                AI2Actions.deleteOwner(IsbariyaServantsAI2.this);
            }
        }, delay);
    }
}
