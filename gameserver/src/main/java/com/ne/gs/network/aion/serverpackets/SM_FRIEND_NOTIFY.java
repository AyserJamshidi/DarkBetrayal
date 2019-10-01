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
 * Notifies players when their friends log in, out, or delete them
 *
 * @author Ben
 */
public class SM_FRIEND_NOTIFY extends AionServerPacket {

    /**
     * Buddy has logged in (Or become visible)
     */
    public static final int LOGIN = 0;

    /**
     * Buddy has logged out (Or become invisible)
     */
    public static final int LOGOUT = 1;

    /**
     * Buddy has deleted you
     */
    public static final int DELETED = 2;

    private final int code;
    private final String name;

    /**
     * Constructs a new notify packet
     *
     * @param code
     *     Message code
     * @param name
     *     Name of friend
     */
    public SM_FRIEND_NOTIFY(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeS(name);
        writeC(code);
    }
}
