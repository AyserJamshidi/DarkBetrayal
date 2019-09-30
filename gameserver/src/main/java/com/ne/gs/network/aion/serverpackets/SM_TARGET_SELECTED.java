/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;

/**
 * @author Sweetkr
 */
public class SM_TARGET_SELECTED extends AionServerPacket {

    @SuppressWarnings("unused")
    private final Player player;
    private final int level;
    private final int maxHp;
    private final int currentHp;
    private int targetObjId;

    public SM_TARGET_SELECTED(){
        player = null;
        targetObjId = 0;
        level = 0;
        maxHp = 0;
        currentHp = 0;
    }

    public SM_TARGET_SELECTED(Player player) {
        this.player = player;
        if (player.getTarget() instanceof Creature) {
            level = ((Creature) player.getTarget()).getLevel();
            maxHp = ((Creature) player.getTarget()).getLifeStats().getMaxHp();
            currentHp = ((Creature) player.getTarget()).getLifeStats().getCurrentHp();
        } else {
            // TODO: check various gather on retail
            level = 0;
            maxHp = 0;
            currentHp = 0;
        }

        if (player.getTarget() != null) {
            targetObjId = player.getTarget().getObjectId();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(AionConnection con) {
        writeD(targetObjId);
        writeH(level);
        writeD(maxHp);
        writeD(currentHp);
    }
}
