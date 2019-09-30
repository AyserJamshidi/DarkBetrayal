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
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.commons.utils.Rnd;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;

@InstanceID(310110000)
public class TheobomosLabInstance extends GeneralInstanceHandler {
	
	private int destroy;
	
    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
            switch (Rnd.get(1, 4)) {
                case 1:
                    spawn(798223, 254f, 512f, 187.8f, (byte) 119);
                    break;
                case 2:
                    spawn(798223, 359f, 526f, 186.5f, (byte) 102);
                    break;
                case 3:
                    spawn(798223, 476f, 541f, 187.9f, (byte) 90);
                    break;
                case 4:
                    break;
            }
		Npc npc = instance.getNpc(214668);
        if (npc != null) {
            SkillEngine.getInstance().getSkill(npc, 18481, 1, npc).useNoAnimationSkill();
        }
    }
	
    @Override
    public void onDie(Npc npc) {
		
		switch (npc.getNpcId()) {
			case 280971:
			case 280972:
				destroy++;
                if (destroy == 2) {
					Npc boss = instance.getNpc(214668);
                    boss.getEffectController().removeEffect(18481);
                }
                break;
			case 214669:
				spawn(730168, 571f, 491f, 196.8f, (byte) 119);
                break;
        }
    }
	
    @Override
    public void onLeaveInstance(Player player) {
        removeItems(player);
    }
	
    private void removeItems(Player player) {
        Storage storage = player.getInventory();
        storage.decreaseByItemId(185000016, storage.getItemCountByItemId(185000016));
        storage.decreaseByItemId(185000017, storage.getItemCountByItemId(185000017));
		storage.decreaseByItemId(185000018, storage.getItemCountByItemId(185000018));
        storage.decreaseByItemId(185000019, storage.getItemCountByItemId(185000019));
		storage.decreaseByItemId(185000020, storage.getItemCountByItemId(185000020));
        storage.decreaseByItemId(185000021, storage.getItemCountByItemId(185000021));
		storage.decreaseByItemId(185000022, storage.getItemCountByItemId(185000022));
		storage.decreaseByItemId(185000023, storage.getItemCountByItemId(185000023));
        storage.decreaseByItemId(185000024, storage.getItemCountByItemId(185000024));
		storage.decreaseByItemId(185000025, storage.getItemCountByItemId(185000025));
    }
	
    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
}
