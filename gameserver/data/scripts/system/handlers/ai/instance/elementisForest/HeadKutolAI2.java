package ai.instance.elementisForest;

import ai.AggressiveNpcAI2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;

@AIName("head_kutol")
public class HeadKutolAI2 extends AggressiveNpcAI2 {
	
	protected List<Integer> percents = new ArrayList<>();
    private final AtomicBoolean isHome = new AtomicBoolean(true);
	
    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
        if (isHome.compareAndSet(true, false)) {
            sendMsg(1500451);
        }
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 90, 70, 60, 45, 30);
    }
	
    private void checkPercentage(int hpPercentage) {
        for (Iterator<Integer> it = percents.iterator(); it.hasNext(); ) {
            int percent = it.next();
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 90:
						useSkill(19507);
                        sendMsg(1500452);
						rndSpawn(282302);
                        rndSpawn(282302);
						rndSpawn(282302);
                        break;
                    case 70:
						useSkill(19507);
                        sendMsg(1500452);
						rndSpawn(282302);
                        rndSpawn(282302);
						rndSpawn(282302);
                        break;
                    case 60:
						useSkill(19507);
                        sendMsg(1500452);
						rndSpawn(282302);
						rndSpawn(282302);
                        rndSpawn(282302);
                        rndSpawn(282302);
                        break;
                    case 45:
						useSkill(19507);
                        sendMsg(1500452);
						rndSpawn(282302);
						rndSpawn(282302);
                        break;
                    case 30:
						useSkill(19508);
                        sendMsg(1500452);
						rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        rndSpawn(282303);
                        break;
                }
                it.remove();
                break;
            }
        }
    }
	
	private void rndSpawn(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		int distance = Rnd.get(4, 6);
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		attackPlayer((Npc) spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0));
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
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 60, getOwner()).useSkill();
	}

    private void sendMsg(int msg) {
	    NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }
	
    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }
	
    protected void handleDied() {
        super.handleDied();
		despawn(282302);
		despawn(282303);
		despawn(282304);
		despawn(282305);
		despawn(282306);
        sendMsg(1500453);
    }
	
    @Override
    protected void handleBackHome() {
		addPercent();
		despawn(282302);
		despawn(282303);
		despawn(282304);
		despawn(282305);
		despawn(282306);
        isHome.set(true);
        super.handleBackHome();
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		if (!npc.equals(this.getOwner())){
			npc.getController().onDelete();
		}
	  }
	}
}