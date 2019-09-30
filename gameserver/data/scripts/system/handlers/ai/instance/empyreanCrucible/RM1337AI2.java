/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.empyreanCrucible;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.world.WorldPosition;

/**
 * @author Luzien
 */
@AIName("rm_1337")
public class RM1337AI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isHome = new AtomicBoolean(true);
    private final AtomicBoolean isEventStarted = new AtomicBoolean(false);
    private Future<?> task1, task2;

    @Override
    public void handleSpawned() {
        super.handleSpawned();
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500229, getObjectId(), 0, 2000);
    }

    @Override
    public void handleDespawned() {
        cancelTask();
        super.handleDespawned();
    }

    @Override
    public void handleDied() {
        cancelTask();
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500231, getObjectId(), 0, 0);
        super.handleDied();
    }

    @Override
    public void handleBackHome() {
        cancelTask();
        super.handleBackHome();
    }

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            startSkillTask1();
        }
        if (getLifeStats().getHpPercentage() <= 75) {
            if (isEventStarted.compareAndSet(false, true)) {
                startSkillTask2();
            }
        }
    }

    private void cancelTask() {
        if (task1 != null && !task1.isCancelled()) {
            task1.cancel(true);
        }
        if (task2 != null && !task2.isCancelled()) {
            task2.cancel(true);
        }
    }

    private void startSkillTask1() {
        task1 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelTask();
                } else {
                    if (getOwner().getCastingSkill() != null) {
                        return;
                    }
                    if (getLifeStats().getHpPercentage() <= 50) {
                        switch (Rnd.get(2)) {
                            case 0:
                                SkillEngine.getInstance().getSkill(getOwner(), 19550, 10, getTargetPlayer()).useNoAnimationSkill();
                                break;
                            default:
                                final Player target = getTargetPlayer();
                                SkillEngine.getInstance().getSkill(getOwner(), 19552, 10, target).useNoAnimationSkill();
                                ThreadPoolManager.getInstance().schedule(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (!isAlreadyDead()) {
                                            SkillEngine.getInstance().getSkill(getOwner(), 19553, 10, target).useNoAnimationSkill();
                                        }
                                    }
                                }, 4000);
                        }
                    } else {
                        SkillEngine.getInstance().getSkill(getOwner(), 19550, 10, getTargetPlayer()).useNoAnimationSkill();
                    }
                }
            }
        }, 10000, 23000);
    }

    private void startSkillTask2() {
        task2 = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelTask();
                } else {
                    getOwner().getController().cancelCurrentSkill();
                    NpcShoutsService.getInstance().sendMsg(getOwner(), 1500230, getObjectId(), 0, 0);
                    SkillEngine.getInstance().getSkill(getOwner(), 19551, 10, getTarget()).useNoAnimationSkill();
                    spawnSparks();
                }
            }
        }, 0, 60000);
    }

    private void spawnSparks() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    int count = Rnd.get(8, 12);
                    for (int i = 0; i < count; i++) {
                        rndSpawn(282373);
                    }

                }
            }
        }, 4000);
    }

    private Player getTargetPlayer() {
        List<Player> players = new ArrayList<>();
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 37)) {
                players.add(player);
            }
        }
        return !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
    }

    private void rndSpawn(int npcId) {
        float direction = Rnd.get(0, 180) / 100f;
        int distance = Rnd.get(3, 12);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
    }
}
