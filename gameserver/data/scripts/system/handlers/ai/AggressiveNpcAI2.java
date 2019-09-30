/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.handler.AggroEventHandler;
import com.ne.gs.ai2.handler.CreatureEventHandler;
import com.ne.gs.model.gameobjects.Creature;

/**
 * @author ATracer
 */
@AIName("aggressive")
public class AggressiveNpcAI2 extends GeneralNpcAI2 {

    @Override
    protected void handleCreatureSee(Creature creature) {
        CreatureEventHandler.onCreatureSee(this, creature);
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        CreatureEventHandler.onCreatureMoved(this, creature);
    }

    @Override
    protected void handleCreatureAggro(Creature creature) {
        if (canThink()) {
            AggroEventHandler.onAggro(this, creature);
        }

    }

    @Override
    protected boolean handleGuardAgainstAttacker(Creature attacker) {
        return AggroEventHandler.onGuardAgainstAttacker(this, attacker);
    }

}
