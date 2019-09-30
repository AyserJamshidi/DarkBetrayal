/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.pvparenas;

import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.world.WorldMapInstance;

/**
 * @author xTz
 */
@InstanceID(300430000)
public class DisciplineTrainingGroundsInstance extends PvPArenaInstance {

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        killBonus = 200;
        deathFine = -100;
        super.onInstanceCreate(instance);
    }

}
