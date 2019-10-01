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
import com.ne.gs.model.stats.container.CreatureLifeStats;
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
    private int maxMp;
    private int currentMp;
    private int targetObjId;

    public SM_TARGET_SELECTED(){
        this.player = null;
        this.targetObjId = 0;
        this.level = 0;
        this.maxHp = 0;
        this.currentHp = 0;
        this.maxMp = 0;
        this.currentMp = 0;
    }

    public SM_TARGET_SELECTED(Player player) {
        this.player = player;
        if (player.getTarget() instanceof Creature) {
            Creature target = (Creature) player.getTarget();
            CreatureLifeStats<? extends Creature> targetStats = target.getLifeStats();

            this.level = target.getLevel();
            this.maxHp = targetStats.getMaxHp();
            this.currentHp = targetStats.getCurrentHp();
            this.maxMp = targetStats.getMaxMp();
            this.currentMp = targetStats.getCurrentMp();
        } else {
            // TODO: check various gather on retail
            this.level = 0;
            this.maxHp = 0;
            this.currentHp = 0;
            this.maxMp = 0;
            this.currentMp = 0;
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
        writeD(maxMp);
        writeD(currentMp);

        player.sendMsg("targetObjID = " + targetObjId);
    }
}
