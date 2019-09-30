/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package instance;

import java.util.ArrayList;
import java.util.List;

import com.ne.gs.instance.handlers.GeneralInstanceHandler;
import com.ne.gs.instance.handlers.InstanceID;
import com.ne.gs.model.EmotionType;
import com.ne.gs.model.Race;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.Npc;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.items.storage.Storage;
import com.ne.gs.network.aion.serverpackets.SM_DIE;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_MOTION;
import com.ne.gs.network.aion.serverpackets.SM_PLAYER_INFO;
import com.ne.gs.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.network.aion.serverpackets.SM_TRANSFORM;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.skillengine.SkillEngine;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.World;
import com.ne.gs.world.WorldMap;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;
import com.ne.gs.world.knownlist.Visitor;

@InstanceID(300230000)
public class KromedesTrialInstance extends GeneralInstanceHandler {

    private int skillId;
    private final List<Integer> movies = new ArrayList<>();
    private boolean isSpawned = false;

    @Override
    public void onEnterInstance(Player player) {
        if (movies.contains(453)) {
            return;
        }
		skillId = player.getRace() == Race.ASMODIANS ? 19270 : 19220;
        sendMovie(player, 453);
    }

    @Override
    public void onLeaveInstance(Player player) {
        player.setTransformed(false);
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
		player.getEffectController().removeEffect(19288);
    }

    @Override
    public void onPlayerLogOut(Player player) {
        player.setTransformed(false);
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_TRANSFORM(player, false));
		player.getEffectController().removeEffect(19288);
    }
	
    @Override
    public void onDie(Npc npc) {
        switch (npc.getNpcId()) {
            case 216981:
				despawnNpcs(instance.getNpcs(700965));
                spawn(700939, 656.92f, 585.74f, 199.4f, (byte) 86);
                break;
			case 217030:
				Player player = npc.getAggroList().getMostPlayerDamage();
				SkillEngine.getInstance().applyEffectDirectly(19288, player, player, 0);
                npc.getKnownList().doOnAllPlayers(new Visitor<Player>() {
                    @Override
                    public void visit(Player player) {
						player.sendPck(new SM_SYSTEM_MESSAGE(1111307));
                    }
                });
                break;
			case 700835:
			case 217000:
			case 217002:
			case 216982:
				despawnNpc(npc);
                break;
        }
    }
	
    private void despawnNpcs(List<Npc> npcs) {
        for (Npc npc : npcs) {
            npc.getController().onDelete();
        }
    }
	
    private void despawnNpc(Npc npc) {
        if (npc != null) {
            npc.getController().onDelete();
        }
    }

    @Override
    public void onPlayMovieEnd(Player player, int movieId) {
        Storage storage = player.getInventory();
        switch (movieId) {
			case 453:
				SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
				break;
            case 454:
                Npc npc1 = getNpc(730308);
                if (npc1 != null && MathUtil.isIn3dRange(player, npc1, 20)) {
                    storage.decreaseByItemId(185000109, storage.getItemCountByItemId(185000109));
                    TeleportService.teleportTo(player, mapId, 687.56116f, 681.68225f, 200.28648f, (byte) 30);
                }
                break;
        }
    }

    @Override
    public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("MANOR_ENTRANCE_300230000")) {
            sendMovie(player, 462);
        } else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("KALIGA_TREASURY_300230000")) {
            {
                if (!isSpawned) {
                    isSpawned = true;
                    Npc npc1 = getNpc(217002);
                    Npc npc2 = getNpc(217000);
                    Npc npc3 = getNpc(216982);
                    if (isDead(npc1) && isDead(npc2) && isDead(npc3)) {
                        spawn(217005, 669.214f, 774.387f, 216.88f, (byte) 60);
                        spawn(217001, 663.8805f, 779.1967f, 216.26213f, (byte) 60);
                        spawn(217003, 663.0468f, 774.6116f, 216.26215f, (byte) 60);
                        spawn(217004, 663.0468f, 770.03815f, 216.26212f, (byte) 60);
                    } else {
                        spawn(217006, 669.214f, 774.387f, 216.88f, (byte) 60);
                    }
                }
            }
        }
    }

    private boolean isDead(Npc npc) {
        return (npc == null || npc.getLifeStats().isAlreadyDead());
    }

    private void sendMovie(Player player, int movie) {
        if (!movies.contains(movie)) {
            movies.add(movie);
            player.sendPck(new SM_PLAY_MOVIE(0, movie));
        }
    }

    @Override
    public void onInstanceDestroy() {
        movies.clear();
    }

    @Override
    public boolean onDie(Player player, Creature lastAttacker) {
        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
                true);

        player.sendPck(new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
        return true;
    }

    @Override
    public boolean onReviveEvent(Player player) {
        WorldMap map = World.getInstance().getWorldMap(player.getWorldId());
        if (map == null) {
            PlayerReviveService.bindRevive(player);
            return true;
        }
        PlayerReviveService.revive(player, 25, 25, true, 0);
        player.sendPck(SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
        player.getGameStats().updateStatsAndSpeedVisually();
        PacketSendUtility.sendPck(player, new SM_PLAYER_INFO(player, false));
        PacketSendUtility.sendPck(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
        TeleportService.teleportTo(player, player.getWorldId(), 687.56f, 681.68f, 200.28f);
        SkillEngine.getInstance().applyEffectDirectly(skillId, player, player, 0);
        player.unsetResPosState();
        return true;
    }
}
