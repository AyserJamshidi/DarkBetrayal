/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.walkers;

import org.apache.commons.lang3.ArrayUtils;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.handler.MoveEventHandler;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.gameobjects.state.CreatureState;
import com.ne.gs.utils.MathUtil;

/**
 * @author Rolandas
 */
@AIName("polorserin")
public class PolorSerinAI2 extends WalkGeneralRunnerAI2 {

    static final int[] stopAdults = {203129, 203132};

    @Override
    protected void handleMoveArrived() {
        boolean adultsNear = false;
        for (VisibleObject object : getOwner().getKnownList().getKnownObjects().values()) {
            if (object instanceof Npc) {
                Npc npc = (Npc) object;
                if (!ArrayUtils.contains(stopAdults, npc.getNpcId())) {
                    continue;
                }
                if (MathUtil.isIn3dRange(npc, getOwner(), getOwner().getAggroRange())) {
                    adultsNear = true;
                    break;
                }
            }
        }
        if (adultsNear) {
            MoveEventHandler.onMoveArrived(this);
            getOwner().unsetState(CreatureState.WEAPON_EQUIPPED);
        } else {
            super.handleMoveArrived();
            getOwner().setState(CreatureState.WEAPON_EQUIPPED);
        }
    }
}
