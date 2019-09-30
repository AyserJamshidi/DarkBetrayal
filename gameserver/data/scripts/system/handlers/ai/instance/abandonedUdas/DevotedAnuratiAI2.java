package ai.instance.abandonedUdas;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.NpcShoutsService;


@AIName("devoted_anurati")
public class DevotedAnuratiAI2 extends AggressiveNpcAI2 {

	private int start = 0;
	private Future<?> helperSpawn;
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
		start++;
		if (start == 1) {
			rndHelpSpawn();
			sendMsg(1500031);
        }
    }

    @Override
    protected void handleDied() {
        super.handleDied();
		start = 0;
		cancelRndHelpSpawn();
		despawn(281501);
		despawn(281502);
		despawn(281503);
		despawn(281504);
		sendMsg(1500033);
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
		start = 0;
		cancelRndHelpSpawn();
		despawn(281501);
		despawn(281502);
		despawn(281503);
		despawn(281504);
    }
	
    private void cancelRndHelpSpawn() {
        if (helperSpawn != null && !helperSpawn.isCancelled()) {
            helperSpawn.cancel(true);
        }
    }
	
    private void rndHelpSpawn() {
        helperSpawn = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelRndHelpSpawn();
                } else {
					sendMsg(1500032);
                    switch (Rnd.get(1, 4)) {
                        case 1:
								helpspawn(281501);
								helpspawn2(281502);
                            break;
                        case 2:
								helpspawn(281503);
								helpspawn2(281501);
                            break;
                        case 3:
								helpspawn(281504);
								helpspawn2(281502);
                            break;
						case 4:
								helpspawn(281504);
								helpspawn2(281503);
                            break;
                    } 
                }
            }

        }, 30000, 45000);
    }
	
	private void helpspawn(int npcid) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackPlayer((Npc) spawn(npcid,  632f, 494f, 136f, (byte)0));
			}
		}, 2500);
    }
	
	private void helpspawn2(int npcid) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				attackPlayer((Npc) spawn(npcid,  639f, 494f, 136f, (byte)0));
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
	
	private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}
}