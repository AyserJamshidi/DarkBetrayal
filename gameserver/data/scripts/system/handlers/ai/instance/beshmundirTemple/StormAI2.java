package ai.instance.beshmundirTemple;

import java.util.concurrent.Future;
import java.util.List;
import ai.NoActionAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AISubState;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.skillengine.SkillEngine;

@AIName("stormR")
public class StormAI2 extends NoActionAI2 {

    private Future<?> lifeTask;
	private Future<?> skillTask;

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    getMoveController().abortMove();
                    setSubStateIfNot(AISubState.WALK_RANDOM);
                    setStateIfNot(AIState.WALKING);
                    float direction = Rnd.get(0, 199) / 100f;
                    float x1 = (float) (Math.cos(Math.PI * direction) * 15);
                    float y1 = (float) (Math.sin(Math.PI * direction) * 15);
                    WorldPosition p = getPosition();
                    if (p != null && p.getWorldMapInstance() != null) {
                        getMoveController().moveToPoint(p.getX() + x1, p.getY() + y1, p.getZ());
                        getOwner().setState(1);
                        PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
                    }
                }
            }

        }, 0, 5000);
        startLifeTask();
		startSkillTask();
    }

    private void startSkillTask() {
        skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			
            public void run() {
				SkillEngine.getInstance().getSkill(getOwner(), 18619, 55, getOwner()).useSkill();
			}
        }, 2000, 1000);
    }
	
    private void startLifeTask() {
        lifeTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
			
            public void run() {
				WorldPosition p = getPosition();
				WorldMapInstance instance = p.getWorldMapInstance();
                deleteNpcs(instance.getNpcs(281795));
			}
        }, 20000);
    }
	
	private void cancelSkillTaskTask() {
        if (skillTask != null && !skillTask.isDone()) {
            skillTask.cancel(true);
        }
    }

    private void cancelTask() {
        if (lifeTask != null && !lifeTask.isDone()) {
            lifeTask.cancel(true);
        }
    }
	
	private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

    @Override
    protected void handleDespawned() {
        cancelSkillTaskTask();
		cancelTask();
        super.handleDespawned();
    }

    @Override
    public void handleDied() {
        cancelSkillTaskTask();
		cancelTask();
        super.handleDied();
    }

    @Override
    public AIAnswer ask(AIQuestion question) {
        switch (question) {
            case CAN_RESIST_ABNORMAL:
                return AIAnswers.POSITIVE;
            default:
                return AIAnswers.NEGATIVE;
        }
    }

    @Override
    protected AIAnswer pollInstance(AIQuestion question) {
        switch (question) {
            case SHOULD_DECAY:
                return AIAnswers.NEGATIVE;
            case SHOULD_RESPAWN:
                return AIAnswers.NEGATIVE;
            case SHOULD_REWARD:
                return AIAnswers.NEGATIVE;
            default:
                return null;
        }
    }
}