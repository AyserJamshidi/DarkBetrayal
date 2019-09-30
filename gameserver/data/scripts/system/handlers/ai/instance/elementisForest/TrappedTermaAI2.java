/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.elementisForest;

import com.ne.gs.ai2.*;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.utils.PacketSendUtility;

import java.lang.Override;
import java.lang.Runnable;

/**
 * @author Luzien
 */
@AIName("terma")
public class TrappedTermaAI2 extends NpcAI2 {

    private boolean isMove;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (!isMove) {
            isMove = true;
            moveToDead();
        }
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        Npc freeTerma = (Npc)spawn(205495, 455.94f, 537.06f, 132.6f,(byte) 0);
        spawn(701009, 451.706f, 534.313f, 131.979f, (byte) 0);
        NpcShoutsService.getInstance().sendMsg(freeTerma, 1500444, freeTerma.getObjectId(), 0, 3000);
    }

    private void moveToDead() {
        ((AbstractAI) getOwner().getAi2()).setStateIfNot(AIState.WALKING);
        WalkManager.startWalking((NpcAI2) getOwner().getAi2());
        getOwner().setState(1);
        getOwner().getMoveController().moveToPoint(455.93f, 537.2f, 132.55f);
        PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
        dead();
    }

    private void dead() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                getOwner().getController().die();
            }

        }, 16000);
    }

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    public int modifyDamage(int damage) {
        return 1;
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

