/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.portals;

import ai.ActionItemNpcAI2;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AIName;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.TeleportAnimation;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.portal.PortalPath;
import com.ne.gs.model.templates.portal.PortalUse;
import com.ne.gs.model.templates.teleport.TeleportLocation;
import com.ne.gs.model.templates.teleport.TeleporterTemplate;
import com.ne.gs.services.teleport.PortalService;
import com.ne.gs.services.teleport.TeleportService;

/**
 * @author xTz
 */
@AIName("portal")
public class PortalAI2 extends ActionItemNpcAI2 {

    protected TeleporterTemplate teleportTemplate;
    protected PortalUse portalUse;

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        return true;
    }

    @Override
    protected void handleSpawned() {
        super.handleSpawned();
        teleportTemplate = DataManager.TELEPORTER_DATA.getTeleporterTemplateByNpcId(getNpcId());
        portalUse = DataManager.PORTAL2_DATA.getPortalUse(getNpcId());
    }

    @Override
    protected void handleDialogStart(Player player) {
        AI2Actions.selectDialog(this, player, 0, -1);
        if (getTalkDelay() != 0) {
            super.handleDialogStart(player);
        } else {
            handleUseItemFinish(player);
        }
    }

    @Override
    protected void handleUseItemFinish(Player player) {
        if (portalUse != null) {
            PortalPath portalPath = portalUse.getPortalPath(player.getRace());
            if (portalPath != null) {
                PortalService.port(portalPath, player, getObjectId());
            }
        } else if (teleportTemplate != null) {
            TeleportLocation loc = teleportTemplate.getTeleLocIdData().getTelelocations().get(0);
            if (loc != null) {
                TeleportService.teleport(teleportTemplate, loc.getLocId(), player, getOwner(), TeleportAnimation.BEAM_ANIMATION);
            }
        }
    }

}
