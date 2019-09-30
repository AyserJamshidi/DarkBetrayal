/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.kromedesTrial;

import ai.AggressiveNpcAI2;

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
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;


@AIName("кaliga_hard")
public class KaligaHardAI2 extends AggressiveNpcAI2 {

    private int curentPercent = 100;
	private final List<Integer> percents = new ArrayList<>();
	private int stage = 0;
	
	@Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
		stage++;
		if (stage == 1) {
			SkillEngine.getInstance().getSkill(getOwner(), 19870, 38, getOwner()).useSkill();
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				
				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), 19247, 38, getOwner()).useSkill();
					SkillEngine.getInstance().getSkill(getOwner(), 19248, 38, getOwner()).useSkill();
				}
			}, 2000);
		}
    }
	
    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        addPercent();
    }
	
    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 50);
    }
	
    @Override
    public void handleBackHome() {
        super.handleBackHome();
		stage = 0;
        getEffectController().removeEffect(19247);
        getEffectController().removeEffect(19248);
		addPercent();
		curentPercent = 100;
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
				deleteNpcs(instance.getNpcs(217111));
            }
        }
    }
	
    @Override
    protected void handleDespawned() {
		percents.clear();
        super.handleDespawned();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
				deleteNpcs(instance.getNpcs(217111));
            }
        }
    }
	
    @Override
    public void handleDied() {
        super.handleDied();
        getEffectController().removeEffect(19247);
        getEffectController().removeEffect(19248);
		percents.clear();
		WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
				deleteNpcs(instance.getNpcs(217111));
            }
        }
    }
	
	private synchronized void checkPercentage(int hpPercentage) {
        curentPercent = hpPercentage;
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 50:
						SpawnHelpers(217111);
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }
	
    private void SpawnHelpers(int npcId) {
		WorldPosition p = getPosition();
		attackPlayer((Npc) spawn(npcId, p.getX() + Rnd.get(-7, 7), p.getY() + Rnd.get(-7, 7), p.getZ(), (byte) 0));
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
}
