/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.muadasTrencher;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.manager.EmoteManager;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.geometry.Point3D;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("empress_muada")
public class EmpressMuadaAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean startedEvent = new AtomicBoolean(false);
    private final AtomicBoolean isAggred = new AtomicBoolean(false);
    private final AtomicBoolean sandSquallEvent = new AtomicBoolean(false);
    private Future<?> phaseTask;
    private Future<?> enrageTask;
    private boolean think = true;

    @Override
    public boolean canThink() {
        return think;
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isAggred.compareAndSet(false, true)) {
            startEnrageTask();
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 75) {
            if (startedEvent.compareAndSet(false, true)) {
                startPhaseTask();
            }
        }
        if (hpPercentage <= 50) {
            if (startedEvent.compareAndSet(true, false)) {
                cancelPhaseTask();
                removeHelpers();
            }
        }
        if (hpPercentage <= 25) {
            if (sandSquallEvent.compareAndSet(false, true)) {
                think = false;
                EmoteManager.emoteStopAttacking(getOwner());
                SkillEngine.getInstance().getSkill(getOwner(), 20499, 2, getOwner()).useNoAnimationSkill();
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
						Creature mostHated = getAggroList().getMostHated();
						if (isAlreadyDead() || mostHated == null) {
                            SkillEngine.getInstance().getSkill(getOwner(), 19893, 2, getOwner()).useNoAnimationSkill();
                            spawn(282533, 523.1f, 541.1f, 106.7f, (byte) 0);
                            NpcShoutsService.getInstance().sendMsg(getOwner(), 1401300);
                            ThreadPoolManager.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    if (!isAlreadyDead()) {
                                        think = true;
                                        Creature creature = getAggroList().getMostHated();
                                        if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
                                            setStateIfNot(AIState.FIGHT);
                                            think();
                                        } else {
                                            getMoveController().abortMove();
                                            getOwner().setTarget(creature);
                                            getOwner().getGameStats().renewLastAttackTime();
                                            getOwner().getGameStats().renewLastAttackedTime();
                                            getOwner().getGameStats().renewLastChangeTargetTime();
                                            getOwner().getGameStats().renewLastSkillTime();
                                            setStateIfNot(AIState.FIGHT);
                                            handleMoveValidate();
                                            SkillEngine.getInstance().getSkill(getOwner(), 19897, 2, getOwner()).useNoAnimationSkill();
                                        }
                                    }
                                }

                            }, 20000);
                        }
                    }

                }, 3500);
            }
        }
    }

    private void startEnrageTask() {
        enrageTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    SkillEngine.getInstance().getSkill(getOwner(), 19859, 2, getOwner()).useNoAnimationSkill();
                    enrageTask = ThreadPoolManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            if (!isAlreadyDead()) {
                                SkillEngine.getInstance().getSkill(getOwner(), 20089, 2, getOwner()).useNoAnimationSkill();

                            }
                        }

                    }, 15000);
                }
            }

        }, 1200000); // 20 min
    }

    private void startPhaseTask() {
        phaseTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelPhaseTask();
                } else {
                    SkillEngine.getInstance().getSkill(getOwner(), 19898, 2, getOwner()).useNoAnimationSkill();
                    for (int i = 0; i < 3; i++) {
                        NpcShoutsService.getInstance().sendMsg(getOwner(), 1401299);
                        Point3D p = getRndPos();
                        spawn(282556, p.getX(), p.getY(), p.getZ(), (byte) 0);
                        Npc npc1 = (Npc) spawn(282535, p.getX() + Rnd.get(-3, 3), p.getY() + Rnd.get(-3, 3), p.getZ(), (byte) 0);
                        Npc npc2 = (Npc) spawn(282535, p.getX() + Rnd.get(-3, 3), p.getY() + Rnd.get(-3, 3), p.getZ(), (byte) 0);
                        NpcShoutsService.getInstance().sendMsg(npc1, 1500307, npc1.getObjectId(), 0, 1000);
                        NpcShoutsService.getInstance().sendMsg(npc2, 1500307, npc2.getObjectId(), 0, 1000);
                    }
                }
            }

        }, 1000, 45000);
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}

    private void removeHelpers() {
		despawn(282556);
		despawn(282535);
		despawn(282539);
    }

    private Point3D getRndPos() {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(2, 5);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        return new Point3D(p.getX() + x1, p.getY() + y1, p.getZ());
    }

    private void cancelPhaseTask() {
        if (phaseTask != null && !phaseTask.isDone()) {
            phaseTask.cancel(true);
        }
    }

    private void cancelEnrageTask() {
        if (enrageTask != null && !enrageTask.isDone()) {
            enrageTask.cancel(true);
        }
    }

    @Override
    protected void handleBackHome() {
        cancelEnrageTask();
        cancelPhaseTask();
        getEffectController().removeEffect(19859);
        removeHelpers();
        think = true;
        isAggred.set(false);
        sandSquallEvent.set(false);
        startedEvent.set(false);
        super.handleBackHome();
    }

    @Override
    protected void handleDespawned() {
        cancelEnrageTask();
        cancelPhaseTask();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        removeHelpers();
        cancelEnrageTask();
        cancelPhaseTask();
        super.handleDied();
    }

}
