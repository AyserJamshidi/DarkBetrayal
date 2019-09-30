package ai.instance.worlds.abyss;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.skillengine.SkillEngine;


@AIName("menotios")
public class MenotiosAI2 extends AggressiveNpcAI2 {

    private Future<?> skillTask;
	private int stage1 = 0;
	private int stage2 = 0;
	private int stage3 = 0;
	private int stage4 = 0;
	private int stage5 = 0;
	private int stage6 = 0;
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        super.handleDied();
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		stage4 = 0;
		stage5 = 0;
		stage6 = 0;
		cancelPhaseTask();
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		stage4 = 0;
		stage5 = 0;
		stage6 = 0;
		cancelPhaseTask();
    }
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 85) {
			stage1++;
			if (stage1 == 1) {
				useSkill(17835);
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						useSkill(17838);
					}
				}, 6000);
			}
        }
        if (hpPercentage <= 70) {
			stage2++;
			if (stage2 == 1) {
				useSkill(17835);
			}
        }
		if (hpPercentage <= 55) {
			stage3++;
			if (stage3 == 1) {
				useSkill(17835);
			}
        }
		if (hpPercentage <= 40) {
			stage4++;
			if (stage4 == 1) {
				useSkill(17835);
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						useSkill(17838);
					}
				}, 6000);
			}
        }
		if (hpPercentage <= 25) {
			stage5++;
			if (stage5 == 1) {
				useSkill(17835);
				startSkillTask();
			}
        }
		if (hpPercentage <= 10) {
			stage6++;
			if (stage6 == 1) {
				useSkill(17834);
			}
        }
    }
	
    private void startSkillTask() {
        skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelPhaseTask();
                } else {
					useSkill(17839);
                }
            }
        }, 20000, 20000);
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 51, getOwner()).useSkill();
	}
	
    private void cancelPhaseTask() {
        if (skillTask != null && !skillTask.isDone()) {
            skillTask.cancel(true);
        }
    }
}