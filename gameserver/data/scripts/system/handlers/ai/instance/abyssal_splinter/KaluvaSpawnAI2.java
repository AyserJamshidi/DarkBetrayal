/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.abyssal_splinter;

import java.util.concurrent.Future;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.world.WorldPosition;

@AIName("kaluvaspawn")
public class KaluvaSpawnAI2 extends NpcAI2 {

    private Future<?> task;

    @Override
    protected void handleDied() {
        super.handleDied();
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
        checkKaluva();
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        scheduleHatch();
    }

    private void checkKaluva() {
        Npc kaluva = getPosition().getWorldMapInstance().getNpc(216950);
        if (kaluva != null && !kaluva.getLifeStats().isAlreadyDead()) {
            kaluva.getEffectController().removeEffect(19152);
        }
        AI2Actions.deleteOwner(this);
    }

    private void scheduleHatch() {
        task = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    hatchAdds();
                    checkKaluva();
                }
            }

        }, 22000); // schedule hatch when debuff ends(20s)
    }

    private void hatchAdds() { // 4 different spawn-formations; See Powerwiki for more information
        WorldPosition p = getPosition();
        switch (Rnd.get(1, 4)) {
            case 1:
                spawn(281911, p.getX(), p.getY(), p.getZ(), p.getH());
                spawn(281911, p.getX(), p.getY(), p.getZ(), p.getH());
                break;
            case 2:
                for (int i = 0; i < 12; i++) {
                    spawn(281912, p.getX(), p.getY(), p.getZ(), p.getH());
                }
                break;
            case 3:
                spawn(282057, p.getX(), p.getY(), p.getZ(), p.getH());
                break;
            case 4:
                spawn(281911, p.getX(), p.getY(), p.getZ(), p.getH());
                spawn(281912, p.getX(), p.getY(), p.getZ(), p.getH());
                spawn(281912, p.getX(), p.getY(), p.getZ(), p.getH());
                spawn(281912, p.getX(), p.getY(), p.getZ(), p.getH());
                break;
        }
    }

}
