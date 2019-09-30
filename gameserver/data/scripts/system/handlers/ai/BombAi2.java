/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.ai.BombTemplate;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author xTz
 */
@AIName("bomb")
public class BombAi2 extends AggressiveNpcAI2 {

    private BombTemplate template;

    @Override
    protected void handleSpawned() {
        template = DataManager.AI_DATA.getAiTemplate().get(getNpcId()).getBombs().getBombTemplate();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                doUseSkill();
            }
        }, 2000);
    }

    private void doUseSkill() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                useSkill(template.getSkillId());
            }
        }, template.getCd());
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

    private void useSkill(int skill) {
        AI2Actions.targetSelf(this);
        AI2Actions.useSkill(this, skill);
        int duration = DataManager.SKILL_DATA.getSkillTemplate(skill).getDuration();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                AI2Actions.deleteOwner(BombAi2.this);
            }
        }, duration != 0 ? duration + 4000 : 0);
    }
}
