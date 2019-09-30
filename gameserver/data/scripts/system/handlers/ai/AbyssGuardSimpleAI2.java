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
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.StateEvents;
import com.ne.gs.ai2.event.AIEventType;
import com.ne.gs.ai2.handler.SimpleAbyssGuardHandler;
import com.ne.gs.model.gameobjects.Creature;

/**
 * @author Rolandas
 */
@AIName("simple_abyssguard")
public class AbyssGuardSimpleAI2 extends AggressiveNpcAI2 {

    @Override
    protected boolean canHandleEvent(AIEventType eventType) {
        switch (getState()) {
            case DESPAWNED:
                return StateEvents.DESPAWN_EVENTS.hasEvent(eventType);
            case DIED:
                return StateEvents.DEAD_EVENTS.hasEvent(eventType);
            case CREATED:
                return StateEvents.CREATED_EVENTS.hasEvent(eventType);
        }
        switch (eventType) {
            case DIALOG_START:
            case DIALOG_FINISH:
                return isNonFightingState();
            case CREATURE_MOVED:
                return getState() != AIState.FIGHT;
        }
        return true;
    }

    @Override
    protected void handleCreatureSee(Creature creature) {
        SimpleAbyssGuardHandler.onCreatureSee(this, creature);
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        SimpleAbyssGuardHandler.onCreatureMoved(this, creature);
    }

    @Override
    protected boolean handleGuardAgainstAttacker(Creature attacker) {
        return false;
    }

}
