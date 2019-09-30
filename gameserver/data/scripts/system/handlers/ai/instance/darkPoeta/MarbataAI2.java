/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.skillengine.SkillEngine;


@AIName("marbata")
public class MarbataAI2 extends AggressiveNpcAI2 {
	
	private int stage = 0;
	
	@Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
		stage++;
		if (stage == 1) {
			SkillEngine.getInstance().getSkill(getOwner(), 18556, 50, getOwner()).useSkill();
			SkillEngine.getInstance().getSkill(getOwner(), 18110, 50, getOwner()).useSkill();
		}
    }
	
	@Override
    protected void handleDied() {
		super.handleDied();
        getEffectController().removeEffect(18556);
        getEffectController().removeEffect(18110);
    }

    @Override
    protected void handleBackHome() {
		stage = 0;
        super.handleBackHome();
        getEffectController().removeEffect(18556);
        getEffectController().removeEffect(18110);
    }
}
