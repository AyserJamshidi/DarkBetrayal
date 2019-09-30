/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package com.ne.gs.network.aion.clientpackets;

import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.AionClientPacket;
import com.ne.gs.network.aion.serverpackets.SM_CHAT_WINDOW;
import com.ne.gs.world.World;

/**
 * @author prix
 */
public class CM_CHAT_WINDOW extends AionClientPacket {

    private String playerName;
    @SuppressWarnings("unused")
    private int unk;

    @Override
    protected void readImpl() {
        playerName = readS();
        unk = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        Player target = World.getInstance().findPlayer(playerName);

        if (target == null) {
            return;
        }

        player.sendPck(new SM_CHAT_WINDOW(target));
    }
}
