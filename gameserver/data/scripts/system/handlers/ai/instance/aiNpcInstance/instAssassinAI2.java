package ai.instance.aiNpcInstance;

import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.skillengine.SkillEngine;

@AIName("inst_assassin")
public class instAssassinAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    SkillEngine.getInstance().getSkill(getOwner(), 19915, 60, getOwner()).useNoAnimationSkill();
                }
            }

        }, 2000);
    }

    @Override
    protected void handleBackHome() {
        getEffectController().removeEffect(19915);
        getEffectController().removeEffect(19916);
        SkillEngine.getInstance().getSkill(getOwner(), 19915, 60, getOwner()).useNoAnimationSkill();
        super.handleBackHome();
    }
}
