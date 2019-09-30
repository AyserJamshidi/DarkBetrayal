/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.elementisForest;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

@AIName("tuali")
public class TualiAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isStart = new AtomicBoolean(false);
	private int stage = 0;
	private int stage2 = 0;
	private int stage3 = 0;
    private Future<?> task;

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isStart.compareAndSet(false, true)) {
            NpcShoutsService.getInstance().sendMsg(getOwner(), 1500454, getObjectId(), true, 0, 0);
            scheduleSkills();

            task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    if (!isAlreadyDead()) {
                        SkillEngine.getInstance().getSkill(getOwner(), 19348, 60, getOwner()).useNoAnimationSkill();
                        int size = getPosition().getWorldMapInstance().getNpcs(282308).size();
                        for (int i = 0; i < 6; i++) {
                            if (size >= 12) {
                                break;
                            }
                            size++;
                            rndSpawn(282307);
                        }
                        NpcShoutsService.getInstance().sendMsg(getOwner(), 1401378, 6000);
                    }
                }

            }, 20000, 50000);
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }
	
	private void scheduleSkills() {
        if (isAlreadyDead() || !isStart.get()) {
            return;
        }
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
				AI2Actions.useSkill(TualiAI2.this, 19512 + Rnd.get(5));
                scheduleSkills();
            }
        }, Rnd.get(18, 22) * 1000);
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 65) {
			stage++;
            if (stage == 1) {
                buff();
            }
        }
        if (hpPercentage <= 45) {
			stage2++;
            if (stage2 == 1) {
                buff();
            }
        }
        if (hpPercentage <= 25) {
			stage3++;
            if (stage3 == 1) {
                buff();
            }
        }
    }

    private void buff() {
        SkillEngine.getInstance().getSkill(getOwner(), 19511, 60, getOwner()).useNoAnimationSkill();
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500456, getObjectId(), true, 0, 0);
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1401041, 3500);
    }

    private void rndSpawn(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        int distance = Rnd.get(5, 12);
        float x1 = (float) (Math.cos(Math.PI * direction) * distance);
        float y1 = (float) (Math.sin(Math.PI * direction) * distance);
        WorldPosition p = getPosition();
        spawn(npcId, p.getX() + x1, p.getY() + y1, p.getZ(), (byte) 0);
    }
	
	private void cancelTask() {
        if (task != null && !task.isDone()) {
            task.cancel(true);
        }
    }


    @Override
    public void handleDied() {
        cancelTask();
		stage = 0;
		stage2 = 0;
		stage3 = 0;
        super.handleDied();
        NpcShoutsService.getInstance().sendMsg(getOwner(), 1500457, getObjectId(), true, 0, 0);
    }

    @Override
    protected void handleDespawned() {
        cancelTask();
		stage = 0;
		stage2 = 0;
		stage3 = 0;
        super.handleDespawned();
    }

    @Override
    public void handleBackHome() {
        cancelTask();
        WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(282307));
                deleteNpcs(instance.getNpcs(282308));
            }
        }
        super.handleBackHome();
		stage = 0;
		stage2 = 0;
		stage3 = 0;
        isStart.set(false);
    }

    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }
}
