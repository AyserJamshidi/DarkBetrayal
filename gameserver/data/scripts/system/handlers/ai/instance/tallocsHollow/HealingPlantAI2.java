/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.tallocsHollow;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;

/**
 * @author xTz
 */
@AIName("healingplant")
public class HealingPlantAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        switch (getNpcId()) {
            case 700940:
                player.getLifeStats().increaseHp(TYPE.HP, 20000);
                break;
            case 700941:
                player.getLifeStats().increaseHp(TYPE.HP, 30000);
                break;
        }
        AI2Actions.deleteOwner(this);
    }

}
