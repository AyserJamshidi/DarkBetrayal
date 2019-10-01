/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.serverpackets;

import com.ne.gs.model.team2.TeamType;
import com.ne.gs.model.team2.common.legacy.LootGroupRules;
import com.ne.gs.model.team2.group.PlayerGroup;
import com.ne.gs.network.aion.AionConnection;
import com.ne.gs.network.aion.AionServerPacket;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Lyahim, ATracer, xTz
 */
public class SM_GROUP_INFO extends AionServerPacket {

    private final LootGroupRules lootRules;
    private final int groupId;
    private final int leaderId;
    private TeamType type;
    //private final int groupType; // deafult 0x3F; autogroup 0x02

    public SM_GROUP_INFO(PlayerGroup group) {
        groupId = group.getObjectId();
        leaderId = group.getLeader().getObjectId();
        lootRules = group.getLootGroupRules();
        type = group.getTeamType();
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeD(groupId);
        writeD(leaderId);
        writeD(con.getActivePlayer().getWorldId());
        writeD(lootRules.getLootRule().getId());
        writeD(lootRules.getMisc());
        writeD(lootRules.getCommonItemAbove());
        writeD(lootRules.getSuperiorItemAbove());
        writeD(lootRules.getHeroicItemAbove());
        writeD(lootRules.getFabledItemAbove());
        writeD(lootRules.getEthernalItemAbove());
        writeD(lootRules.getAutodistribution().getId());
        writeD(0x02);
        writeC(0x00);
        writeD(type.getType());
        writeD(type.getSubType());
        writeD(0x00); // Message ID
        writeS(StringUtils.EMPTY); // Name
    }
}
