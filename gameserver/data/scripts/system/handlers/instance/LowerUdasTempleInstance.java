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
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;

@InstanceID(300160000)
public class LowerUdasTempleInstance extends GeneralInstanceHandler {

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        int rnd = Rnd.get(1, 100);
        if (rnd > 80) { // spawn named drop chests, 20% both, 30% epic, 50% fabled chest
            spawn(216150, 455.984f, 1192.506f, 190.221f, (byte) 116);
            spawn(216645, 435.664f, 1182.577f, 190.221f, (byte) 116);
        } else if (rnd > 50) {
            spawn(216150, 455.984f, 1192.506f, 190.221f, (byte) 116);
        } else {
            spawn(216645, 435.664f, 1182.577f, 190.221f, (byte) 116);
        }
    }
	
    @Override
    public void onDie(Npc npc) {
        final int npcId = npc.getNpcId();
        switch (npcId) {
            case 215792:
				spawn(700966, 1145.610f, 354.125f, 110.077f, (byte) 21);
                break;
        }
    }
	
    @Override
    public void onLeaveInstance(Player player) {
		removeItems(player);
    }

    @Override
    public void onPlayerLogOut(Player player) {
        removeItems(player);
    }

    private void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000087, storage.getItemCountByItemId(185000087));
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
}
