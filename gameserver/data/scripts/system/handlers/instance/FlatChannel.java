/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.modules.housing.Housing;
import com.ne.gs.world.WorldMapInstance;

import static com.ne.gs.modules.housing.Housing.housing;

/**
 * @author hex1r0
 */
public abstract class FlatChannel extends GeneralInstanceHandler {

    @Override
    public void onInstanceCreate(WorldMapInstance ch) {
        super.onInstanceCreate(ch);

        housing().tell(new Housing.SpawnFlatTrigger(ch.getOwnerId(), ch.getInstanceId()));
    }

    @InstanceID(720010000)
    public static class Elyos extends FlatChannel {}

    @InstanceID(730010000)
    public static class Asmos extends FlatChannel {}
}
