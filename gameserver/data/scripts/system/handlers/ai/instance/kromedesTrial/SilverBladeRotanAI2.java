/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.kromedesTrial;

import ai.HomingNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AttackIntention;

/**
 * @author Rolandas
 */
@AIName("rotan")
public class SilverBladeRotanAI2 extends HomingNpcAI2 {

    @Override
    public void think() {
        // servants are not thinking
    }

    @Override
    public AttackIntention chooseAttackIntention() {
        if (skillId == 0) {
            skillId = getSkillList().getRandomSkill().getSkillId();
            skillLevel = 1;
        }
        return AttackIntention.SKILL_ATTACK;
    }
}
