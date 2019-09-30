/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.empyreanCrucible;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.instance.StageType;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Luzien
 */
@AIName("spectral_warrior")
public class SpectralWarriorAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isDone = new AtomicBoolean(false);

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 50 && isDone.compareAndSet(false, true)) {
            getPosition().getWorldMapInstance().getInstanceHandler().onChangeStage(StageType.START_STAGE_6_ROUND_5);

            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    resurrectAllies();
                }

            }, 2000);
        }
    }

    private void resurrectAllies() {
        for (VisibleObject obj : getKnownList().getKnownObjects().values()) {
            if (obj instanceof Npc) {
                Npc npc = (Npc) obj;

                if (npc == null || CreatureActions.isAlreadyDead(npc)) {
                    continue;
                }

                switch (npc.getNpcId()) {
                    case 205413:
                        spawn(217576, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
                        CreatureActions.delete(npc);
                        break;
                    case 205414:
                        spawn(217577, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
                        CreatureActions.delete(npc);
                        break;
                }
            }
        }
    }
}
