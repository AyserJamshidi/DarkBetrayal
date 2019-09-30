/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.padmarashkasCave;

import ai.NoActionAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;

import java.util.concurrent.Future;

@AIName("padmarashkaegg")
public class PadmarashkaEggAI2 extends NoActionAI2 {

	private Future<?> startTask;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        switch (this.getNpcId()) {
            case 282613:
                smallEggSpawn();
                break;
            case 282614:
                hugeEggSpawn();
                break;
        }
    }

    private void smallEggSpawn() {
        startTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (getOwner() != null && !isAlreadyDead()) {
                    AI2Actions.deleteOwner(PadmarashkaEggAI2.this);
					attackPlayer((Npc) spawn(282616, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0));
                }

            }
        }, 30000); //TODO: Need right value
    }

    private void hugeEggSpawn() {
        startTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (getOwner() != null && !isAlreadyDead()) {
                    AI2Actions.deleteOwner(PadmarashkaEggAI2.this);
					attackPlayer((Npc) spawn(282620, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0));
                }
            }
        }, 30000); //TODO: Need right value
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
	
	private void cancelStartTask() {
        if (startTask != null && !startTask.isCancelled()) {
            startTask.cancel(true);
        }
    }
	
    @Override
    protected void handleDied() {
        super.handleDied();
		cancelStartTask();
    }
	
    @Override
    protected void handleDespawned() {
        super.handleDespawned();
		cancelStartTask();
    }
}
