/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.pernon;

import javolution.util.FastMap;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.controllers.observer.GaleCycloneObserver;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.skillengine.SkillEngine;

/**
 * @author xTz
 */
@AIName("gale_cyclone")
public class GaleCycloneAI2 extends NpcAI2 {

    private final FastMap<Integer, GaleCycloneObserver> observed = new FastMap<Integer, GaleCycloneObserver>().shared();
    private boolean blocked;

    @Override
    protected void handleCreatureSee(Creature creature) {
        if (blocked) {
            return;
        }
        if (creature instanceof Player) {
            final Player player = (Player) creature;
            GaleCycloneObserver observer = new GaleCycloneObserver(player, getOwner()) {

                @Override
                public void onMove() {
                    if (!blocked) {
                        SkillEngine.getInstance().getSkill(getOwner(), 20528, 50, player).useNoAnimationSkill();
                    }
                }

            };
            player.getObserveController().addObserver(observer);
            observed.put(player.getObjectId(), observer);
        }
    }

    @Override
    protected void handleCreatureNotSee(Creature creature) {
        if (blocked) {
            return;
        }
        if (creature instanceof Player) {
            Player player = (Player) creature;
            Integer obj = player.getObjectId();
            GaleCycloneObserver observer = observed.remove(obj);
            if (observer != null) {
                player.getObserveController().removeObserver(observer);
            }
        }
    }

    @Override
    protected void handleDied() {
        clear();
        super.handleDied();
    }

    @Override
    protected void handleDespawned() {
        clear();
        super.handleDespawned();
    }

    private void clear() {
        blocked = true;
        for (Integer obj : observed.keySet()) {
            Player player = getKnownList().getKnownPlayers().get(obj);
            GaleCycloneObserver observer = observed.remove(obj);
            if (player != null) {
                player.getObserveController().removeObserver(observer);
            }
        }
    }
}
