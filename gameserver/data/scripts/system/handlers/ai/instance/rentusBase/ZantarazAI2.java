/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.rentusBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ai.AggressiveNpcAI2;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("zantaraz")
public class ZantarazAI2 extends AggressiveNpcAI2 {

    protected List<Integer> percents = new ArrayList<>();

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private synchronized void checkPercentage(int hpPercentage) {
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                percents.remove(percent);
                sp(282384);
                sp(282384);
                sp(282384);
                sp(282384);
                sp(217301);
                sp(217301);
                sp(217301);
                sp(217301);
                break;
            }
        }
    }

    private void sp(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(0, 2);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), p.getH());
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 75, 50, 25);
    }

    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }

    @Override
    protected void handleSpawned() {
        addPercent();
        super.handleSpawned();
    }

    @Override
    protected void handleBackHome() {
        addPercent();
        super.handleBackHome();
    }

    @Override
    protected void handleDespawned() {
        percents.clear();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        WorldMapInstance instance = getPosition().getWorldMapInstance();
        if (instance != null) {
            deleteNpcs(instance.getNpcs(282384));
            deleteNpcs(instance.getNpcs(217301));
            deleteNpcs(instance.getNpcs(218610));
            deleteNpcs(instance.getNpcs(218611));
        }
        percents.clear();
        super.handleDied();
    }

}
