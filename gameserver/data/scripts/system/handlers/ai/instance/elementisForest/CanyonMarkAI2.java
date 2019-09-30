/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.elementisForest;

import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;

/**
 * @author Luzien
 */
@AIName("canyonmark")
public class CanyonMarkAI2 extends AggressiveNpcAI2 {

    private Creature target;

    @Override
    public void handleSpawned() {
        super.handleSpawned();
        markTarget();
    }

    private void markTarget() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                target = (Creature) getOwner().getTarget();
                if (target != null) {
                    AI2Actions.useSkill(CanyonMarkAI2.this, 19504);

                    ThreadPoolManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            if (!isAlreadyDead()) {
                                AI2Actions.targetCreature(CanyonMarkAI2.this, target);
                                AI2Actions.useSkill(CanyonMarkAI2.this, 19505);
                                AI2Actions.deleteOwner(CanyonMarkAI2.this);
                            }
                        }

                    }, Rnd.get(5, 10) * 1000);

                } else {
                    AI2Actions.deleteOwner(CanyonMarkAI2.this);
                }
            }
        }, 5000);
    }
}
