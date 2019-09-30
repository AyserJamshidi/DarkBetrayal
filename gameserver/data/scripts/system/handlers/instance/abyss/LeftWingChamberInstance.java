/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.abyss;

import java.util.List;

import com.ne.commons.network.util.ThreadPoolManager;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.flyring.FlyRing;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.flyring.FlyRingTemplate;
import com.ne.gs.model.utils3d.Point3D;
import com.ne.gs.network.aion.serverpackets.SM_QUEST_ACTION;
import com.ne.gs.world.WorldMapInstance;

/**
 * @author xTz
 */
@InstanceID(300080000)
public class LeftWingChamberInstance extends GeneralInstanceHandler {

    private boolean isStartTimer = false;
    private long startTime;
    private boolean isInstanceDestroyed = false;

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        spawnRings();
    }

    private void spawnRings() {
        FlyRing f1 = new FlyRing(new FlyRingTemplate("LEFT_WING_1", mapId, new Point3D(576.2102, 585.4146, 353.90677), new Point3D(576.2102, 585.4146,
            359.90677), new Point3D(575.18384, 596.36664, 353.90677), 10), instanceId);
        f1.spawn();
    }

    @Override
    public boolean onPassFlyingRing(Player player, String flyingRing) {
        if (flyingRing.equals("LEFT_WING_1")) {
            if (!isStartTimer) {
                isStartTimer = true;
                startTime = System.currentTimeMillis();
                player.sendPck(new SM_QUEST_ACTION(0, 900));
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        despawnNpcs(getNpcs(700466));
                    }

                }, 900000);
            }
        }
        return false;
    }

    @Override
    public void onEnterInstance(Player player) {
        if (isStartTimer) {
            long time = System.currentTimeMillis() - startTime;
            if (time < 900000) {
                player.sendPck(new SM_QUEST_ACTION(0, 900 - (int) time / 1000));
            }
        }

    }

    private List<Npc> getNpcs(int npcId) {
        if (!isInstanceDestroyed) {
            return instance.getNpcs(npcId);
        }
        return null;
    }

    private void despawnNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            npc.getController().onDelete();
        }
    }

    @Override
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
    }
}
