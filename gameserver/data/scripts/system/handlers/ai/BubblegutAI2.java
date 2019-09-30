/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;

/**
 * @author Luzien
 */
@AIName("bubblegut")
public class BubblegutAI2 extends GeneralNpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        AI2Actions.useSkill(this, 16447);
    }

}
