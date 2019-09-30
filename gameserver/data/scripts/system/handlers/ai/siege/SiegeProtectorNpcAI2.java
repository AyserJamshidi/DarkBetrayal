/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Npc;

/**
 * @author ATracer, Source
 */
@AIName("siege_protector")
public class SiegeProtectorNpcAI2 extends SiegeNpcAI2 {

    //spawn for quest
    @Override
    protected void handleDied() {
        super.handleDied();
        int npc = getOwner().getNpcId();
        switch (npc) {
            case 259614:
                spawn(701237, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
                despawnClaw();
                break;
        }
    }

    private void despawnClaw() {
        final Npc claw = getPosition().getWorldMapInstance().getNpc(701237);
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                claw.getController().onDelete();
            }
        }, 60000 * 5);
    }
}
