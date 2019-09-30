package ai.instance.theobomosLab;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;

@AIName("triroan")
public class TriroanAI2 extends AggressiveNpcAI2 {

    private Future<?> basicSkillTask;
	private int stage1 = 0;
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        super.handleDied();
		cancelSkillTask();
		stage1 = 0;
		despawn(280976);
		despawn(280977);
		despawn(280978);
		despawn(280975);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		cancelSkillTask();
		stage1 = 0;
		despawn(280976);
		despawn(280977);
		despawn(280978);
		despawn(280975);
    }
	
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 95) {
			stage1++;
			if (stage1 == 1) {
				 startBasicSkillTask();
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
							moveExecutor(280976);
							break;
						case 2:
							moveExecutor(280978);
							break;
						case 3:
							moveExecutor(280977);
							break;
						case 4:
							moveExecutor(280975);
							break;
        }
                }
            }

        }, 30000, 30000);
    }
	
	private void moveExecutor(final int executorId) {
        final Npc npc = (Npc) spawn(executorId, 601f, 488f, 196.2f, (byte) 0);
    }
	
    private void cancelSkillTask() {
        if (basicSkillTask != null && !basicSkillTask.isCancelled()) {
            basicSkillTask.cancel(true);
        }
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}
}