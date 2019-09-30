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

@InstanceID(300100000)
public class SteelRakeInstance extends GeneralInstanceHandler {
	
    @Override
    public void onLeaveInstance(Player player) {
        removeItems(player);
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        if (Rnd.get(1, 2) == 1) { // Collector memekin
            spawn(215064, 747.065f, 458.293f, 942.354f, (byte) 60);
        } else { // Discerner werikiki
            spawn(215065, 728.008f, 541.524f, 942.354f, (byte) 59);
        }
        if (Rnd.get(1, 100) > 25) { // Pegureronerk
            spawn(798376, 516.198364f, 489.708008f, 885.760315f, (byte) 60);
        }
        if (Rnd.get(1, 2) == 1) { // Madame Bovariki
            spawn(215078, 460.904999f, 512.684998f, 952.549011f, (byte) 0);
        } else {
            spawn(215078, 477.534210f, 478.140991f, 951.703674f, (byte) 0);
        }
        int npcId = 0;
        switch (Rnd.get(1, 6)) {  // Special Delivery
            case 1:
                npcId = 215074;
                break;
            case 2:
                npcId = 215075;
                break;
            case 3:
                npcId = 215076;
                break;
            case 4:
                npcId = 215077;
                break;
            case 5:
                npcId = 215054;
                break;
            case 6:
                npcId = 215055;
                break;
        }
        spawn(npcId, 461.933350f, 510.545654f, 877.618103f, (byte) 90);
    }
	
    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
            case 730199:
					TeleportService.teleportTo(player, 300100000, 702.11993f, 500.80948f, 940.60675f, (byte) 60, TeleportAnimation.BEAM_ANIMATION);
                break;
        }
    }
	
    private void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000046, storage.getItemCountByItemId(185000046));
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
