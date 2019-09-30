/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.world.zone;

import java.util.List;

public enum ZoneAttributes {
    BIND(1),
    RECALL(2),
    GLIDE(4),
    FLY(8),
    RIDE(16),
    FLY_RIDE(32),
    PVP_ENABLED(64),
    DUEL_SAME_RACE_ENABLED(128),
    DUEL_OTHER_RACE_ENABLED(256);

    private final int id;

    private ZoneAttributes(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Integer fromList(List<ZoneAttributes> flagValues) {
        Integer result = 0;
        for (ZoneAttributes attribute : ZoneAttributes.values()) {
            if (flagValues.contains(attribute))
                result |= attribute.getId();
        }
        return result;
    }
}
