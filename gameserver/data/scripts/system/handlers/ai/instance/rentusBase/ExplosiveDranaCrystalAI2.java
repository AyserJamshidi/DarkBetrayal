/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.ActionItemNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.controllers.effect.EffectController;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("explosive_drana_crystal")
public class ExplosiveDranaCrystalAI2 extends ActionItemNpcAI2 {

    private final AtomicBoolean isUsed = new AtomicBoolean(false);
    private Future<?> lifeTask;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        startLifeTask();
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (isUsed.compareAndSet(false, true)) {
            WorldPosition p = getPosition();
            Npc boss = p.getWorldMapInstance().getNpc(217308);
            if (boss != null && !CreatureActions.isAlreadyDead(boss)) {
                EffectController ef = boss.getEffectController();
                if (ef.hasAbnormalEffect(19370)) {
                    ef.removeEffect(19370);
                } else if (ef.hasAbnormalEffect(19371)) {
                    ef.removeEffect(19371);
                } else if (ef.hasAbnormalEffect(19372)) {
                    ef.removeEffect(19372);
                }
            }
            Npc npc = (Npc) spawn(282530, p.getX(), p.getY(), p.getZ(), p.getH());
            Npc invisibleNpc = (Npc) spawn(282529, p.getX(), p.getY(), p.getZ(), p.getH());
            SkillEngine.getInstance().getSkill(npc, 19373, 60, npc).useNoAnimationSkill();
            SkillEngine.getInstance().getSkill(invisibleNpc, 19654, 60, invisibleNpc).useNoAnimationSkill();
            CreatureActions.delete(invisibleNpc);
            AI2Actions.deleteOwner(this);
        }
    }

    private void cancelLifeTask() {
        if (lifeTask != null && !lifeTask.isDone()) {
            lifeTask.cancel(true);
        }
    }

    private void startLifeTask() {
        lifeTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    AI2Actions.deleteOwner(null);
                }
            }

        }, 60000);
    }

    @Override
    protected void handleDied() {
        cancelLifeTask();
        super.handleDied();
    }

    @Override
    protected void handleDespawned() {
        cancelLifeTask();
        super.handleDespawned();
    }

}
