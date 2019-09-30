/*
 * This file is part of Neon-Eleanor project
 *
 * This is proprietary software. See the EULA file distributed with
 * this project for additional information regarding copyright ownership.
 *
 * Copyright (c) 2011-2013, Neon-Eleanor Team. All rights reserved.
 */
package ai.instance.beshmundirTemple;

import com.ne.gs.ai2.AI2Actions;
import com.ne.gs.ai2.AI2Request;
import com.ne.gs.ai2.AIName;
import com.ne.gs.ai2.NpcAI2;
import com.ne.gs.dataholders.DataManager;
import com.ne.gs.model.DescId;
import com.ne.gs.model.gameobjects.Creature;
import com.ne.gs.model.gameobjects.player.Player;
import com.ne.gs.model.templates.portal.PortalPath;
import com.ne.gs.model.templates.portal.PortalUse;
import com.ne.gs.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.ne.gs.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.ne.gs.services.teleport.PortalService;

/**
 * @author Gigi, vlog
 */
@AIName("beshmundirswalk")//730231
public class BeshmundirsWalkAI2 extends NpcAI2 {

    @Override
    protected void handleDialogStart(Player player) {
        player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 10));
    }

    @Override
    public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
        AI2Request request = new AI2Request() {

            @Override
            public void acceptRequest(Creature requester, Player responder) {
                // TODO: create an instance, depending on difficulty level
                moveToInstance(responder);
            }
        };
        switch (dialogId) {
            case 60: { // I'm ready to enter
//                if (player.isInGroup2() && player.getPlayerGroup2().isLeader(player)) {
//                    player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 4762));
//                } else if (isAGroupMemberInInstance(player)) {
                    moveToInstance(player);
//                } else {
//                    player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 10));
//                }
                break;
            }
            case 4763: { // I'll take the safer path
                AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM, getObjectId(), request,
                    DescId.of(1804103));
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 4762));
                break;
            }
            case 4848: { // Give me the dangerous path
                AI2Actions.addRequest(this, player, SM_QUESTION_WINDOW.STR_INSTANCE_DUNGEON_WITH_DIFFICULTY_ENTER_CONFIRM, getObjectId(), request,
                    DescId.of(1804105));
                player.sendPck(new SM_DIALOG_WINDOW(getObjectId(), 4762));
                break;
            }
        }
        return true;
    }

    private boolean isAGroupMemberInInstance(Player player) {
        if (player.isInGroup2()) {
            for (Player member : player.getPlayerGroup2().getMembers()) {
                if (member.getWorldId() == 300170000) {
                    return true;
                }
            }
        }
        return false;
    }

    private void moveToInstance(Player player) {
        PortalUse portalUse = DataManager.PORTAL2_DATA.getPortalUse(getNpcId());
        if (portalUse != null) {
            PortalPath portalPath = portalUse.getPortalPath(player.getRace());
            if (portalPath != null) {
                PortalService.port(portalPath, player, getObjectId());
            }
        }
    }
}
