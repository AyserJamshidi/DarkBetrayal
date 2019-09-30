package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;
import com.ne.commons.network.util.ThreadPoolManager;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.MathUtil;
import java.util.List;
import java.util.concurrent.Future;

@AIName("spaller_echtra")
public class SpallerEchtraAI2 extends AggressiveNpcAI2
{

	private boolean think = true;
	private Future<?> skillTask;
	private int stage = 0;
	
	@Override
	protected void handleAttack(Creature creature)
	{
		super.handleAttack(creature);
		checkDirection();
	}

	private void checkDirection()
	{
		List<Npc> npcs = getPosition().getWorldMapInstance().getNpcs(281178);
		if(npcs != null){
			for (Npc npc : npcs) {
				if(MathUtil.getDistance(getOwner(), npc) <= 4){
					npc.getController().onDie(npc);
					stage++;
					if (stage == 1) {
						startTasks();
					}
				}
			}
		}
	}
	
	private void startTasks() {
		Npc drana = getPosition().getWorldMapInstance().getNpc(281178);
		SkillEngine.getInstance().getSkill(getOwner(), 18179, 50, drana).useSkill();
		skillTask = ThreadPoolManager.getInstance().schedule(new Runnable(){
			public void run() {
				think = false;
				SkillEngine.getInstance().getSkill(getOwner(), 18536, 50, getOwner()).useSkill();
				EmoteManager.emoteStopAttacking(getOwner());
				startThinkTask();
			}
		}, 3500);
	}

	private void startThinkTask() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					SkillEngine.getInstance().getSkill(getOwner(), 18534, 50, getOwner()).useSkill();
					stage = 0;
					think = true;
					Creature creature = getAggroList().getMostHated();
					if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
						setStateIfNot( AIState.FIGHT);
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

		}, 25000);
	}

	private void cancelTask(){
		if(skillTask != null && !skillTask.isDone())
			skillTask.cancel(true);
	}

	@Override
	protected void handleBackHome(){
		stage = 0;
		cancelTask();
		super.handleBackHome();
	}

	@Override
	protected void handleDespawned(){
		stage = 0;
		cancelTask();
		super.handleDespawned();
	}

	@Override
	protected void handleDied() {
		stage = 0;
		cancelTask();
		super.handleDied();
	}

	@Override
	public boolean canThink() {
		return think;
	}
}