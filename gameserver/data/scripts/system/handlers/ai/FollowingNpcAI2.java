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
import com.ne.gs.ai2.handler.FollowEventHandler;
import com.ne.gs.model.gameobjects.Creature;

/**
 * @author ATracer
 */
@AIName("following")
public class FollowingNpcAI2 extends GeneralNpcAI2 {

    @Override
    protected void handleFollowMe(Creature creature) {
        FollowEventHandler.follow(this, creature);
    }

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

        if (eventType == AIEventType.CREATURE_MOVED) {
            return getState() == AIState.FOLLOWING;
        }
        return true;
    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature == getOwner().getTarget()) {
            FollowEventHandler.creatureMoved(this, creature);
        }
    }

    @Override
    protected void handleStopFollowMe(Creature creature) {
        FollowEventHandler.stopFollow(this, creature);
    }

}
