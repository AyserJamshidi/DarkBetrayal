package ai.instance.darkPoeta;

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
import com.ne.gs.world.WorldPosition;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;


@AIName("legat_anuhart")
public class LegatAnuhartAI2 extends AggressiveNpcAI2 {

	private int stage1 = 0;
	private int stage2 = 0;
	private int stage3 = 0;
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        super.handleDied();
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		despawn(281245);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		despawn(281245);
    }
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 75) {
			stage1++;
			if (stage1 == 1) {
				helpspawn();
			}
        }
        if (hpPercentage <= 50) {
			stage2++;
			if (stage2 == 1) {
				useSkill(18168);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						helpspawn();
					}
				}, 500);
			}
        }
		if (hpPercentage <= 35) {
			stage3++;
			if (stage3 == 1) {
				helpspawn();
				Heal();
			}
        }
    }
	
	private void Heal() {
		useSkill(18170);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
				useSkill(18170);
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						useSkill(18170);
					}
				}, 15000);
			}
			
		}, 15000);
    }
	
	private void helpspawn() {
		useSkill(18238);
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				WorldPosition p = getPosition();
				attackPlayer((Npc) spawn(281245,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
				attackPlayer((Npc) spawn(281245,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
				attackPlayer((Npc) spawn(281245,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
			}
		}, 2500);
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
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 50, getTarget()).useSkill();
	}
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}
}