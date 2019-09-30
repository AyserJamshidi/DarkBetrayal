package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;

import com.ne.commons.utils.Rnd;

import com.ne.gs.ai2.AIName;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.world.WorldPosition;


@AIName("DarkPoetaGenerator")
public class DarkPoetaGeneratorAI2 extends AggressiveNpcAI2{
	
	private int stage = 0;
	private int stagetwo = 0;
	private int npcSumon = 0;
	
	
	@Override
	protected void handleAttack(Creature creature){
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}
	
    @Override
    protected void handleDied() {
        super.handleDied();
		stage = 0;
		stagetwo = 0;
		npcSumon = 281091;
		rndSpawn(npcSumon);
		rndSpawn(npcSumon);
		rndSpawn(npcSumon);
		rndSpawn(npcSumon);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		stage = 0;
		stagetwo = 0;
    }

	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 50) {
			stage++;
			if (stage == 1) {
				npcSumon = 281090;
				startStageThree();
				
			}
        }
		if (hpPercentage <= 25) {
			stagetwo++;
			if (stagetwo == 1) {
				npcSumon = 281092;
				startStageThree();
			}
        }
    }
	
	private void startStageThree() {
		if (!isAlreadyDead()) {
			SkillEngine.getInstance().getSkill(getOwner(), 18179, 55, getTarget()).useNoAnimationSkill();
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				public void run() {
					rndSpawn(npcSumon);
					rndSpawn(npcSumon);
					rndSpawn(npcSumon);
				}
			}, 2500);
		}
	}
	
	private void rndSpawn(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(3, 5);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
    }
}