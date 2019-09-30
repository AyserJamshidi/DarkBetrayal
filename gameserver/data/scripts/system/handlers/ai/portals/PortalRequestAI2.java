/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.portals;

import com.ne.gs.ai2.AIName;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.DescId;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.gameobjects.player.RequestResponseHandler;
import com.ne.gs.model.templates.teleport.TelelocationTemplate;
import com.ne.gs.model.templates.teleport.TeleportLocation;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.ne.gs.services.teleport.TeleportService;
import com.ne.gs.services.trade.PricesService;

/**
 * @author xTz
 */
@AIName("portal_request")
public class PortalRequestAI2 extends PortalAI2 {

    @Override
    protected void handleUseItemFinish(final Player player) {
        if (teleportTemplate != null) {
            final TeleportLocation loc = teleportTemplate.getTeleLocIdData().getTelelocations().get(0);
            if (loc != null) {
                TelelocationTemplate locationTemplate = DataManager.TELELOCATION_DATA.getTelelocationTemplate(loc.getLocId());
                RequestResponseHandler portal = new RequestResponseHandler(player) {

                    @Override
                    public void acceptRequest(Creature requester, Player responder) {
                        TeleportService.teleport(teleportTemplate, loc.getLocId(), player, getOwner(), TeleportAnimation.JUMP_AIMATION);
                    }

                    @Override
                    public void denyRequest(Creature requester, Player responder) {
                        // Nothing Happens
                    }

                };
                long transportationPrice = PricesService.getPriceForService(loc.getPrice(), player.getRace());
                if (player.getResponseRequester().putRequest(160013, portal)) {
                    player.sendPck(new SM_QUESTION_WINDOW(160013, getObjectId(), 0, DescId.of(
                        locationTemplate.getNameId() * 2 + 1), transportationPrice));
                }
            }
        }
    }
}