/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.argentManor;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("magical_sap")
public class MagicalSapAI2 extends NpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        startEventTask(1000);
        startEventTask(4000);
        startEventTask(7000);
        startEventTask(10000);
    }

    private void startEventTask(final int time) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    SkillEngine.getInstance().getSkill(getOwner(), 19306, 55, getOwner()).useNoAnimationSkill();
                    if (time == 10000) {
                        AI2Actions.deleteOwner(MagicalSapAI2.this);
                    }
                }
            }

        }, time);

    }
}
