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
import com.ne.gs.ai2.AttackIntention;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.ai2.handler.*;
import com.ne.gs.ai2.manager.SkillAttackManager;
import com.ne.gs.model.skill.NpcSkillEntry;

@AIName("homing2")
public class Homing2NpcAI2 extends GeneralNpcAI2 {

    @Override
    public void think() {
        // homings are not thinking to return :)
    }
	
    @Override
    public AttackIntention chooseAttackIntention() {
        NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
        if (skill != null) {
            skillId = skill.getSkillId();
            skillLevel = skill.getSkillLevel();
            return AttackIntention.SKILL_ATTACK;
        }
		return AttackIntention.SIMPLE_ATTACK;
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
