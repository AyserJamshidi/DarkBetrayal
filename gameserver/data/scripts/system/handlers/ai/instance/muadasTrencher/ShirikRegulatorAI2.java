/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.muadasTrencher;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("shirik_regulator")
public class ShirikRegulatorAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleDied() {
        WorldPosition p = getPosition();
        if (p != null) {
            spawn(282539, p.getX(), p.getY(), p.getZ(), (byte) 0);
        }
        super.handleDied();
        AI2Actions.deleteOwner(this);
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }
}
