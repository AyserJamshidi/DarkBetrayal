/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.lowerUdas;

import ai.AggressiveNpcAI2;

import java.util.concurrent.Future;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.AIState;
import com.ne.gs.ai2.AbstractAI;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;


@AIName("bergrisar")
public class BergrisarAI2 extends AggressiveNpcAI2 {

    private int curentPercent = 100;
	private final List<Integer> percents = new ArrayList<>();
	private Future<?> series;

    @Override
    public void handleAttack(Creature creature) {
        super.handleAttack(creature);
		 checkPercentage(getLifeStats().getHpPercentage());
    }
	
    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 90, 50, 25);
    }
	
    @Override
    public void handleBackHome() {
        super.handleBackHome();
		addPercent();
		curentPercent = 100;
		cancelSeriesTask();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(281417));
            }
        }
    }
	
    @Override
    protected void handleDespawned() {
        super.handleDespawned();
		percents.clear();
		cancelSeriesTask();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(281417));
            }
        }
    }
	
    @Override
    public void handleDied() {
        super.handleDied();
		percents.clear();
		cancelSeriesTask();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                deleteNpcs(instance.getNpcs(281417));
            }
        }
    }
	
	private synchronized void checkPercentage(int hpPercentage) {
        curentPercent = hpPercentage;
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 90:
						startSeriesTask();
                        break;
					case 50:
						SpawnHelpers(281417);
                        break;
					case 25:
						SpawnHelpers(281417);
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }
	
    private void startSeriesTask() {
        series = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (isAlreadyDead()) {
                    cancelSeriesTask();
                } else {
					useSkill(18647);
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							useSkill(18643);
						}
					}, 1800);
                }
            }

        }, 20000, 40000);
    }
	
    private void SpawnHelpers(int npcId) {
		WorldPosition p = getPosition();
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
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
	
    private void deleteNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            if (npc != null) {
                npc.getController().onDelete();
            }
        }
    }
	
	private void useSkill(int skillId){
		SkillEngine.getInstance().getSkill(getOwner(), skillId, 53, getTarget()).useSkill();
	}
	
    private void cancelSeriesTask() {
        if (series != null && !series.isCancelled()) {
            series.cancel(true);
        }
    }
}
