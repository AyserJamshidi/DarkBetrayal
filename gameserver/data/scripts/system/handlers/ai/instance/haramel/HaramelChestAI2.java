/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.haramel;

import ai.ChestAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("haramelchest")
public class HaramelChestAI2 extends ChestAI2 {

    @Override
    protected void handleDespawned() {
        WorldPosition p = getPosition();
        if (p != null && p.getWorldMapInstance() != null) {
            spawn(700852, 224.598f, 331.143f, 141.892f, (byte) 90);
        }
        super.handleDespawned();
    }
}
