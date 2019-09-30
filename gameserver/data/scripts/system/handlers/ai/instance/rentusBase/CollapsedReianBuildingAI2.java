/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("collapsed_reian_building")
public class CollapsedReianBuildingAI2 extends NpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        SkillEngine.getInstance().getSkill(getOwner(), 20088, 60, getOwner()).useNoAnimationSkill();
    }

}
