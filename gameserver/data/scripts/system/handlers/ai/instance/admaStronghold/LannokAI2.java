package ai.instance.admaStronghold;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;

@AIName("lannok")
public class LannokAI2 extends AggressiveNpcAI2 {

    private Future<?> basicSkillTask;
	private int stage1 = 0;
	private int stage2 = 0;
	private int stage3 = 0;
	private int stage4 = 0;
	
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
		stage2 = 0;
		stage3 = 0;
		stage4 = 0;
		despawn(280933);
		despawn(280949);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		cancelSkillTask();
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		stage4 = 0;
		despawn(280933);
		despawn(280949);
    }
	
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 80) {
			stage1++;
			if (stage1 == 1) {
				 startBasicSkillTask();
			}
        }
		if (hpPercentage <= 50) {
			stage2++;
			if (stage2 == 1) {
				 spawn();
			}
        }
		if (hpPercentage <= 25) {
			stage3++;
			if (stage3 == 1) {
				 spawn();
			}
        }
		if (hpPercentage <= 10) {
			stage4++;
			if (stage4 == 1) {
				 useSkill(18107);
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
					useSkill(18461);
                }
            }

        }, 40000, 40000);
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

	private void spawn() {
		switch (Rnd.get(1, 2)) {
			case 1:
				attackPlayer((Npc) spawn(280949,  595f,  720f,  198.7f, (byte)88));
				attackPlayer((Npc) spawn(280949,  621f,  723f,  198.7f, (byte)88));
				attackPlayer((Npc) spawn(280933,  600f,  767f,  198.7f, (byte)88));
				attackPlayer((Npc) spawn(280933,  623f,  760f,  198.7f, (byte)88));
				break;
			case 2:
				attackPlayer((Npc) spawn(280949,  595f,  720f,  198.7f, (byte)88));
				attackPlayer((Npc) spawn(280949,  621f,  723f,  198.7f, (byte)88));
				attackPlayer((Npc) spawn(280949,  600f,  767f,  198.7f, (byte)88));
				attackPlayer((Npc) spawn(280933,  623f,  760f,  198.7f, (byte)88));
				break;
        }
	}
	
    private void cancelSkillTask() {
        if (basicSkillTask != null && !basicSkillTask.isCancelled()) {
            basicSkillTask.cancel(true);
        }
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 50, getTarget()).useSkill();
	}
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}
}