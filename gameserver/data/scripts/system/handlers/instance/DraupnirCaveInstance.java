/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import com.ne.commons.utils.Rnd;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;


@InstanceID(320080000)
public class DraupnirCaveInstance extends GeneralInstanceHandler {
	
    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        if (Rnd.get(1, 100) > 35) { // Драконид-главарь 47-го отряда
            spawn(213771, 497.1218f, 434.713f, 616.584f, (byte) 71);
        } else { // Драконид-пехотинец 47-го легиона
            spawn(213380, 497.1218f, 434.1912f, 616.67548f, (byte) 71);
        }
        if (Rnd.get(1, 100) > 35) { // Драконид-врач 47-го отряда
            spawn(213773, 380.694f, 611.956f, 598.523f, (byte) 98);
        } else { // Драконид-пехотинец 47-го легиона
            spawn(213380, 380.694f, 611.956f, 598.523f, (byte) 98);
        }
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
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 491.7f, 370.9f, 623.2f, (byte) 25);
        return true;
    }
}
