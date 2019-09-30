/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.clientpackets;

import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.templates.spawns.SpawnSearchResult;
import com.ne.gs.network.aion.AionClientPacket;
import com.ne.gs.network.aion.serverpackets.SM_SHOW_NPC_ON_MAP;

/**
 * @author Lyahim
 */
public class CM_OBJECT_SEARCH extends AionClientPacket {

    private int npcId;

    /**
     * Nothing to do
     */
    @Override
    protected void readImpl() {
        npcId = readD();
    }

    /**
     * Logging
     */
    @Override
    protected void runImpl() {
        SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(0, npcId);
        if (searchResult != null) {
            sendPacket(new SM_SHOW_NPC_ON_MAP(npcId, searchResult.getWorldId(), searchResult.getSpot().getX(), searchResult.getSpot().getY(), searchResult
                .getSpot().getZ()));
        }
    }
}
