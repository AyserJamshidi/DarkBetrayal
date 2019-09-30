/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Source
 */
@AIName("siege_mine")
public class MineAI2 extends SiegeNpcAI2 {

    @Override
    protected void handleCreatureAggro(Creature creature) {

        AI2Actions.useSkill(this, 18407);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                AI2Actions.deleteOwner(MineAI2.this);
            }

        }, 1500);
    }

}
