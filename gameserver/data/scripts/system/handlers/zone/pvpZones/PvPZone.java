/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package zone.pvpZones;

import com.ne.gs.model.EmotionType;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.network.aion.serverpackets.SM_EMOTION;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.player.PlayerReviveService;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.utils.ThreadPoolManager;
import com.ne.gs.world.knownlist.Visitor;
import com.ne.gs.world.zone.SiegeZoneInstance;
import com.ne.gs.world.zone.ZoneInstance;
import com.ne.gs.world.zone.ZoneName;
import com.ne.gs.world.zone.handler.AdvencedZoneHandler;

/**
 * @author MrPoke
 */
public abstract class PvPZone implements AdvencedZoneHandler {

    @Override
    public void onEnterZone(Creature player, ZoneInstance zone) {
    }

    @Override
    public void onLeaveZone(Creature player, ZoneInstance zone) {
    }

    @Override
    public boolean onDie(final Creature lastAttacker, Creature target, final ZoneInstance zone) {
        if (!(target instanceof Player)) {
            return false;
        }

        final Player player = (Player) target;

        PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()),
            true);
        if (zone instanceof SiegeZoneInstance) {
            ((SiegeZoneInstance) zone).doOnAllPlayers(new Visitor<Player>() {

                @Override
                public void visit(Player p) {
                    p.sendPck(SM_SYSTEM_MESSAGE.STR_PvPZONE_OUT_MESSAGE(player.getName()));
                }
            });

            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    PlayerReviveService.duelRevive(player);
                    doTeleport(player, zone.getZoneTemplate().getName());
                    player.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_PvPZONE_MY_DEATH_TO_B(lastAttacker.getName()));
                }
            }, 5000);
        }
        return true;
    }

    protected abstract void doTeleport(Player player, ZoneName zoneName);
}
