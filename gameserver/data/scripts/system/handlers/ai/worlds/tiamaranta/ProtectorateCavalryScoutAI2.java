/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.worlds.tiamaranta;

import com.ne.commons.utils.Rnd;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.ai2.manager.WalkManager;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.NpcShoutsService;
import com.ne.gs.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("protectorate_cavalry_scout")
public class ProtectorateCavalryScoutAI2 extends NpcAI2 {

    private int size;

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        spawnEventNpc();
    }

    private void spawnEventNpc() {
        size++;
        int npcId = 0;
        switch (Rnd.get(1, 3)) {
            case 1:
                npcId = 799991;
                break;
            case 2:
                npcId = 799992;
                break;
            case 3:
                npcId = 799993;
                break;
        }
        int msg = 0;
        switch (Rnd.get(1, 3)) {
            case 1:
                msg = 340937;
                break;
            case 2:
                msg = 340955;
                break;
            case 3:
                msg = 0;
                break;
        }
        Npc npc = (Npc) spawn(npcId, 131.34761f, 2770.1194f, 293.92636f, (byte) 100);
        npc.getSpawn().setWalkerId("6000300001");
        WalkManager.startWalking((NpcAI2) npc.getAi2());
        npc.setState(1);
        PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
        if (msg != 0) {
            NpcShoutsService.getInstance().sendMsg(npc, msg, npc.getObjectId(), 0, 2000);
        }

    }

    @Override
    protected void handleCreatureMoved(Creature creature) {
        if (creature instanceof Npc) {
            Npc npc = (Npc) creature;
            int npcId = npc.getNpcId();
            if (npcId == 799991 || npcId == 799992 || npcId == 799993) {
                int point = npc.getMoveController().getCurrentPoint();
                if (point == 4 && size < 2) {
                    spawnEventNpc();
                } else if (point == 0) {
                    npc.getMoveController().abortMove();
                    npc.getSpawn().setWalkerId(null);
                    WalkManager.stopWalking((NpcAI2) npc.getAi2());
                    CreatureActions.delete(npc);
                    size--;
                }
            }
        }
        super.handleCreatureMoved(creature);
    }

}
