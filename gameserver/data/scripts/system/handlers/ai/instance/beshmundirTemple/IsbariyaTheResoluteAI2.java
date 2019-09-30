/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.beshmundirTemple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.*;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.commons.utils.Rnd;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.geometry.Point3D;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.model.EmotionType;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.spawnengine.SpawnEngine;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;

@AIName("isbariya")
public class IsbariyaTheResoluteAI2 extends AggressiveNpcAI2 {

    private int stage = 0;
    private int stage2 = 0;
    private int stage3 = 0;
    private final AtomicBoolean isStart = new AtomicBoolean(false);
    private final List<Point3D> soulLocations = new ArrayList<>();
    private Future<?> basicSkillTask;
    private Future<?> basicSoulTask;
    private Future<?> basicEnergyTask;
    private Future<?> basicForzeTask;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isStart.compareAndSet(false, true)) {
            sendMsg(1500082);
            getPosition().getWorldMapInstance().getDoors().get(535).setOpen(false);
            startBasicSkillTask();
        }
        checkPercentage(getLifeStats().getHpPercentage());
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        soulLocations.add(new Point3D(1580.5f, 1572.8f, 304.64f));
        soulLocations.add(new Point3D(1582.1f, 1571.2f, 304.64f));
        soulLocations.add(new Point3D(1583.3f, 1569.9f, 304.64f));
        soulLocations.add(new Point3D(1585.3f, 1568.1f, 304.64f));
        soulLocations.add(new Point3D(1586.4f, 1567.1f, 304.64f));
        soulLocations.add(new Point3D(1588.3f, 1566.2f, 304.64f));
    }

    @Override
    protected void handleDied() {
        super.handleDied();
        sendMsg(1500085);
        getPosition().getWorldMapInstance().getDoors().get(535).setOpen(true);
        cancelSkillTask();
        cancelSouls();
        cancelEnergy();
        cancelForze();
        despawn(281645);
        despawn(281659);
        despawn(281660);
        stage = 0;
        stage2 = 0;
        stage3 = 0;
    }

    @Override
    protected void handleBackHome() {
        super.handleBackHome();
        sendMsg(1500084);
        getPosition().getWorldMapInstance().getDoors().get(535).setOpen(true);
        cancelSkillTask();
        cancelSouls();
        cancelEnergy();
        cancelForze();
		despawn(281645);
        despawn(281659);
        despawn(281660);
        stage = 0;
        stage2 = 0;
        stage3 = 0;
        isStart.set(false);
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage <= 75) {
            stage++;
            if (stage == 1) {
                NpcShoutsService.getInstance().sendMsg(getOwner(), 1400459);
                souls();
            }
        }
        if (hpPercentage <= 50) {
            stage2++;
            if (stage2 == 1) {
                NpcShoutsService.getInstance().sendMsg(getOwner(), 1400460);
                cancelSouls();
                energy();
            }
        }
        if (hpPercentage <= 35) {
            stage3++;
            if (stage3 == 1) {
                NpcShoutsService.getInstance().sendMsg(getOwner(), 1400461);
                cancelEnergy();
                forze(this);
            }
        }
    }

    private void startBasicSkillTask() {
        basicSkillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelSkillTask();
                } else {
                    sendMsg(1500083);
                    switch (Rnd.get(1, 2)) {
                        case 1:
                            useSkill(18912);
                            NpcShoutsService.getInstance().sendMsg(getOwner(), 1400456);
                            break;
                        case 2:
                            useSkill(18913);
                            NpcShoutsService.getInstance().sendMsg(getOwner(), 1400457);
                            break;
                    }
                }
            }

        }, 5000, 28000);
    }

    private void cancelSkillTask() {
        if (basicSkillTask != null && !basicSkillTask.isCancelled()) {
            basicSkillTask.cancel(true);
        }
    }

    private void souls() {
        basicSoulTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelSouls();
                } else {
                    NpcShoutsService.getInstance().sendMsg(getOwner(), 1400462);
                    SkillEngine.getInstance().getSkill(getOwner(), 18959, 50, getTargetPlayer()).useNoAnimationSkill();
                    spawnSouls();
                }
            }

        }, 3000, 25000);
    }

    private void cancelSouls() {
        if (basicSoulTask != null && !basicSoulTask.isCancelled()) {
            basicSoulTask.cancel(true);
        }
    }

    private void energy() {
        basicEnergyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelEnergy();
                } else {
                    rndSpawn(281660, 5);
                }
            }

        }, 3000, 11000);
    }

    private void cancelEnergy() {
        if (basicEnergyTask != null && !basicEnergyTask.isCancelled()) {
            basicEnergyTask.cancel(true);
        }
    }

    private void forze(final NpcAI2 ai) {
        basicForzeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    cancelForze();
                } else {
                    rndSpawn(281659, 1);
                    AI2Actions.useSkill(ai, 18993);
                }
            }

        }, 3000, 20000);
    }

    private void cancelForze() {
        if (basicForzeTask != null && !basicForzeTask.isCancelled()) {
            basicForzeTask.cancel(true);
        }
    }

    private void rndSpawn(int npcId, int count) {
        for (int i = 0; i < count; i++) {
            SpawnTemplate template = rndSpawnInRange(npcId);
            SpawnEngine.spawnObject(template, getPosition().getInstanceId());
        }
    }

    private void spawnSouls() {
        List<Point3D> points = new ArrayList<>();
        points.addAll(soulLocations);
        int count = Rnd.get(3, 6);
        for (int i = 0; i < count; i++) {
            if (!points.isEmpty()) {
                Point3D spawn = points.remove(Rnd.get(points.size()));
                attackPlayer((Npc) spawn(281645, spawn.getX(), spawn.getY(), spawn.getZ(), (byte) 18));
            }
        }
    }
	
	private void attackPlayer(final Npc npc) {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                npc.setTarget(getTarget());
                ((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
                npc.setState(1);
                npc.getMoveController().moveToTargetObject();
                PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
            }
        }, 1000);
    }

    private Player getTargetPlayer() {
        List<Player> players = new ArrayList<>();
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (!CreatureActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 40) && player != getTarget()) {
                players.add(player);
            }
        }
        return !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
    }

    private SpawnTemplate rndSpawnInRange(int npcId) {
        float direction = Rnd.get(0, 199) / 100f;
        float x1 = (float) (Math.cos(Math.PI * direction) * 5);
        float y1 = (float) (Math.sin(Math.PI * direction) * 5);
        return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY() + y1, getPosition().getZ(),
            getPosition().getH());
    }
	
	private void despawn(int npcId) {
	  for (Npc npc : getPosition().getWorldMapInstance().getNpcs(npcId)) {
		 npc.getController().onDelete();
	  }
	}

    private void sendMsg(int msg) {
        NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
    }

    private void useSkill(int skillId){
        SkillEngine.getInstance().getSkill(getOwner(), skillId, 55, getTarget()).useSkill();
    }

}
