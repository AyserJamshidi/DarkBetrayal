/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.tiamarantasEye;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author Luzien
 *         TODO: fight AI
 */
@AIName("berserker_sunayaka")
public class BerserkerSunayakaAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isHome = new AtomicBoolean(true);

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            SkillEngine.getInstance().getSkill(getOwner(), 20651, 1, getOwner()).useNoAnimationSkill(); // ragetask
        }
    }

    @Override
    public void handleBackHome() {
        super.handleBackHome();
        isHome.set(true);
        getEffectController().removeEffect(20651);
        getEffectController().removeEffect(8763);
        // SkillEngine.getInstance().getSkill(getOwner(), 20652, 1, getOwner()).useNoAnimationSkill();
    }
}
