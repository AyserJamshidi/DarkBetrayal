/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.controllers.movement;

import com.ne.gs.configs.main.FallDamageConfig;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.utils.stats.StatFunctions;

/**
 * @author ATracer
 */
public class PlayerMoveController extends PlayableMoveController<Player> {

    private float fallDistance;
    private float lastFallZ;

    public PlayerMoveController(Player owner) {
        super(owner);
        beginX = owner.getX();
        beginY = owner.getY();
        beginZ = owner.getZ();
    }

    private float beginX, beginY, beginZ;

    @Override
    public float beginX(){
        return beginX;
    }

    @Override
    public float beginY(){
        return beginY;
    }

    @Override
    public float beginZ(){
        return beginZ;
    }

    public void setBegin(float x, float y, float z){
        beginX = x;
        beginY = y;
        beginZ = z;
    }

    public void updateFalling(float newZ) {
        if (lastFallZ != 0) {
            fallDistance += lastFallZ - newZ;
            if (fallDistance >= FallDamageConfig.MAXIMUM_DISTANCE_MIDAIR) {
                StatFunctions.calculateFallDamage(owner, fallDistance, false);
            }
        }
        lastFallZ = newZ;
    }

    public void stopFalling(float newZ) {
        if (lastFallZ != 0) {
            if (!owner.isFlying()) {
                StatFunctions.calculateFallDamage(owner, fallDistance, true);
            }
            fallDistance = 0;
            lastFallZ = 0;
        }
    }

}
