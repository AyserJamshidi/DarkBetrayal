package ai.worlds.sarpan;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.services.NpcShoutsService;

@AIName("djakufu")
public class DjakufuAI2 extends AggressiveNpcAI2 {
	
    private Future<?> basicSkillTask;
	private int stage = 0;
	private int stage2 = 0;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        cancelSkillTask();
		stage = 0;
		stage2 = 0;
		sendMsg(1500290);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        cancelSkillTask();
		stage = 0;
		stage2 = 0;
    }
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 99) {
			stage++;
			if (stage == 1) {
				startBasicSkillTask();
				sendMsg(1500283);
			}
        }
		if (hpPercentage <= 20) {
			stage2++;
			if (stage2 == 1) {
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
								useSkill(20007);
								sendMsg(1500284);
                            break;
                        case 2:
								useSkill(20010);
								sendMsg(1500286);
                            break;
                        case 3:
								useSkill(20011);
                            break;
                    } 
                }
            }

        }, 15000, 15000);
    }
	
	private void startFinalSkillTask() {
		cancelSkillTask();
		sendMsg(1500288);
		useSkill(20008);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			
            public void run() {
				startBasicSkillTask();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					public void run() {
						useSkill(20009);
						sendMsg(1500285);
						cancelSkillTask();
					}
				}, 30000);

			}
			
		}, 3000);
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getTarget()).useSkill();
	}

    private void cancelSkillTask() {
        if (basicSkillTask != null && !basicSkillTask.isCancelled()) {
            basicSkillTask.cancel(true);
        }
    }
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

}