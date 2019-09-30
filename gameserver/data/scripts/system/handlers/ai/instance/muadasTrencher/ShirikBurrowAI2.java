/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.muadasTrencher;

import java.util.concurrent.Future;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("shirik_burrow")
public class ShirikBurrowAI2 extends AggressiveNpcAI2 {

    private Future<?> lifeTask;

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleSpawned() {
        startLifeTask();
        super.handleSpawned();
    }

    @Override
    protected void handleDespawned() {
        cancelLifeTask();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        cancelLifeTask();
        super.handleDied();
        AI2Actions.deleteOwner(this);
    }

    private void startLifeTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    rndSpawn(282535);
                    rndSpawn(282535);
                    AI2Actions.deleteOwner(ShirikBurrowAI2.this);
                }
            }

        }, 25000);
    }

    private void rndSpawn(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(1, 2);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        Npc npc = (Npc) spawn(npcId,  p.getX() + Rnd.get(-4, 4), p.getY() + Rnd.get(-4, 4), p.getZ(), (byte) 0);
        NpcShoutsService.getInstance().sendMsg(npc, 1500307, npc.getObjectId(), 0, 1000);
    }

    private void cancelLifeTask() {
        if (lifeTask != null && !lifeTask.isDone()) {
            lifeTask.cancel(true);
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
}
