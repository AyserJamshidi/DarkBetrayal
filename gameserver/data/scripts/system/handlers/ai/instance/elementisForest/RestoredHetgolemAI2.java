/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.elementisForest;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AISubState;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("restored_hetgolem")
public class RestoredHetgolemAI2 extends AggressiveNpcAI2 {

    private Future<?> lifeTask;
    private final AtomicBoolean isStartEvent = new AtomicBoolean(false);

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    getMoveController().abortMove();
                    setSubStateIfNot(AISubState.WALK_RANDOM);
                    setStateIfNot(AIState.WALKING);
                    float direction = Rnd.get(0, 199) / 100f;
                    float x1 = (float) (Math.cos(Math.PI * direction) * 8);
                    float y1 = (float) (Math.sin(Math.PI * direction) * 8);
                    WorldPosition p = getPosition();
                    if (p != null && p.getWorldMapInstance() != null) {
                        getMoveController().moveToPoint(p.getX() + x1, p.getY() + y1, p.getZ());
                        getOwner().setState(1);
                        PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
                    }
                }
            }

        }, 3000);
        startLifeTask();
    }

    private void startLifeTask() {
        lifeTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    spawnEvent();
                }
            }

        }, 5000);
    }

    private void cancelTask() {
        if (lifeTask != null && !lifeTask.isDone()) {
            lifeTask.cancel(true);
        }
    }

    private void spawnEvent() {
        if (isStartEvent.compareAndSet(false, true)) {
            WorldPosition p = getPosition();
            if (p != null && p.getWorldMapInstance() != null) {
                spawn(282308, p.getX(), p.getY(), p.getZ(), p.getH());
                Npc npc = (Npc) spawn(282465, p.getX(), p.getY(), p.getZ(), p.getH());
                CreatureActions.delete(npc);
            }
            AI2Actions.deleteOwner(this);
        }
    }

    @Override
    protected void handleDespawned() {
        cancelTask();
        super.handleDespawned();
    }

    @Override
    public void handleDied() {
        cancelTask();
        spawnEvent();
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
