/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.empyreanCrucible;

import ai.GeneralNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.EmotionType;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("arminos_draky")
public class ArminosDrakyAI2 extends GeneralNpcAI2 {

    private final String walkerId = "300300001";
    private boolean isStart = true;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        getSpawnTemplate().setWalkerId(walkerId);
        WalkManager.startWalking(this);
        getOwner().setState(1);
        PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
    }

    @Override
    protected void handleMoveArrived() {
        int point = getOwner().getMoveController().getCurrentPoint();
        super.handleMoveArrived();
        if (point == 15) { // circle twice
            if (!isStart) {
                getSpawnTemplate().setWalkerId(null);
                WalkManager.stopWalking(this);
                AI2Actions.deleteOwner(this);
            } else {
                isStart = false;
            }
        }
    }

    @Override
    public AIAnswer ask(AIQuestion question) {
        switch (question) {
            case CAN_RESIST_ABNORMAL:
                return AIAnswers.POSITIVE;
            case SHOULD_REWARD_AP:
                return AIAnswers.POSITIVE;
            default:
                return AIAnswers.NEGATIVE;
        }
    }

    @Override
    public boolean canThink() {
        return false;
    }
}
