package ai.siege;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.services.NpcShoutsService;

@AIName("avatar_crack")
public class TiamatAvatarCrackAI2 extends SiegeNpcAI2 {
	
	private final AtomicBoolean isHome = new AtomicBoolean(true);
	protected List<Integer> percents = new ArrayList<>();
	private Future<?> phaseTask;
	private int npcid = 282735;
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
        if (isHome.compareAndSet(true, false)) {
            startPhaseTask();
            sendMsg(1500487);
        }
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 75, 50, 25);
    }
	
    private void checkPercentage(int hpPercentage) {
        for (Iterator<Integer> it = percents.iterator(); it.hasNext(); ) {
            int percent = it.next();
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 75:
						npcid = 282737;
                        break;
                    case 50:
						npcid = 282735;
                        break;
                    case 25:
						npcid = 282737;
                        break;
                }
                it.remove();
                break;
            }
        }
    }
	
    private void startPhaseTask() {
        phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
			Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelskillTask();
					despawn(282735);
					despawn(282737);
                } else {
                    sendMsg(1500488);
					despawn(282735);
					despawn(282737);
                    rndSpawn(npcid);
					rndSpawn(npcid);
					rndSpawn(npcid);
					rndSpawn(npcid);
					rndSpawn(npcid);
					rndSpawn(npcid);
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							useSkill(20161);
						}
					}, 1000);
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
        sendMsg(1500489);
		cancelskillTask();
		despawn(282735);
		despawn(282737);
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
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }
	
    @Override
    protected void handleBackHome() {
		cancelskillTask();
		addPercent();
        isHome.set(true);
		despawn(282735);
		despawn(282737);
        super.handleBackHome();
    }
	
    @Override
    protected void handleDespawned() {
		cancelskillTask();
		despawn(282735);
		despawn(282737);
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