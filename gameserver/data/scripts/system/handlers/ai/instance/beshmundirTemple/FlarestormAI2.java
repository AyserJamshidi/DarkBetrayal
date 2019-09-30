package ai.instance.beshmundirTemple;

import ai.AggressiveNpcAI2;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;

import java.util.concurrent.Future;

@AIName("flarestorm")
public class FlarestormAI2 extends AggressiveNpcAI2 {

	private Future<?> spawnGuard;
	private Future<?> skillTask;
	private int stage1 = 0;
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        super.handleDied();
		cancelGuardTask();
		cancelSkillTask();
		sendMsg(1500078);
		stage1 = 0;
		despawn(216226);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		cancelSkillTask();
		cancelGuardTask();
		stage1 = 0;
		despawn(216226);
    }
	
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 99) {
			stage1++;
			if (stage1 == 1) {
				 sendMsg(1500076);
				 startSkillTask();
				 scheduleSpawnEntrance();
			}
        }
    }
	
	private void startSkillTask() {
        skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				sendMsg(1500077);
				useSkill(18909);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					public void run() {
						useSkill(18997);
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								useSkill(18911);
							}
						}, 5500);
					}
				}, 1500);
			}
        }, 15000, 35000);
    }

	
	private void scheduleSpawnEntrance() {
        spawnGuard = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
			@Override
            public void run() {
				getOwner().clearAttackedCount();
				int count = Rnd.get(3, 5);
				for (int i = 0; i < count; i++) {
					int npcId = 216226;
					attackPlayer((Npc) spawn(npcId, 1472.465f + Rnd.get(-3, 3), 1082.921f + Rnd.get(-3, 3), 278.351f, (byte) 105));
				}
			}
        }, 10000, 25000);
    }
	
	
	private void attackPlayer(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                npc.setTarget(getTarget());
                ((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
                npc.setState(1);
                npc.getMoveController().moveToTargetObject();
                PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
            }
        }, 1000);
    }
	
	private void cancelGuardTask() {
        if (spawnGuard != null && !spawnGuard.isDone())
            spawnGuard.cancel(true);
    }
	
	private void cancelSkillTask() {
        if (skillTask != null && !skillTask.isDone())
            skillTask.cancel(true);
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 50, getTarget()).useSkill();
	}

	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
}