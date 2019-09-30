package ai.worlds.sarpan;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.services.NpcShoutsService;

@AIName("haridu")
public class HariduAI2 extends AggressiveNpcAI2 {
	
    private Future<?> basicSkillTask;
	private int stage = 0;
	private int stage2 = 0;
	private boolean think = true;

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
		sendMsg(1500299);
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
				sendMsg(1500296);
			}
        }
		if (hpPercentage <= 25) {
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
								useSkill(20474);
								sendMsg(1500296);
                            break;
                        case 2:
								useSkill(20014);
                            break;
                        case 3:
								useSkill(20017);
								sendMsg(1500360);
                            break;
                    } 
                }
            }

        }, 15000, 15000);
    }
	
	private void startFinalSkillTask() {
		cancelSkillTask();
		sendMsg(1500292);
		useSkill(20015);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			
            public void run() {
				startBasicSkillTask();
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					public void run() {
						useSkill(20016);
						think = false;
						EmoteManager.emoteStopAttacking(getOwner());
						startThinkTask();
						cancelSkillTask();
						startBasicSkillTask();
					}
				}, 30000);

			}
			
		}, 3000);
    }
	
    private void startThinkTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    think = true;
                    Creature creature = getAggroList().getMostHated();
                    if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
                        setStateIfNot(AIState.FIGHT);
                        think();
                    } else {
                        getMoveController().abortMove();
                        getOwner().setTarget(creature);
                        getOwner().getGameStats().renewLastAttackTime();
                        getOwner().getGameStats().renewLastAttackedTime();
                        getOwner().getGameStats().renewLastChangeTargetTime();
                        getOwner().getGameStats().renewLastSkillTime();
                        setStateIfNot(AIState.FIGHT);
                        handleMoveValidate();
                    }
                }
            }

        }, 8000);
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
	
    @Override
    public boolean canThink() {
        return think;
    }

}