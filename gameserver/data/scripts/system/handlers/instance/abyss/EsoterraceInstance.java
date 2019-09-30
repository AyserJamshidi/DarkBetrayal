/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance.abyss;

import java.util.Map;

import com.ne.commons.utils.Rnd;
import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.drop.DropItem;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.StaticDoor;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.drop.DropRegistrationService;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldMapInstance;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;
import java.util.Set;

/**
 * @author xTz, Gigi
 */
@InstanceID(300250000)
public class EsoterraceInstance extends GeneralInstanceHandler {

    private Map<Integer, StaticDoor> doors;

    @Override
    public void onInstanceCreate(WorldMapInstance instance) {
        super.onInstanceCreate(instance);
        doors = instance.getDoors();
        doors.get(367).setOpen(true);
        if (Rnd.chance(21)) {
            spawn(799580, 1034.11f, 985.01f, 327.35095f, (byte) 105);
        }
    }

    @Override
    public void onDie(Npc npc) {
        switch (npc.getObjectTemplate().getTemplateId()) {
            case 282295:
                openDoor(39);
                break;
            case 282291: // Surkana Feeder enables "hardmode"
                sendMsg(1400996);
                getNpc(217204).getController().onDelete();
                spawn(217205, 1315.43f, 1171.04f, 51.8054f, (byte) 66);
                break;
            case 217289:
                sendMsg(1400924);
                openDoor(122);
                break;
            case 217281:
                sendMsg(1400921);
                openDoor(70);
                break;
            case 217195:
                sendMsg(1400922);
                openDoor(45);
                openDoor(52);
                openDoor(67);
                spawn(701024, 751.513489f, 1136.021851f, 365.031158f, (byte) 60, 41);
                spawn(701024, 829.620789f, 1134.330078f, 365.031281f, (byte) 60, 77);
                break;
            case 217185:
                spawn(701023, 1264.862061f, 644.995178f, 296.831818f, (byte) 60, 112);
                doors.get(367).setOpen(false);
                break;
            case 217204:
                spawn(205437, 1309.390259f, 1163.644287f, 51.493992f, (byte) 13);
                spawn(701027, 1318.669800f, 1180.467651f, 52.879887f, (byte) 75, 727);
                break;
            case 217206:
                spawn(205437, 1309.390259f, 1163.644287f, 51.493992f, (byte) 13);
                spawn(701027, 1318.669800f, 1180.467651f, 52.879887f, (byte) 75, 727);
                spawn(701027, 1325.484497f, 1173.198486f, 52.879887f, (byte) 75, 726);
                break;
            case 217284:
            case 217283:
            case 217282:
                Npc npc1 = getNpc(217284);
                Npc npc2 = getNpc(217283);
                Npc npc3 = getNpc(217282);
                if (isDead(npc1) && isDead(npc2) && isDead(npc3)) {
                    sendMsg(1400920);
                    openDoor(111);
                }
                break;
            case 217649:
                spawn(701025, 1038.6369f, 987.7414f, 328.3564f, (byte) 0);
                break;
        }
    }
    
    @Override
    public void onDropRegistered(Npc npc) {
        Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
        int npcId = npc.getNpcId();
        Integer object = instance.getSoloPlayerObj();
        switch (npcId) {
            case 217185:
                dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 185000111, 1));
                break;
        }
    }

    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 384.57535f, 535.4073f, 321.6642f, (byte) 17);
        return true;
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }

    private boolean isDead(Npc npc) {
        return (npc == null || npc.getLifeStats().isAlreadyDead());
    }


    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("DRANA_PRODUCTION_LAB_300250000")) {
            player.sendPck(new SM_SYSTEM_MESSAGE(1400919));
        }
    }

    private void openDoor(int doorId) {
        StaticDoor door = doors.get(doorId);
        if (door != null) {
            door.setOpen(true);
        }
    }

    @Override
    public void onInstanceDestroy() {
        doors.clear();
    }

}
