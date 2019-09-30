package ai.instance.elementisForest;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;
import com.ne.commons.utils.Rnd;
import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

@AIName("canyonguard")
public class CanyonguardAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isStart = new AtomicBoolean(false);
	private int stage = 0;
    private Future<?> skillStart;
	private Future<?> specialSkillTask;
	private Future<?> spawnGuard;
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useSkill();
	}

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isStart.compareAndSet(false, true)) {
			scheduleSpawnEntrance();
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 95) {
			stage++;
            if (stage == 1) {
                startBasicSkillTask();
            }
        }
    }
	
	private void scheduleSpawnEntrance() {
        spawnGuard = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				getOwner().clearAttackedCount();
				int count = Rnd.get(1, 2);
				for (int i = 0; i < count; i++) {
					int npcId = 282429;
					spawn(npcId, 301.465f + Rnd.get(-7, 7), 737.921f + Rnd.get(-7, 7), 201.1f, (byte) 105);
				}
			}
        }, 30000, 40000);
    }
	
	private void startBasicSkillTask() {
        skillStart = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelTask();
                } else {
                    switch (Rnd.get(1, 3)) {
                        case 1:
								Smash();
                            break;
						case 2:
								SkillEngine.getInstance().getSkill(getOwner(), 19644, 29, getTarget()).useNoAnimationSkill();
                            break;
						case 3:
								SkillEngine.getInstance().getSkill(getOwner(), 19645, 29, getTarget()).useNoAnimationSkill();
                            break;
                    } 
                }
            }

        }, 1000, 20000);
    }

    private void Smash() {
		useSkill(19503);
		specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			public void run() {
				useSkill(19710);
				specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
					public void run() {
						useSkill(19710);
						specialSkillTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								useSkill(19710);
							}
						}, 2500);
					}
				}, 2500);	
			}
        }, 5500);
    }
	
	private void cancelGuardTask() {
        if (spawnGuard != null && !spawnGuard.isCancelled()) {
            spawnGuard.cancel(true);
        }
    }
	
	private void cancelTask() {
        if (skillStart != null && !skillStart.isCancelled()) {
            skillStart.cancel(true);
        }
    }
	
    private void cancelSpecialTask() {
        if (specialSkillTask != null && !specialSkillTask.isCancelled()) {
            specialSkillTask.cancel(true);
        }
    }

    @Override
    protected void handleDespawned() {
        cancelTask();
		cancelSpecialTask();
		cancelGuardTask();
		stage = 0;
        super.handleDespawned();
    }

    @Override
    public void handleBackHome() {
        cancelTask();
		cancelSpecialTask();
		cancelGuardTask();
        WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(282429));
                deleteNpcs(instance.getNpcs(282430));
            }
        }
        super.handleBackHome();
		stage = 0;
        isStart.set(false);
    }
	
    @Override
    public void handleDied() {
        cancelTask();
		cancelSpecialTask();
		cancelGuardTask();
		stage = 0;
		super.handleDied();
		WorldPosition p = getPosition();
		WorldMapInstance instance = p.getWorldMapInstance();
        deleteNpcs(instance.getNpcs(282429));
        deleteNpcs(instance.getNpcs(282430));
    }

    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

}
