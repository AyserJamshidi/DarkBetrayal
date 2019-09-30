/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.actions.CreatureActions;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldPosition;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;

/**
 * @author Luzien
 */
@InstanceID(300260000)
public class ElementisForestInstance extends GeneralInstanceHandler {

    private byte spawned = 0;

    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
            case 217233:
                spawn(700999, 303.07858f, 768.25012f, 204.34013f, (byte) 7);
                deleteNpc(700998);
                deleteNpc(282362);
                deleteNpc(217233);
				deleteNpc(282260);
                break;
            case 217238:
                spawn(282204, 472.9886f, 798.10944f, 129.94006f, (byte) 90);
                sendJurdinDialog();
                break;
            case 217234:
                spawn(730378, 574.359f, 429.351f, 125.533f, (byte) 0, 82);
                break;
			case 282429:
			case 282430:
                npc.getController().onDelete();
                break;
        }
    }

    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
        if (zone.getAreaTemplate().getZoneName() == ZoneName.get("CANYONGUARDS_RAVINE_300260000")) {
            if (spawned == 0) {
                spawn(217233, 301.77118f, 765.36951f, 193.03818f, (byte) 90);
                spawned++;
            }
        } else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("JURDINS_DOMAIN_300260000")) {
            if (spawned == 1) {
                sendMsg(1500242);
                spawned++;
            }
        }
    }

    @Override
    public void handleUseItemFinish(Player player, Npc npc) {
        switch (npc.getNpcId()) {
            case 282440:
                SkillEngine.getInstance().getSkill(npc, 19402, 60, player).useNoAnimationSkill();
                npc.getController().onDelete();
                break;
            case 799637:
            case 799639:
            case 799641:
            case 799643:
            case 799645:
            case 799647:
                SkillEngine.getInstance().getSkill(npc, 19692, 60, player).useNoAnimationSkill();
                npc.getController().onDelete();
                break;
            case 282308:
                SkillEngine.getInstance().getSkill(npc, 19517, 40, player).useNoAnimationSkill();
                WorldPosition p = npc.getPosition();
                if (p != null && p.getWorldMapInstance() != null) {
                    spawn(282441, p.getX(), p.getY(), p.getZ(), p.getH());
                    Npc smoke = (Npc) spawn(282465, p.getX(), p.getY(), p.getZ(), p.getH());
                    CreatureActions.delete(smoke);
                }
                CreatureActions.delete(npc);
                break;
        }
    }

    private void sendJurdinDialog() {
        sendMsg(1500243, getNpc(282204).getObjectId(), false, 0, 5000);
        sendMsg(1500244, getNpc(282204).getObjectId(), false, 0, 8000);
    }

    private void deleteNpc(int npcId) {
        if (getNpc(npcId) != null) {
            getNpc(npcId).getController().onDelete();
        }
    }
    
    @Override
    public boolean onReviveEvent(Player player) {
        PlayerReviveService.revive(player, 25, 25, false, 0);
        player.getGameStats().updateStatsAndSpeedVisually();
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        TeleportService.teleportTo(player, mapId, instanceId, 141.33386f, 651.8774f, 238.87491f, (byte) 0);
        return true;
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }
}
