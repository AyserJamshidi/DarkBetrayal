/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.tallocsHollow;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import ai.AggressiveNpcAI2;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.templates.spawns.SpawnTemplate;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.WorldPosition;

/**
 * @author xTz
 */
@AIName("celestius")
public class CelestiusAI2 extends AggressiveNpcAI2 {

    private final AtomicBoolean isHome = new AtomicBoolean(true);
    private int start = 0;

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        if (isHome.compareAndSet(true, false)) {
            start = 1;
            startHelpersCall();

        }
    }

    private void startHelpersCall() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                Creature mostHated = getAggroList().getMostHated();
                if (isAlreadyDead() || mostHated == null) {
                    deleteHelpers();
                    start = 0;
                } else if (start == 1){
                    startHelpersCall();
                    deleteHelpers();
                    SkillEngine.getInstance().getSkill(getOwner(), 18981, 44, getOwner()).useNoAnimationSkill();
                    startRun((Npc) spawn(281514, 518, 813, 1378, (byte) 0), "3001900001");
                    startRun((Npc) spawn(281514, 551, 795, 1376, (byte) 0), "3001900002");
                    startRun((Npc) spawn(281514, 574, 854, 1375, (byte) 0), "3001900003");
                }
            }

        }, 25000);
    }

    private void startRun(Npc npc, String walkId) {
        npc.getSpawn().setWalkerId(walkId);
        WalkManager.startWalking((NpcAI2) npc.getAi2());
        npc.setState(1);
        PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
    }

    private void deleteHelpers() {
        WorldPosition p = getPosition();
        if (p != null) {
            WorldMapInstance instance = p.getWorldMapInstance();
            if (instance != null) {
                List<Npc> npcs = instance.getNpcs(281514);
                for (Npc npc : npcs) {
                    SpawnTemplate template = npc.getSpawn();
                    if (npc != null && (template.getX() == 518 || template.getX() == 551 || template.getX() == 574)) {
                        npc.getController().onDelete();
                    }
                }
            }
        }
    }

    @Override
    protected void handleBackHome() {
        deleteHelpers();
        isHome.set(true);
        start = 0;
        super.handleBackHome();
    }

    @Override
    protected void handleDespawned() {
        deleteHelpers();
        start = 0;
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        deleteHelpers();
        start = 0;
        super.handleDied();
    }

}
