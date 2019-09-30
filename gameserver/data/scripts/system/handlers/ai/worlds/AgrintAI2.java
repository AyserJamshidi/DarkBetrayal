/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds;

import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("agrint")
public class AgrintAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isSpawned = new AtomicBoolean(false);

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        int msg = 0;
        switch (getNpcId()) {
            case 218862:
            case 218850:
                msg = 1401246;
                break;
            case 218863:
            case 218851:
                msg = 1401247;
                break;
            case 218864:
            case 218852:
                msg = 1401248;
                break;
            case 218865:
            case 218853:
                msg = 1401249;
                break;
        }
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, 2000);
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 50) {
            if (isSpawned.compareAndSet(false, true)) {
                int npcId;
                switch (getNpcId()) {
                    case 218850:
                    case 218851:
                    case 218852:
                    case 218853:
                        npcId = getNpcId() + 320;
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        break;
                    case 218862:
                    case 218863:
                    case 218864:
                    case 218865:
                        npcId = getNpcId() + 308;
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        rndSpawnInRange(npcId, Rnd.get(1, 2));
                        break;
                }
            }
        }
    }

    private Npc rndSpawnInRange(int npcId, float distance) {
        float direction = Rnd.get(0, 199) / 100f;
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        return (Npc) spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
    }

    @Override
    protected void handleBackHome() {
        isSpawned.set(false);
        super.handleBackHome();
    }

    private void spawnChests(int npcId) {
        int count = Rnd.get(10, 18);
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));
        rndSpawnInRange(npcId, Rnd.get(1, 6));

        switch (count) {
            case 10:
                break;
            case 11:
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                break;
            case 12:
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                break;
            case 13:
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                break;
            case 14:
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                break;
            case 15:
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                break;
            case 16:
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                break;
            case 17:
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                break;
            case 18:
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                rndSpawnInRange(npcId, Rnd.get(1, 6));
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleDied() {
        switch (getNpcId()) {
            case 218850:
                spawnChests(218874);
                break;
            case 218851:
                spawnChests(218876);
                break;
            case 218852:
                spawnChests(218878);
                break;
            case 218853:
                spawnChests(218880);
                break;
            case 218862:
                spawnChests(218882);
                break;
            case 218863:
                spawnChests(218884);
                break;
            case 218864:
                spawnChests(218886);
                break;
            case 218865:
                spawnChests(218888);
                break;
        }
        super.handleDied();
    }

    @Override
    public int modifyOwnerDamage(int damage) {
        return 1;
    }

    @Override
    public int modifyDamage(int damage) {
        return 1;
    }

    @Override
    public int modifyReflectedDamage(int reflectedDamage) {
        return 1;
    }
}
