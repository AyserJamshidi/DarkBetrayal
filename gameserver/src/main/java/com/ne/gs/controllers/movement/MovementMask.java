/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.controllers.movement;

/**
 * @author Mr. Poke
 */
public final class MovementMask {

    public static final byte IMMEDIATE = 0; // 0
    public static final byte GLIDE = (byte) 0x04; // 4
    public static final byte FALL = (byte) 0x08; // 8
    public static final byte VEHICLE = (byte) 0x10; // 16
    public static final byte MOUSE = (byte) 0x20; // 32
    public static final byte STARTMOVE = (byte) 0xC0; // 192
    public static final byte NPC_WALK_SLOW = -22;
    public static final byte NPC_WALK_FAST = -24;
    public static final byte NPC_RUN_SLOW = -28;
    public static final byte NPC_RUN_FAST = -30;
    public static final byte NPC_STARTMOVE = -32;

    /**
     * -60 = Gliding, changing direction
     * -124 = Gliding, not changing direction
     *
     * -64 = Flying, not changing direction
     * -128 = Flying, changing direction
     */
    public static final byte GLIDE_START_WHILE_FLYING = -60;
    public static final byte GLIDE_MOVE_WHILE_FLYING = -124;
}
