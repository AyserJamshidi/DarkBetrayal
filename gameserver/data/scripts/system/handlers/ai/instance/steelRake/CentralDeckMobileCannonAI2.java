/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.steelRake;

import java.util.List;
import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("centralcannon")
public class CentralDeckMobileCannonAI2 extends ActionItemNpcAI2 {

    @Override
    protected void handleUseItemFinish(Player player) {
        if (!player.getInventory().decreaseByItemId(185000052, 1)) {
            player.sendPck(new SM_SYSTEM_MESSAGE(1111302));
            return;
        }
        WorldPosition worldPosition = player.getPosition();

        if (worldPosition.isInstanceMap()) {
            if (worldPosition.getMapId() == 300100000) {
                WorldMapInstance worldMapInstance = worldPosition.getWorldMapInstance();
                // need check
                // getOwner().getController().useSkill(18572);

                killNpc(worldMapInstance.getNpcs(215402));
                killNpc(worldMapInstance.getNpcs(215403));
                killNpc(worldMapInstance.getNpcs(215404));
                killNpc(worldMapInstance.getNpcs(215405));
            }
        }
    }

    private void killNpc(List<Npc> npcs) {
        for (Npc npc : npcs) {
            AI2Actions.killSilently(this, npc);
        }
    }

}
