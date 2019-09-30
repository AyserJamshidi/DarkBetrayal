package ai.siege;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Future;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.services.NpcShoutsService;

@AIName("avatar_crystal")
public class TiamatAvatarCrystalAI2 extends SiegeNpcAI2 {
	
	private final AtomicBoolean isHome = new AtomicBoolean(true);
	private Future<?> phaseTask;
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            startPhaseTask();
            sendMsg(1500496);
        }
    }
	
    private void startPhaseTask() {
        phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
			Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelskillTask();
					despawn(282731);
                } else {
                    sendMsg(1500497);
					despawn(282731);
                    rndSpawn(282731);
					rndSpawn(282731);
					rndSpawn(282731);
					rndSpawn(282731);
					rndSpawn(282731);
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							useSkill(20170);
						}
					}, 3000);
                }
            }
        }, 60000, 60000);
    }
	
	private void rndSpawn(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(10, 25);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
    }
	
	private void cancelskillTask() {
        if (phaseTask != null && !phaseTask.isCancelled()) {
            phaseTask.cancel(true);
        }
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useSkill();
	}

    private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
	
    protected void handleDied() {
        super.handleDied();
        sendMsg(1500498);
		cancelskillTask();
		despawn(282731);
        int npc = getOwner().getNpcId();
        switch (npc) {
            case 259614:
                spawn(701237, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
                despawnClaw();
                break;
        }
    }

    private void despawnClaw() {
        final Npc claw = getPosition().getWorldMapInstance().getNpc(701237);
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                claw.getController().onDelete();
            }
        }, 60000 * 5);
    }
	
    @Override
    protected void handleBackHome() {
		cancelskillTask();
        isHome.set(true);
		despawn(282731);
        super.handleBackHome();
    }
	
    @Override
    protected void handleDespawned() {
		cancelskillTask();
		despawn(282731);
        super.handleDespawned();
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		if (!npc.equals(this.getOwner())){
			npc.getController().onDelete();
		}
	  }
	}
}