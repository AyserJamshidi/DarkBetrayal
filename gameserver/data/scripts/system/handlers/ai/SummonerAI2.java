/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.ai.Percentage;
import com.ne.gs.model.ai.SummonGroup;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.VisibleObject;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.spawnengine.SpawnEngine;
import com.ne.gs.world.World;

/**
 * @author xTz
 */
@AIName("summoner")
public class SummonerAI2 extends AggressiveNpcAI2 {

    private final List<Integer> spawnedNpc = new ArrayList<>();

    private List<Percentage> percentage = Collections.emptyList();

    private int spawnedPercent = 0;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleDespawned() {
        super.handleDespawned();

        synchronized (spawnedNpc) {
            removeHelpersSpawn();
            spawnedNpc.clear();
        }

        percentage.clear();
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();

        synchronized (spawnedNpc) {
            removeHelpersSpawn();
            spawnedNpc.clear();
        }

        spawnedPercent = 0;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        percentage = DataManager.AI_DATA.getAiTemplate().get(getNpcId()).getSummons().getPercentage();
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        removeHelpersSpawn();
        spawnedNpc.clear();
        percentage.clear();
    }

    private void removeHelpersSpawn() {
        for (Integer object : spawnedNpc) {
            VisibleObject npc = World.getInstance().findVisibleObject(object);
            if (npc != null && npc.isSpawned()) {
                npc.getController().onDelete();
            }
        }
    }

    protected void addHelpersSpawn(int objId) {
        synchronized (spawnedNpc) {
            spawnedNpc.add(objId);
        }
    }

    private void checkPercentage(int hpPercentage) {
        for (Percentage percent : percentage) {
            if (spawnedPercent != 0 && spawnedPercent <= percent.getPercent()) {
                continue;
            }

            if (hpPercentage <= percent.getPercent()) {
                int skill = percent.getSkillId();
                if (skill != 0) {
                    AI2Actions.useSkill(this, skill);
                }

                if (percent.isIndividual()) {
                    handleIndividualSpawnedSummons(percent);
                } else if (percent.getSummons() != null) {
                    handleBeforeSpawn(percent);
                    for (SummonGroup summonGroup : percent.getSummons()) {
                        final SummonGroup sg = summonGroup;
                        ThreadPoolManager.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                spawnHelpers(sg);
                            }
                        }, summonGroup.getSchedule());

                    }
                }
                spawnedPercent = percent.getPercent();
            }
        }
    }

    protected void spawnHelpers(SummonGroup summonGroup) {
        if (!isAlreadyDead() && checkBeforeSpawn()) {
            int count = 0;
            if (summonGroup.getCount() != 0) {
                count = summonGroup.getCount();
            } else {
                count = Rnd.get(summonGroup.getMinCount(), summonGroup.getMaxCount());
            }
            for (int i = 0; i < count; i++) {
                SpawnTemplate summon = null;
                if (summonGroup.getDistance() != 0) {
                    summon = rndSpawnInRange(summonGroup.getNpcId(), summonGroup.getDistance());
                } else {
                    summon = SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), summonGroup.getNpcId(), summonGroup.getX(), summonGroup.getY(),
                        summonGroup.getZ(), summonGroup.getH());
                }
                VisibleObject npc = SpawnEngine.spawnObject(summon, getPosition().getInstanceId());
                addHelpersSpawn(npc.getObjectId());
            }
            handleSpawnFinished(summonGroup);
        }
    }

    protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
        float direction = Rnd.get(0, 199) / 100f;
        float x = (float) (Math.cos(Math.PI * direction) * distance);
        float y = (float) (Math.sin(Math.PI * direction) * distance);
        return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY() + y, getPosition().getZ(),
            getPosition().getH());
    }

    protected boolean checkBeforeSpawn() {
        return true;
    }

    protected void handleBeforeSpawn(Percentage percent) {
    }

    protected void handleSpawnFinished(SummonGroup summonGroup) {
    }

    protected void handleIndividualSpawnedSummons(Percentage percent) {
    }

}
