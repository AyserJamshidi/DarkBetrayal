package ai.instance.darkPoeta;

import ai.AggressiveNpcAI2;

import com.ne.commons.utils.Rnd;
import java.util.concurrent.atomic.AtomicBoolean;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;


@AIName("telepathycontroller")
public class TelepathyControllerAI2 extends AggressiveNpcAI2
{
	private AtomicBoolean isStart50Event = new AtomicBoolean(false);
	private AtomicBoolean isStart10Event = new AtomicBoolean(false);
	
	
	@Override
	protected void handleAttack(Creature creature){
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleBackHome() {
		isStart50Event.set(false);
		isStart10Event.set(false);
		super.handleBackHome();
	}

	private void checkPercentage(int hpPercentage){
		if (hpPercentage <= 50){
			if (isStart50Event.compareAndSet(false, true)) {
				spawnInRange(281150);
			}
		}
		if (hpPercentage <= 10){
			if (isStart10Event.compareAndSet(false, true)) {
				spawnInRange(281334);
				spawnInRange(281334);
			}
		}
	}
	
	private void spawnInRange(int npcId) {
        WorldPosition p = getPosition();
		attackPlayer((Npc) spawn(npcId,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
		attackPlayer((Npc) spawn(npcId,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
		attackPlayer((Npc) spawn(npcId,  p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte)0));
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
}