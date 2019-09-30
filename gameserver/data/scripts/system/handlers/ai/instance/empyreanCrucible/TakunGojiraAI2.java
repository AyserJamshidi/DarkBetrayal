/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.empyreanCrucible;

import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Npc;

/**
 * @author Luzien
 */
@AIName("takun_gojira")
public class TakunGojiraAI2 extends AggressiveNpcAI2 {

    private Npc counterpart;

    @Override
    public void handleSpawned() {
        super.handleSpawned();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                counterpart = getPosition().getWorldMapInstance().getNpc(getNpcId() == 217596 ? 217597 : 217596);
                if (counterpart != null) {
                    getAggroList().addHate(counterpart, 1000000);
                }
            }
        }, 500);
    }

}
