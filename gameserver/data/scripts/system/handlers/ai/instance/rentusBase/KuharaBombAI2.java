/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.GeneralNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("kuhara_bomb")
public class KuharaBombAI2 extends GeneralNpcAI2 {

    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);
    private Npc boss;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        setStateIfNot(AIState.FOLLOWING);
        boss = getPosition().getWorldMapInstance().getNpc(217311);
    }

    @Override
    protected void handleMoveArrived() {
        if (isDestroyed.compareAndSet(false, true)) {
            if (boss != null && !CreatureActions.isAlreadyDead(boss)) {
                SkillEngine.getInstance().getSkill(getOwner(), 19659, 60, boss).useNoAnimationSkill();
            }
        }
    }
}
