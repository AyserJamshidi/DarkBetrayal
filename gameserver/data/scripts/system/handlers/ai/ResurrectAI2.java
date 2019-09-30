/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ne.gs.database.GDB;
import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AI2Request;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.configs.main.CustomConfig;
import com.ne.gs.database.dao.PlayerBindPointDAO;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.Race;
import com.ne.gs.model.TribeClass;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.PersistentState;
import com.ne.gs.model.gameobjects.player.BindPointPosition;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.BindPointTemplate;
import com.ne.gs.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.utils.MathUtil;
import com.ne.gs.utils.PacketSendUtility;
import com.ne.gs.world.WorldType;

/**
 * @author ATracer
 */
@AIName("resurrect")
public class ResurrectAI2 extends NpcAI2 {

    private static final Logger log = LoggerFactory.getLogger(ResurrectAI2.class);

    @Override
    protected void handleDialogStart(Player player) {
        BindPointTemplate bindPointTemplate = DataManager.BIND_POINT_DATA.getBindPointTemplate(getNpcId());
        Race race = player.getRace();
        if (bindPointTemplate == null) {
            log.info("There is no bind point template for npc: " + getNpcId());
            return;
        }

        if (player.getBindPoint() != null
            && player.getBindPoint().getMapId() == getPosition().getMapId()
            && MathUtil.getDistance(player.getBindPoint().getX(), player.getBindPoint().getY(), player.getBindPoint().getZ(), getPosition().getX(),
            getPosition().getY(), getPosition().getZ()) < 20) {
            player.sendPck(SM_SYSTEM_MESSAGE.STR_ALREADY_REGISTER_THIS_RESURRECT_POINT);
            return;
        }

        WorldType worldType = player.getWorldType();
        if (!CustomConfig.ENABLE_CROSS_FACTION_BINDING && !getTribe().equals(TribeClass.FIELD_OBJECT_ALL)) {
            if ((!getRace().equals(Race.NONE) && !getRace().equals(race)) || (race.equals(Race.ASMODIANS) && getTribe().equals(TribeClass.FIELD_OBJECT_LIGHT))
                || (race.equals(Race.ELYOS) && getTribe().equals(TribeClass.FIELD_OBJECT_DARK))) {
                player.sendPck(SM_SYSTEM_MESSAGE.STR_MSG_BINDSTONE_CANNOT_FOR_INVALID_RIGHT(player.getCommonData().getOppositeRace().toString()));
                return;
            }
        }
        if (worldType == WorldType.PRISON) {
            player.sendPck(SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
            return;
        }
        bindHere(player, bindPointTemplate);
    }

    private void bindHere(Player player, final BindPointTemplate bindPointTemplate) {

        String price = Integer.toString(bindPointTemplate.getPrice());
        AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_ASK_REGISTER_RESURRECT_POINT, 0, new AI2Request() {

            @Override
            public void acceptRequest(Creature requester, Player responder) {
                // check if this both creatures are in same world
                if (responder.getWorldId() == requester.getWorldId()) {
                    // check enough kinah
                    if (responder.getInventory().getKinah() < bindPointTemplate.getPrice()) {
                        responder.sendPck(SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_RESURRECT_POINT_NOT_ENOUGH_FEE);
                        return;
                    } else if (MathUtil.getDistance(requester, responder) > 5) {
                        responder.sendPck(SM_SYSTEM_MESSAGE.STR_CANNOT_REGISTER_RESURRECT_POINT_FAR_FROM_NPC);
                        return;
                    }

                    BindPointPosition old = responder.getBindPoint();
                    BindPointPosition bpp = new BindPointPosition(requester.getWorldId(), responder.getX(), responder.getY(), responder.getZ(), responder
                        .getHeading());
                    bpp.setPersistentState(old == null ? PersistentState.NEW : PersistentState.UPDATE_REQUIRED);
                    responder.setBindPoint(bpp);
                    if (GDB.get(PlayerBindPointDAO.class).store(responder)) {
                        responder.getInventory().decreaseKinah(bindPointTemplate.getPrice());
                        TeleportService.sendSetBindPoint(responder);
                        PacketSendUtility.broadcastPacket(responder, new SM_LEVEL_UPDATE(responder.getObjectId(), 2, responder.getCommonData().getLevel()),
                            true);
                        responder.sendPck(SM_SYSTEM_MESSAGE.STR_DEATH_REGISTER_RESURRECT_POINT(""));
                        old = null;
                    } else {
                        // if any errors happen, left that player with old bind point
                        responder.setBindPoint(old);
                    }
                }
            }
        }, price);
    }

}
