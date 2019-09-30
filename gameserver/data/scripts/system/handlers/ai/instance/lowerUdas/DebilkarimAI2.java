package ai.instance.lowerUdas;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;


@AIName("debilkarim")
public class DebilkarimAI2 extends AggressiveNpcAI2 {

    private int curentPercent = 100;
	private final List<Integer> percents = new ArrayList<>();
	private Future<?> series;
	private Future<?> fear;
	private int start = 0;

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
		start++;
		if (start == 1) {
			useSkill(18630);
			useSkill(18631);
		}
    }
	
    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 90, 50, 25);
    }
	
    @Override
    public void handleBackHome() {
        super.handleBackHome();
		start = 0;
		addPercent();
		cancelSeriesTask();
		cancelFearTask();
		curentPercent = 100;
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(281420));
				deleteNpcs(instance.getNpcs(215845));
            }
        }
    }
	
    @Override
    protected void handleDespawned() {
        super.handleDespawned();
		start = 0;
		percents.clear();
		cancelSeriesTask();
		cancelFearTask();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(281420));
				deleteNpcs(instance.getNpcs(215845));
            }
        }
    }
	
    @Override
    public void handleDied() {
        super.handleDied();
		start = 0;
		percents.clear();
		cancelSeriesTask();
		cancelFearTask();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(281420));
				deleteNpcs(instance.getNpcs(215845));
            }
        }
    }
	
	private synchronized void checkPercentage(int hpPercentage) {
        curentPercent = hpPercentage;
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 90:
						startSeriesTask();
                        break;
					case 50:
						cancelSeriesTask();
						SpawnHeal(281420);
						startFearTask();
                        break;
					case 25:
						SpawnHelpers(215845);
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }
	
    private void startSeriesTask() {
        series = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelSeriesTask();
                } else {
					useSkill(18634);
                }
            }

        }, 10000, 30000);
    }
	
	private void startFearTask() {
        fear = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelFearTask();
                } else {
					useSkill(18635);
                }
            }

        }, 25000, 40000);
    }
	
    private void SpawnHeal(int npcId) {
		WorldPosition p = getPosition();
		spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0);
		spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0);
		spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0);
		spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0);
		spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0);
		spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0);
    }
	
    private void SpawnHelpers(int npcId) {
		WorldPosition p = getPosition();
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
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
	
    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 53, getTarget()).useSkill();
	}
	
    private void cancelSeriesTask() {
        if (series != null && !series.isCancelled()) {
            series.cancel(true);
        }
    }
	
	private void cancelFearTask() {
        if (fear != null && !fear.isCancelled()) {
            fear.cancel(true);
        }
    }
}
