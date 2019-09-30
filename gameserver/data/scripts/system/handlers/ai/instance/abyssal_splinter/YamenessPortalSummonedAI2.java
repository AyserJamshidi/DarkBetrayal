/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2014, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.abyssal_splinter;

import ai.AggressiveNpcAI2;
import java.util.concurrent.Future;

import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.EmotionType;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;


@AIName("yamenessportal")
public class YamenessPortalSummonedAI2 extends AggressiveNpcAI2 {
	
	private Future<?> phaseTask;

	/*
    @Override
    protected void handleSpawned() {
		phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelPhaseTask();
                } else {
                    attackPlayer((Npc) spawn(281903, getOwner().getX() + 3, getOwner().getY() - 3, getOwner().getZ(), (byte) 0));
                }
            }

        }, 9000, 12000);
    }
	 */

    protected void handleSpawned() {
        phaseTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelPhaseTask();
                } else {
                    attackPlayer((Npc) spawn(281903, getOwner().getX() + 3, getOwner().getY() - 3, getOwner().getZ(), (byte) 0));
                }
            }

        }, 7000);
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
	
	private void cancelPhaseTask() {
        if (phaseTask != null && !phaseTask.isCancelled()) {
            phaseTask.cancel(true);
        }
    }
	
	@Override
    protected void handleDied() {
		cancelPhaseTask();
        super.handleDied();
    }
	
	@Override
    protected void handleDespawned() {
		cancelPhaseTask();
        super.handleDespawned();
    }
}
