/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.walkers;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.state.CreatureState;

/**
 * @author Rolandas
 */
@AIName("aggrorunner")
public class WalkAggroRunnerAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleMoveArrived() {
        super.handleMoveArrived();
        getOwner().setState(CreatureState.WEAPON_EQUIPPED);
    }
}
