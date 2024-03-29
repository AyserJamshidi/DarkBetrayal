/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;

/**
 * @author Simple
 */
public class SM_PRIVATE_STORE_NAME extends AionServerPacket {

    /**
     * Private store Information *
     */
    private final int playerObjId;
    private final String name;

    public SM_PRIVATE_STORE_NAME(int playerObjId, String name) {
        this.playerObjId = playerObjId;
        this.name = name;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(playerObjId);
        writeS(name);
    }
}
