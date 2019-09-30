/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.elementisForest;

import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("seed_hetgolem")
public class SeedHetgolemAI2 extends AggressiveNpcAI2 {

    @Override
    public void handleDied() {
        WorldPosition p = getPosition();
        if (p != null && p.getWorldMapInstance() != null) {
            spawn(282441, p.getX(), p.getY(), p.getZ(), p.getH());
            Npc npc = (Npc) spawn(282465, p.getX(), p.getY(), p.getZ(), p.getH());
            CreatureActions.delete(npc);
        }
        super.handleDied();
        AI2Actions.deleteOwner(this);

    }

}
