package ai.instance.darkPoeta;


import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;
import java.util.List;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.world.WorldPosition;

@AIName("atx")
public class AtxAI2 extends AggressiveNpcAI2 {
	
    private Future<?> basicSkillTask;
	private int stage = 0;
	private int stagetwo = 0;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        cancelSkillTask();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(280647));
		stage = 0;
		stagetwo = 0;
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        cancelSkillTask();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(280647));
		stage = 0;
		stagetwo = 0;
    }
	
	private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 99) {
			stage++;
			if (stage == 1) {
				startBasicSkillTask();
			}
        }
        if (hpPercentage <= 25) {
			stagetwo++;
			if (stagetwo == 1) {
				SpawnHelpers();
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
                    switch (Rnd.get(1, 7)) {
                        case 1:
								useSkill(16797);
                            break;
                        case 2:
								useSkill(16407);
                            break;
                        case 3:
								useSkill(16934);
                            break;
						case 4:
								useSkill(17530);
                            break;
                        case 5:
								useSkill(17521);
                            break;
                        case 6:
								useSkill(17520);
                            break;
                        case 7:
								useSkill(18376);
                            break;
                    } 
                }
            }

        }, 5000, 10000);
    }
	
	private void SpawnHelpers() {
		WorldPosition p = getPosition();
		attackPlayer((Npc) spawn(280647, p.getX() + Rnd.get(-3, 3), p.getY() + Rnd.get(-3, 3), p.getZ(), (byte) 0));
		attackPlayer((Npc) spawn(280647, p.getX() + Rnd.get(-3, 3), p.getY() + Rnd.get(-3, 3), p.getZ(), (byte) 0));
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

    private void cancelSkillTask() {
        if (basicSkillTask != null && !basicSkillTask.isCancelled()) {
            basicSkillTask.cancel(true);
        }
    }
	
	private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }
}