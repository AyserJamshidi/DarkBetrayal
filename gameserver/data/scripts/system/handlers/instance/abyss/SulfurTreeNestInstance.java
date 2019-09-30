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
@InstanceID(300060000)
public class SulfurTreeNestInstance extends GeneralInstanceHandler {

    private boolean isStartTimer = false;
    private long startTime;
    private boolean isInstanceDestroyed = false;

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        spawnRings();
    }

    private void spawnRings() {
        FlyRing f1 = new FlyRing(new FlyRingTemplate("SULFUR_1", mapId, new Point3D(462.9394, 380.34888, 168.97256), new Point3D(462.9394, 380.34888,
            174.97256), new Point3D(468.9229, 380.7933, 168.97256), 6), instanceId);
        f1.spawn();
    }

    @Override
    public boolean onPassFlyingRing(Player player, String flyingRing) {
        if (flyingRing.equals("SULFUR_1")) {
            if (!isStartTimer) {
                isStartTimer = true;
                startTime = System.currentTimeMillis();
                player.sendPck(new SM_QUEST_ACTION(0, 900));
                ThreadPoolManager.getInstance().schedule(new Runnable() {

                    @Override
                    public void run() {
                        despawnNpcs(getNpcs(214804));
                        despawnNpcs(getNpcs(700463));
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
