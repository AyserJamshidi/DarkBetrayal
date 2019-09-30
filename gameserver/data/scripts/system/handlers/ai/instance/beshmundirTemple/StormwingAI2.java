package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;
import java.util.List;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

@AIName("stormwing")
public class StormwingAI2 extends AggressiveNpcAI2 {
	
    private Future<?> basicSkillTask;
	private Future<?> finalSkillTask;
	private Future<?> random;
	private int stage = 0;
	private int stagetwo = 0;
	private int stagethree = 0;

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
		cancelRandomTask();
		stage = 0;
		stagetwo = 0;
		stagethree = 0;
		sendMsg(1500092);
		WorldPosition p = getPosition();
		WorldMapInstance instance = p.getWorldMapInstance();
        deleteNpcs(instance.getNpcs(281795));
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        cancelSkillTask();
		cancelFinalSkillTask();
		cancelRandomTask();
		stage = 0;
		stagetwo = 0;
		stagethree = 0;
		WorldPosition p = getPosition();
		WorldMapInstance instance = p.getWorldMapInstance();
        deleteNpcs(instance.getNpcs(281795));
    }
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 99) {
			stage++;
			if (stage == 1) {
				startBasicSkillTask();
				sendMsg(1500086);
				startStageThree();
			}
        }
        if (hpPercentage <= 35) {
			stagetwo++;
			if (stagetwo == 1) {
				cancelSkillTask();
				startFinalSkillTask();
				useSkill(18617);
				sendMsg(1500090);
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
                    switch (Rnd.get(1, 4)) {
                        case 1:
								useSkill(18612);
								sendMsg(1500087);
                            break;
                        case 2:
								useSkill(18614);
								sendMsg(1500089);
                            break;
                        case 3:
								useSkill(18613);
								sendMsg(1500091);
                            break;
						case 4:
								useSkill(18615);
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
                    switch (Rnd.get(1, 4)) {
                        case 1:
								useSkill(18616);
								sendMsg(1500088);
                            break;
                        case 2:
								useSkill(18612);
								sendMsg(1500087);
                            break;
                        case 3:
								useSkill(18614);
								sendMsg(1500089);
                            break;
						case 4:
								useSkill(18613);
								sendMsg(1500091);
                            break;
                    } 
                }
            }

        }, 15000, 15000);
    }
	
	private void startStageThree() {
		random = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), 18612, 55, getTarget()).useNoAnimationSkill();
					int size = getPosition().getWorldMapInstance().getNpcs(281795).size();
					for (int i = 0; i < 9; i++) {
						if (size >= 12) {
							break;
						}
						size++;
						rndSpawn(281795);
					}
				}
			}
		}, 60000, 60000);
	}
	
	private void rndSpawn(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(5, 10);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
    }
	
	private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getTarget()).useSkill();
	}
	
	private void cancelRandomTask() {
        if (random != null && !random.isCancelled()) {
            random.cancel(true);
        }
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
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

}