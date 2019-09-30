package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.skillengine.SkillEngine;

@AIName("lakhara")
public class CaptainLakharaAI2 extends AggressiveNpcAI2 {
	
    private Future<?> basicSkillTask;
	private Future<?> finalSkillTask;
	private int stage = 0;
	private int stagetwo = 0;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        cancelSkillTask();
		cancelFinalSkillTask();
		stage = 0;
		stagetwo = 0;
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        cancelSkillTask();
		cancelFinalSkillTask();
		stage = 0;
		stagetwo = 0;
    }
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 95) {
			stage++;
			if (stage == 1) {
				startBasicSkillTask();
			}
        }
        if (hpPercentage <= 25) {
			stagetwo++;
			if (stagetwo == 1) {
				cancelSkillTask();
				startFinalSkillTask();
			}
        }
    }
	
    private void startBasicSkillTask() {
        basicSkillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelSkillTask();
                } else {
                    switch (Rnd.get(1, 3)) {
                        case 1:
								useSkill(18889);
                            break;
                        case 2:
								useSkill(18890);
                            break;
                        case 3:
								useSkill(19090);
                            break;
                    } 
                }
            }

        }, 15000, 15000);
    }
	
	private void startFinalSkillTask() {
        finalSkillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelSkillTask();
                } else {
					useSkill(18891);
                }
            }

        }, 15000, 15000);
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 50, getTarget()).useSkill();
	}

    private void cancelSkillTask() {
        if (basicSkillTask != null && !basicSkillTask.isCancelled()) {
            basicSkillTask.cancel(true);
        }
    }
	
    private void cancelFinalSkillTask() {
        if (finalSkillTask != null && !finalSkillTask.isCancelled()) {
            finalSkillTask.cancel(true);
        }
    }

}