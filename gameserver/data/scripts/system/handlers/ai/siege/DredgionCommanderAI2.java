/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.siege;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.utils.ThreadPoolManager;

/*
 * @author Luzien
 */
@AIName("dredgionCommander")
public class DredgionCommanderAI2 extends SiegeNpcAI2 {

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        scheduleOneShot();
    }

    private int getSkill() {
        switch (getNpcId()) {
            case 276649:
                return 17572;
            case 276871:
            case 276872:
                return 18411;
            case 258236:
                return 18428;
            default:
                return 0;
        }
    }

    private void scheduleOneShot() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (getSkill() != 0) {
                    if (getTarget() instanceof Npc) {
                        Npc target = (Npc) getTarget();
                        Race race = target.getRace();
                        if ((race.equals(Race.GCHIEF_DARK) || race.equals(Race.GCHIEF_LIGHT)) && !target.getLifeStats().isAlreadyDead()) {
                            AI2Actions.useSkill(DredgionCommanderAI2.this, getSkill());
                            getAggroList().addHate(target, 10000);
                        }
                    }
                    scheduleOneShot();
                }
            }
        }, 45 * 1000);
    }
}
