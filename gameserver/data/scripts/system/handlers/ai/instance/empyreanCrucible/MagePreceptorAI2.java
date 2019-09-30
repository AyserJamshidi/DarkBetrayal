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
import java.util.Collections;
import java.util.List;
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
@AIName("mage_preceptor")
public class MagePreceptorAI2 extends AggressiveNpcAI2 {

    private final List<Integer> percents = new ArrayList<>();

    @Override
    public void handleSpawned() {
        super.handleSpawned();
        addPercents();
    }

    @Override
    public void handleDespawned() {
        percents.clear();
        despawnNpcs();
        super.handleDespawned();
    }

    @Override
    public void handleDied() {
        despawnNpcs();
        super.handleDied();
    }

    @Override
    public void handleBackHome() {
        addPercents();
        despawnNpcs();
        super.handleBackHome();
    }

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void startEvent(int percent) {
        if (percent == 50 || percent == 25) {
            SkillEngine.getInstance().getSkill(getOwner(), 19606, 10, getTarget()).useNoAnimationSkill();
        }

        switch (percent) {
            case 75:
                SkillEngine.getInstance().getSkill(getOwner(), 19605, 10, getTargetPlayer()).useNoAnimationSkill();
                break;
            case 50:
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        if (!isAlreadyDead()) {
                            SkillEngine.getInstance().getSkill(getOwner(), 19609, 10, getOwner()).useNoAnimationSkill();
                            ThreadPoolManager.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    WorldPosition p = getPosition();
                                    spawn(282364, p.getX(), p.getY(), p.getZ(), p.getH());
                                    spawn(282363, p.getX(), p.getY(), p.getZ(), p.getH());
                                    scheduleSkill(2000);
                                }

                            }, 4500);

                        }
                    }
                }, 3000);
                break;
            case 25:
                scheduleSkill(3000);
                scheduleSkill(9000);
                scheduleSkill(15000);
                break;
        }

    }

    private void scheduleSkill(int delay) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isAlreadyDead()) {
                    SkillEngine.getInstance().getSkill(getOwner(), 19605, 10, getTargetPlayer()).useNoAnimationSkill();

                }
            }
        }, delay);
    }

    private Player getTargetPlayer() {
        List<Player> players = new ArrayList<>();
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 37)) {
                players.add(player);
            }
        }
        return players.get(Rnd.get(players.size()));
    }

    private void checkPercentage(int percentage) {
        for (Integer percent : percents) {
            if (percentage <= percent) {
                percents.remove(percent);
                startEvent(percent);
                break;
            }
        }
    }

    private void addPercents() {
        percents.clear();
        Collections.addAll(percents, 75, 50, 25);
    }

    private void despawnNpcs() {
        despawnNpc(getPosition().getWorldMapInstance().getNpc(282364));
        despawnNpc(getPosition().getWorldMapInstance().getNpc(282363));
    }

    private void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }
}
