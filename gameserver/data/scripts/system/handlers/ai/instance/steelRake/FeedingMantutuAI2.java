/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.steelRake;

import ai.ShifterAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.world.WorldMapInstance;

@AIName("feeding_mantutu")
public class FeedingMantutuAI2 extends ShifterAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        WorldMapInstance instance = getPosition().getWorldMapInstance();
        if (instance.getNpc(281128) == null && instance.getNpc(281129) == null) {
            super.handleDialogStart(player);
        }
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        super.handleUseItemFinish(player);
        Npc boss = getPosition().getWorldMapInstance().getNpc(215079);
		Npc boss2 = getPosition().getWorldMapInstance().getNpc(219033);
        if (boss != null && boss.isSpawned() && !CreatureActions.isAlreadyDead(boss) || boss2 != null && boss2.isSpawned() && !CreatureActions.isAlreadyDead(boss2)) {
            Npc npc = null;
            switch (getNpcId()) {
                case 701387: // water supply
                    npc = (Npc) spawn(281129, 712.042f, 490.5559f, 939.7027f, (byte) 0);
                    break;
                case 701386: // feed supply
                    npc = (Npc) spawn(281128, 714.62634f, 504.4552f, 939.60675f, (byte) 0);
                    break;
            }
            boss.getAi2().onCustomEvent(1, npc);
            AI2Actions.deleteOwner(this);
        }
    }

}
