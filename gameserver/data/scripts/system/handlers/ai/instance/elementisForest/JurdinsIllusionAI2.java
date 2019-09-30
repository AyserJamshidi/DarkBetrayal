/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.elementisForest;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.GeneralNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("jurdins_illusion")
public class JurdinsIllusionAI2 extends GeneralNpcAI2 {

    private final AtomicBoolean isSpawned = new AtomicBoolean(false);

    @Override
    protected void handleDialogFinish(Player player) {
        if (isSpawned.compareAndSet(false, true)) {
            WorldPosition p = getPosition();
            final int instanceId = p.getInstanceId();
            final int worldId = p.getMapId();
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    ThreadPoolManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            spawn(worldId, 217238, 472.989f, 798.109f, 130.072f, (byte) 90, 0, instanceId);
                            Npc smoke = (Npc) spawn(282465, 472.989f, 798.109f, 130.072f, (byte) 0);
                            CreatureActions.delete(smoke);
                        }

                    }, 4000);
                    AI2Actions.deleteOwner(JurdinsIllusionAI2.this);
                }

            }, 3000);
        }
        super.handleDialogFinish(player);
    }
}
