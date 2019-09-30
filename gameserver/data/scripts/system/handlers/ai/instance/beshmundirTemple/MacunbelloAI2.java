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
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;

@AIName("macunbello")
public class MacunbelloAI2 extends AggressiveNpcAI2 {

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
		sendMsg(1500063);
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		stage4 = 0;
		despawn(281775);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		stage4 = 0;
		despawn(281775);
    }
	
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 95) {
			stage1++;
			if (stage1 == 1) {
				 sendMsg(1500060);
			}
        }
        if (hpPercentage <= 75) {
			stage2++;
			if (stage2 == 1) {
				 sendMsg(1500061);
				 spawn();
			}
        }
		if (hpPercentage <= 50) {
			stage3++;
			if (stage3 == 1) {
				 sendMsg(1500061);
				 spawn();
			}
        }
        if (hpPercentage <= 25) {
			stage4++;
			if (stage4 == 1) {
				 sendMsg(1500061);
				 spawn();
			}
        }
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
				attackPlayer((Npc) spawn(281775,  1004.0021f,  158.0125f,  242.1f, (byte)88));
				attackPlayer((Npc) spawn(281775,  955.3885f,  158.5231f,  242.1f, (byte)88));
				break;
			case 2:
				attackPlayer((Npc) spawn(281775,  952.647f,  110.2747f,  243.1f, (byte)32));
				attackPlayer((Npc) spawn(281775,  1007.3302f,  110.5709f,  243.1f, (byte)30));
				break;
        }
	}
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}

	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
}