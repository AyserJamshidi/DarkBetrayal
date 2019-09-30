/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.commons.utils.Rnd;

@InstanceID(320130000)
public class AdmaStrongholdInstance extends GeneralInstanceHandler {
	
    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
            switch (Rnd.get(1, 4)) {
                case 1:
                    spawn(205224, 478f, 397f, 187.6f, (byte) 58);
                    break;
                case 2:
                    spawn(205224, 347f, 559f, 180.4f, (byte) 102);
                    break;
                case 3:
                    spawn(205224, 529f, 546f, 189.6f, (byte) 50);
                    break;
                case 4:
                    break;
            }
    }
	
    @Override
    public void onLeaveInstance(Player player) {
        removeItems(player);
    }
	
    private void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000026, storage.getItemCountByItemId(185000026));
        storage.decreaseByItemId(185000027, storage.getItemCountByItemId(185000027));
		storage.decreaseByItemId(185000028, storage.getItemCountByItemId(185000028));
        storage.decreaseByItemId(185000029, storage.getItemCountByItemId(185000029));
		storage.decreaseByItemId(185000030, storage.getItemCountByItemId(185000030));
        storage.decreaseByItemId(185000031, storage.getItemCountByItemId(185000031));
		storage.decreaseByItemId(185000032, storage.getItemCountByItemId(185000032));
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
                true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }

    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        player.sendPck( SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 451.1f, 202.9f, 167.9f, (byte) 30);
        return true;
    }
}
