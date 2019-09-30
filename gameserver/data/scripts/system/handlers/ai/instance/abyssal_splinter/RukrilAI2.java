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
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.ThreadPoolManager;

/**
 * @author Ritsu, Luzien
 */
@AIName("rukril")
public class RukrilAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isHome = new AtomicBoolean(true);
    private Future<?> skillTask;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 95 && isHome.compareAndSet(true, false)) {
            startSkillTask();
        }
    }

    private void startSkillTask() {
        skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelTask();
                } else {
                    SkillEngine.getInstance().getSkill(getOwner(), 19266, 55, getOwner()).useNoAnimationSkill();
                    if (getPosition().getWorldMapInstance().getNpc(281907) == null) {
                        spawn(281907, 447.3828f, 675.9968f, 433.95636f, (byte) 19);
                        spawn(281907, 441.49512f, 680.38495f, 434.02753f, (byte) 19);
                    }
                }
            }
        }, 5000, 70000);
    }

    private void cancelTask() {
        if (skillTask != null && !skillTask.isCancelled()) {
            skillTask.cancel(true);
        }
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        cancelTask();
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        cancelTask();
        isHome.set(true);
        getEffectController().removeEffect(19266);
    }
}
