/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.inggison;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;

/**
 * @author Luzien
 */
@AIName("omegaclone")
public class CloneOfBarrierAI2 extends AggressiveNpcAI2 {

    @Override
    protected void handleDied() {
        for (VisibleObject object : getKnownList().getKnownObjects().values()) {
            if (object instanceof Npc && isInRange(object, 5)) {
                Npc npc = (Npc) object;
                if (npc.getNpcId() == 216516 && !npc.getLifeStats().isAlreadyDead()) {
                    npc.getEffectController().removeEffect(18671);
                    break;
                }
            }
        }
        super.handleDied();
    }

}
