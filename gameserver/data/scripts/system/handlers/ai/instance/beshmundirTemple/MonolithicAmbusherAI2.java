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

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.event.AIEventType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;

/**
 * @author xTz
 */
@AIName("monolithicambusher")
public class MonolithicAmbusherAI2 extends AggressiveNpcAI2 {

    private boolean hasHelped;

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        hasHelped = false;
    }

    @Override
    protected void handleCreatureAggro(Creature creature) {
        super.handleCreatureAggro(creature);
        if (!hasHelped) {
            hasHelped = true;
            help(creature);
        }
    }

    private void help(Creature creature) {
        for (VisibleObject object : getKnownList().getKnownObjects().values()) {
            if (object instanceof Npc && isInRange(object, 60)) {
                Npc npc = (Npc) object;
                if (!npc.getLifeStats().isAlreadyDead() && npc.getNpcId() == 216215 && (int) npc.getSpawn().getY() == (int) getSpawnTemplate().getY()) {
                    npc.getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
                }
            }
        }
    }
}
