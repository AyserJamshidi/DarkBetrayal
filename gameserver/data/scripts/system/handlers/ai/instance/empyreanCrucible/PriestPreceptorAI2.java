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
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.world.WorldPosition;

/**
 * @author Luzien
 */
@AIName("priest_preceptor")
public class PriestPreceptorAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean is75EventStarted = new AtomicBoolean(false);
    private final AtomicBoolean is25EventStarted = new AtomicBoolean(false);

    @Override
    public void handleSpawned() {
        super.handleSpawned();

        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                SkillEngine.getInstance().getSkill(getOwner(), 19612, 15, getOwner()).useNoAnimationSkill();
            }

        }, 1000);

    }

    @Override
    public void handleBackHome() {
        is75EventStarted.set(false);
        is25EventStarted.set(false);
        super.handleBackHome();
    }

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int percentage) {
        if (percentage <= 75) {
            if (is75EventStarted.compareAndSet(false, true)) {
                SkillEngine.getInstance().getSkill(getOwner(), 19611, 10, getTargetPlayer()).useNoAnimationSkill();
            }
        }
        if (percentage <= 25) {
            if (is25EventStarted.compareAndSet(false, true)) {
                startEvent();
            }
        }
    }

    private void startEvent() {
        SkillEngine.getInstance().getSkill(getOwner(), 19610, 10, getOwner()).useNoAnimationSkill();
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                SkillEngine.getInstance().getSkill(getOwner(), 19614, 10, getOwner()).useNoAnimationSkill();

                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        WorldPosition p = getPosition();
                        applySoulSickness((Npc) spawn(282366, p.getX(), p.getY(), p.getZ(), p.getH()));
                        applySoulSickness((Npc) spawn(282367, p.getX(), p.getY(), p.getZ(), p.getH()));
                        applySoulSickness((Npc) spawn(282368, p.getX(), p.getY(), p.getZ(), p.getH()));
                    }
                }, 5000);
            }

        }, 2000);
    }

    private Player getTargetPlayer() {
        List<Player> players = new ArrayList<>();
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 25)) {
                players.add(player);
            }
        }
        return players.get(Rnd.get(players.size()));
    }

    private void applySoulSickness(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                npc.getLifeStats().setCurrentHpPercent(50); // TODO: remove this, fix max hp debuffs not reducing current hp properly
                SkillEngine.getInstance().getSkill(npc, 19594, 4, npc).useNoAnimationSkill();
            }

        }, 1000);
    }

}
