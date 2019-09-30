/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_SHIELD_EFFECT;
import com.ne.gs.services.SiegeService;
import com.ne.gs.world.knownlist.Visitor;

/**
 * @author Source
 */
@AIName("siege_shieldnpc")
public class ShieldNpcAI2 extends SiegeNpcAI2 {

    @Override
    protected void handleDespawned() {
        sendShieldPacket(false);
        super.handleDespawned();
    }

    @Override
    protected void handleSpawned() {
        sendShieldPacket(true);
        super.handleSpawned();
    }

    private void sendShieldPacket(boolean shieldStatus) {
        int id = getSpawnTemplate().getSiegeId();
        SiegeService.getInstance().getFortress(id).setUnderShield(shieldStatus);

        final SM_SHIELD_EFFECT packet = new SM_SHIELD_EFFECT(id);
        getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

            @Override
            public void visit(Player player) {
                player.sendPck(packet);
            }

        });
    }

}
