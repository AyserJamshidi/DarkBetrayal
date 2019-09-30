package ai.instance.darkPoeta;

import java.util.concurrent.Future;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AISubState;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.skillengine.SkillEngine;

@AIName("DarkPoetaGeneratorCore")
public class DarkPoetaGeneratorCoreAI2 extends AggressiveNpcAI2 {

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

        }, 0, 8000);
    }

    private int npcSkill() {
        switch (getNpcId()) {
            case 281090:
                return 18125;
            default:
                return 0;
        }
    }

    private void startSkillTask() {
        Creature mostHated = getAggroList().getMostHated();
        if (isAlreadyDead() || mostHated == null) {
            cancelSkillTaskTask();
        } else {
            skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate( new Runnable() {

                public void run() {
                    SkillEngine.getInstance().getSkill( getOwner(), npcSkill(), 55, getOwner() ).useSkill();
                }
            }, 2500, 2500 );
        }
    }
	
	private void cancelSkillTaskTask() {
        if (skillTask != null && !skillTask.isDone()) {
            skillTask.cancel(true);
        }
    }

    @Override
    protected void handleDespawned() {
        cancelSkillTaskTask();
        super.handleDespawned();
    }

    @Override
    public void handleDied() {
        cancelSkillTaskTask();
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