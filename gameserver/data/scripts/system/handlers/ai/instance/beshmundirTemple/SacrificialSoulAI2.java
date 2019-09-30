/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.beshmundirTemple;

import ai.GeneralNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.model.gameobjects.Creature;

@AIName("templeSoul")
public class SacrificialSoulAI2 extends GeneralNpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        AI2Actions.useSkill(this, 18901);
        setStateIfNot(AIState.FOLLOWING);
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (creature.getEffectController().hasAbnormalEffect(18959)) {
            getMoveController().abortMove();
            AI2Actions.deleteOwner(this);
        }
    }

    @Override
    public boolean canThink() {
        return false;
    }
}
