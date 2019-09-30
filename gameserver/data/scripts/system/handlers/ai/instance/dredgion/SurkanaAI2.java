/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.dredgion;

import ai.OneDmgPerHitAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.event.AIEventType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;

/**
 * recieve only 1 dmg with each attack(handled by super)
 * Aggro the whole room on attack
 *
 * @author Luzien
 */
@AIName("surkana")
public class SurkanaAI2 extends OneDmgPerHitAI2 {

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        // roomaggro
        checkForSupport(creature);
    }

    private void checkForSupport(Creature creature) {
        for (VisibleObject object : getKnownList().getKnownObjects().values()) {
            if (object instanceof Npc && isInRange(object, 25) && !((Npc) object).getLifeStats().isAlreadyDead()) {
                ((Npc) object).getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
            }
        }
    }
}
