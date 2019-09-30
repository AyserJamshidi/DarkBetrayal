/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.argentManor;

import ai.GeneralNpcAI2;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.poll.AIAnswer;
import com.ne.gs.ai2.poll.AIAnswers;
import com.ne.gs.ai2.poll.AIQuestion;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("pletus_orb")
public class PletusOrbAI2 extends GeneralNpcAI2 {

    @Override
    public boolean canThink() {
        return false;
    }

    @Override
    protected void handleMoveArrived() {
        super.handleMoveArrived();
        getOwner().setState(Rnd.get(1, 2) == 1 ? 1 : 64);
        PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
        if (!isInRangeMagicalSap()) {
            if (Rnd.get(1, 100) >= 70) {
                WorldPosition p = getPosition();
                spawn(282148, p.getX(), p.getY(), p.getZ(), (byte) 0);
            }
        }
    }

    private boolean isInRangeMagicalSap() {
        for (VisibleObject obj : getKnownList().getKnownObjects().values()) {
            if (obj instanceof Npc) {
                Npc npc = (Npc) obj;
                if (isInRange(obj, 1) && npc.getNpcId() == 282148) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int modifyDamage(int damage) {
        return 1;
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
}
