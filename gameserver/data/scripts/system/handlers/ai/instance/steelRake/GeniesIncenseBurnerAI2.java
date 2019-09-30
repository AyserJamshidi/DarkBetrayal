/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.steelRake;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;

/**
 * @author xTz
 */
@AIName("geniesincenseburner")
public class GeniesIncenseBurnerAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        AI2Actions.targetSelf(this);
        AI2Actions.useSkill(this, 18465);
    }
}
