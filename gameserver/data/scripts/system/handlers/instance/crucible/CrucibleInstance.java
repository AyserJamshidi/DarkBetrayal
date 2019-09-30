/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.crucible;

import java.util.List;

import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.instance.StageType;
import com.ne.gs.model.instance.instancereward.InstanceReward;
import com.ne.gs.model.instance.playerreward.CruciblePlayerReward;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author xTz
 */
@SuppressWarnings("rawtypes")
public class CrucibleInstance extends GeneralInstanceHandler {

    protected boolean isInstanceDestroyed = false;
    protected StageType stageType = StageType.DEFAULT;
    protected InstanceReward instanceReward;

    @Override
    public void onEnterInstance(Player player) {
        if (!instanceReward.containPlayer(player.getObjectId())) {
            addPlayerReward(player);
        } else {
            getPlayerReward(player.getObjectId()).setPlayer(player);
        }
    }

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        instanceReward = new InstanceReward(mapId, instanceId);
    }

    @SuppressWarnings("unchecked")
    protected void addPlayerReward(Player player) {
        instanceReward.addPlayerReward(new CruciblePlayerReward(player));
    }

    protected CruciblePlayerReward getPlayerReward(Integer object) {
        return (CruciblePlayerReward) instanceReward.getPlayerReward(object);
    }

    @Override
    public InstanceReward<?> getInstanceReward() {
        return instanceReward;
    }

    protected List<Npc> getNpcs(int npcId) {
        if (!isInstanceDestroyed) {
            return instance.getNpcs(npcId);
        }
        return null;
    }

    protected boolean isInZone(ZoneName zone, Player player) {
        return player.isInsideZone(zone);
    }

    protected void sendMsg(int msg, int Obj, int color) {
        sendMsg(msg, Obj, false, color);
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(false, false, 0, 8));
        return true;
    }

    protected void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    protected void despawnNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            npc.getController().onDelete();
        }
    }

    protected void teleport(Player player, float x, float y, float z, byte h) {
        TeleportService.teleportTo(player, mapId, instanceId, x, y, z, h);
    }

    @Override
    public StageType getStage() {
        return stageType;
    }

    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 100, 100, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        return true;
    }

    @Override
    public void onInstanceDestroy() {
        isInstanceDestroyed = true;
        instanceReward.clear();
    }
}
