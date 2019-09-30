/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.inggison;

import ai.SummonerAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.ai.Percentage;
import com.ne.gs.model.ai.SummonGroup;
import com.ne.gs.model.gameobjects.player.Player;

/**
 * @author Luzien, xTz
 */
@AIName("omega")
public class OmegaAI2 extends SummonerAI2 {

    @Override
    protected void handleBeforeSpawn(Percentage percent) {
        AI2Actions.useSkill(this, 19189);
        AI2Actions.useSkill(this, 19191);
    }

    @Override
    protected void handleSpawnFinished(SummonGroup summonGroup) {
        if (summonGroup.getNpcId() == 281948) {
            AI2Actions.useSkill(this, 18671);
        }
    }

    @Override
    protected boolean checkBeforeSpawn() {
        boolean hit = false;
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (isInRange(player, 30)) {
                hit = true;
                break;
            }
        }
        return hit;
    }
}
