/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.templates.spawns.siegespawns.SiegeSpawnTemplate;

/**
 * @author ATracer
 */
public class SiegeNpcAI2 extends AggressiveNpcAI2 {

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.POSITIVE;
            default:
                return null;
        }
    }

    @Override
    protected SiegeSpawnTemplate getSpawnTemplate() {
        return (SiegeSpawnTemplate) super.getSpawnTemplate();
    }

}
