/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.walkers;

import ai.GeneralNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.state.CreatureState;

/**
 * @author Rolandas
 */
@AIName("generalrunner")
public class WalkGeneralRunnerAI2 extends GeneralNpcAI2 {

    @Override
    protected void handleMoveArrived() {
        super.handleMoveArrived();
        getOwner().setState(CreatureState.WEAPON_EQUIPPED);
    }
}
