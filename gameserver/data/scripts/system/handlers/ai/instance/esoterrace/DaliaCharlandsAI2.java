/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.esoterrace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ai.AggressiveNpcAI2;

import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("dalia_charlands")
public class DaliaCharlandsAI2 extends AggressiveNpcAI2 {

    private final List<Integer> percents = new ArrayList<>();

    @Override
    protected void handleSpawned() {
        addPercent();
        super.handleSpawned();
    }

    @Override
    protected void handleAttack(Creature creature) {
        super.handleAttack(creature);
        checkPercentage(getLifeStats().getHpPercentage());
    }

    private void checkPercentage(int hpPercentage) {
        if (hpPercentage > 80 && percents.size() < 3) {
            addPercent();
        }
        for (Integer percent : percents) {
            if (hpPercentage <= percent) {
                switch (percent) {
                    case 75:
                    case 50:
                    case 25:
                        spawnHelpers();
                        break;
                }
                percents.remove(percent);
                break;
            }
        }
    }

    private void addPercent() {
        percents.clear();
        Collections.addAll(percents, 75, 50, 25);
    }

    private void spawnHelpers() {
        startWalk((Npc) spawn(282177, 1173.68f, 674.11f, 297.5f, (byte) 0), "3002500001");
        startWalk((Npc) spawn(282176, 1174.44f, 669.64f, 297.5f, (byte) 0), "3002500002");
        startWalk((Npc) spawn(282178, 1176.2f, 677.32f, 297.5f, (byte) 0), "3002500003");
    }

    private void startWalk(Npc npc, String walkId) {
        npc.getSpawn().setWalkerId(walkId);
        WalkManager.startWalking((NpcAI2) npc.getAi2());
        npc.setState(1);
        PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
    }

    @Override
    protected void handleDespawned() {
        percents.clear();
        super.handleDespawned();
    }

    @Override
    protected void handleDied() {
        percents.clear();
        super.handleDied();
    }

    @Override
    protected void handleBackHome() {
        addPercent();
        super.handleBackHome();
    }

}
