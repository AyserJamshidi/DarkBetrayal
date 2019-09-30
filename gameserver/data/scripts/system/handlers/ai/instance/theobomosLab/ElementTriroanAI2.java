package ai.instance.theobomosLab;

import java.util.concurrent.atomic.AtomicBoolean;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.model.EmotionType;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.skillengine.SkillEngine;

@AIName("elementTriroan")
public class ElementTriroanAI2 extends NpcAI2 {

    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        movienpc();
    }

    @Override
    protected void handleMoveArrived() {
        super.handleMoveArrived();
        getMoveController().abortMove();
        if (isDestroyed.compareAndSet(false, true)) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					if (!isAlreadyDead()) {
						SkillEngine.getInstance().getSkill(getOwner(), getGravSkill(), 44, getOwner()).useNoAnimationSkill();
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							public void run() {
								AI2Actions.deleteOwner(ElementTriroanAI2.this);
							}
						}, 3200);
					}
				}

			}, 1000);
        }
    }

    private void movienpc() {
        switch (getNpcId()) {
            case 280975:
                ((AbstractAI) getOwner().getAi2()).setStateIfNot(AIState.WALKING);
                WalkManager.startWalking((NpcAI2) getOwner().getAi2());
                getOwner().setState(1);
                PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
                getOwner().getMoveController().moveToPoint(621f, 475f, 196.66f);
                break;
            case 280976:
                ((AbstractAI) getOwner().getAi2()).setStateIfNot(AIState.WALKING);
                WalkManager.startWalking((NpcAI2) getOwner().getAi2());
                getOwner().setState(1);
                PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
                getOwner().getMoveController().moveToPoint(620f, 500f, 196.66f);
                break;
            case 280977:
                ((AbstractAI) getOwner().getAi2()).setStateIfNot(AIState.WALKING);
                WalkManager.startWalking((NpcAI2) getOwner().getAi2());
                getOwner().setState(1);
                PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
                getOwner().getMoveController().moveToPoint(582f, 499f, 196.66f);
                break;
            case 280978:
                ((AbstractAI) getOwner().getAi2()).setStateIfNot(AIState.WALKING);
                WalkManager.startWalking((NpcAI2) getOwner().getAi2());
                getOwner().setState(1);
                PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
                getOwner().getMoveController().moveToPoint(581f, 477f, 196.66f);
                break;
        }
    }

    private int getGravSkill() {
        switch (getNpcId()) {
            case 280975:
                return 18486;
            case 280976:
                return 18483;
			case 280977:
                return 18485;
			case 280978:
                return 18484;
            default:
                return 0;
        }
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

    @Override
    protected void handleDied() {
        super.handleDied();
        AI2Actions.deleteOwner(this);
    }
}