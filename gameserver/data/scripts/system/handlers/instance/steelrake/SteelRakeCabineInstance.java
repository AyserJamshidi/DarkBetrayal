/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.steelrake;

import com.ne.commons.utils.Rnd;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.services.teleport.TeleportService;

@InstanceID(300460000)
public class SteelRakeCabineInstance extends GeneralInstanceHandler {
	
    @Override
    public void onLeaveInstance(Player player) {
        removeItems(player);
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        if (Rnd.get(1, 2) == 1) { // Sweeper Nunukin
            spawn(219026, 353.814f, 491.557f, 949.466f, (byte) 119);
        } else {
            spawn(219026, 354.7875f, 536.17004f, 949.4662f, (byte) 7);
        }
        int chance = Rnd.get(1, 2);
        // Madame Bovariki + Steel Rake Shaman
        spawn(chance == 1 ? 219032 : 219003, 463.124f, 512.75f, 952.545f, (byte) 0);
        spawn(chance == 1 ? 219003 : 219032, 502.859f, 548.55f, 952.417f, (byte) 85);
    }
	
    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
            case 730199:
					TeleportService.teleportTo(player, 300460000, 702.11993f, 500.80948f, 940.60675f, (byte) 60, TeleportAnimation.BEAM_ANIMATION);
                break;
        }
    }
	
    private void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000076, storage.getItemCountByItemId(185000076));
		storage.decreaseByItemId(185000047, storage.getItemCountByItemId(185000047));
    }
	
    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
}
