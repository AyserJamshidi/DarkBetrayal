/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.lowerUdas;

import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.skillengine.SkillEngine;


@AIName("udas_assassin")
public class UdasAssassinAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    SkillEngine.getInstance().getSkill(getOwner(), 19915, 60, getOwner()).useNoAnimationSkill();
                }
            }

        }, 2000);
    }

    @Override
    protected void handleBackHome() {
        getEffectController().removeEffect(19915);
        getEffectController().removeEffect(19916);
        SkillEngine.getInstance().getSkill(getOwner(), 19915, 60, getOwner()).useNoAnimationSkill();
        super.handleBackHome();
    }
}
