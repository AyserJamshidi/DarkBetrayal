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
import com.ne.gs.model.gameobjects.Creature;

/**
 * @author Luzien
 */
@AIName("divineartifact")
public class DivineArtifactAI2 extends AggressiveNpcAI2 {

    private boolean cooldown = false;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (!cooldown) {
            AI2Actions.useSkill(this, 18915);
            setCD();
        }
    }

    private void setCD() { // ugly hack to prevent overflow TODO: remove on AI improve
        cooldown = true;

        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                cooldown = false;
            }
        }, 1000);
    }
}
