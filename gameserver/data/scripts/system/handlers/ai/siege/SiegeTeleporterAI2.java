/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import ai.GeneralNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.siege.SiegeNpc;
import com.ne.gs.network.aion.serverpackets.SM_FORTRESS_INFO;
import com.ne.gs.services.SiegeService;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author Source
 */
@AIName("siege_teleporter")
public class SiegeTeleporterAI2 extends GeneralNpcAI2 {

    @Override
    protected void handleDespawned() {
        canTeleport(false);
        super.handleDespawned();
    }

    @Override
    protected void handleSpawned() {
        canTeleport(true);
        super.handleSpawned();
    }

    private void canTeleport(final boolean status) {
        final int id = ((SiegeNpc) getOwner()).getSiegeId();
        SiegeService.getInstance().getFortress(id).setCanTeleport(status);

        getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(new SM_FORTRESS_INFO(id, status));
            }

        });
    }

}
