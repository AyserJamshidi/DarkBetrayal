package ai.instance.worlds.heiron;

import ai.AggressiveNpcAI2;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;


@AIName("jeshuchi")
public class JeshuchiAI2 extends AggressiveNpcAI2 {

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
		despawn(280758);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		stage1 = 0;
		stage2 = 0;
		stage3 = 0;
		despawn(280758);
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
				helpspawn();
			}
        }
		if (hpPercentage <= 25) {
			stage3++;
			if (stage3 == 1) {
				helpspawn();
			}
        }
    }
	
	private void helpspawn() {
        WorldPosition p = getPosition();
		attackPlayer((Npc) spawn(280758,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
		attackPlayer((Npc) spawn(280758,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
		attackPlayer((Npc) spawn(280758,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
		attackPlayer((Npc) spawn(280758,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
		attackPlayer((Npc) spawn(280758,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
		attackPlayer((Npc) spawn(280758,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
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
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}
}