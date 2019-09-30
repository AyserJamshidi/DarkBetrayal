/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.tallocsHollow;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("kinquid_debuff")
public class KinquidDebuffAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleCreatureMoved(Creature creature) {
        super.handleCreatureMoved(creature);
        if (creature instanceof Npc && isInRange(creature, 2)) {
            Npc npc = (Npc) creature;
            if (npc.getNpcId() == 215467) {
                SkillEngine.getInstance().getSkill(getOwner(), getNpcId() == 282008 ? 19235 : 19236, 46, getOwner()).useNoAnimationSkill();
            }
        }
    }

}
